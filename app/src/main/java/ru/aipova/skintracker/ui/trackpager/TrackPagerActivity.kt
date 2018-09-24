package ru.aipova.skintracker.ui.trackpager

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.view.GravityCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.animation.OvershootInterpolator
import kotlinx.android.synthetic.main.track_pager_activity.*
import kotlinx.android.synthetic.main.track_pager_content.*
import org.joda.time.LocalDate
import ru.aipova.skintracker.InjectionStub
import ru.aipova.skintracker.R
import ru.aipova.skintracker.ui.statistics.StatisticsActivity
import ru.aipova.skintracker.ui.trackpager.dialog.NoteCreateDialog
import ru.aipova.skintracker.ui.trackpager.dialog.NoteEditDialog
import ru.aipova.skintracker.ui.tracktype.TrackTypeActivity
import ru.aipova.skintracker.ui.trackvalues.TrackValuesActivity
import ru.aipova.skintracker.utils.TimeUtils
import ru.aipova.skintracker.utils.TransitionUtils
import java.io.File
import java.util.*


class TrackPagerActivity :
    AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    TrackPagerFragment.Callbacks,
    NoteCreateDialog.Callbacks,
    NoteEditDialog.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TransitionUtils.enableTransitions(window)
        setContentView(R.layout.track_pager_activity)
        setSupportActionBar(toolbar)

        setupNavigation()

        val currentPage = savedInstanceState?.getInt(CURRENT_ITEM) ?: getTodaysPage()
        setupTrackPager(currentPage)

        setupNavigationButtons()
        setupMenuFab()
    }


    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        outState?.putInt(CURRENT_ITEM, getCurrentPage())
        super.onSaveInstanceState(outState, outPersistentState)
    }

    private fun setupNavigationButtons() {
        leftBtn.setOnClickListener { trackPager.currentItem-- }
        rightBtn.setOnClickListener { trackPager.currentItem++ }
        calendarBtn.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val currentDate = TimeUtils.getCalendarForPosition(getCurrentPage())
        DatePickerDialog(
            this,
            dateChangedListener,
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private val dateChangedListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val newDate = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
            trackPager.currentItem =
                    TimeUtils.getPositionForDate(LocalDate.fromCalendarFields(newDate))
        }

    private fun getCurrentDiaryDate() = TimeUtils.getDateForPosition(getCurrentPage())

    private fun getCurrentPage() = trackPager.currentItem

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if ((requestCode == EDIT_REQUEST || requestCode == PHOTO_REQUEST) && resultCode == Activity.RESULT_OK) {
            viewPagerAdapter.notifyDataSetChanged()
        }
    }

    private fun setupNavigation() {
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun setupTrackPager(currentPage: Int) {
        trackPager.adapter = viewPagerAdapter
        trackPager.addOnPageChangeListener(pageChangeListener)
        setCurrentTrack(currentPage)
    }

    private fun setupMenuFab() {
        createMenuFabAnimation()
        fabAddParameters.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_params))
        fabAddParameters.setOnClickListener {
            menuFab.close(false)
            startActivityForResult(
                TrackValuesActivity.createIntent(
                    this,
                    getCurrentDiaryDate()
                ), EDIT_REQUEST
            )
        }
        fabAddNote.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_note))
        fabAddNote.setOnClickListener {
            menuFab.close(false)
            val track = InjectionStub.trackRepository.getTrackByDate(getCurrentDiaryDate())
            if (track?.note == null) {
                NoteCreateDialog.newInstance().show(supportFragmentManager, CREATE_NOTE_DIALOG)
            } else {
                NoteEditDialog.newInstance(track.note!!).show(supportFragmentManager, EDIT_NOTE_DIALOG)
            }

        }
        with(fabAddPhoto) {
            setImageDrawable(
                ContextCompat.getDrawable(
                    this@TrackPagerActivity,
                    R.drawable.ic_photo
                )
            )
            setOnClickListener {
                menuFab.close(false)
                makePhoto(getPhotoFile())
            }
        }
    }

    override fun onCreateNewNote(note: String) {
        saveNote(note)
    }

    override fun onEditNote(note: String) {
        saveNote(note)
    }

    fun saveNote(note: String) {
        InjectionStub.trackRepository.saveNote(
            getCurrentDiaryDate(),
            note
        ) { viewPagerAdapter.notifyDataSetChanged() }
    }

    private fun getPhotoFile(): File {
        return InjectionStub.photoFileConstructor.getForDate(getCurrentDiaryDate())
    }

    private fun makePhoto(photoFile: File) {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePhotoIntent.resolveActivity(packageManager) != null) {
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoUri(photoFile))
            startActivityForResult(takePhotoIntent, PHOTO_REQUEST)
        }
    }


    private fun getPhotoUri(photoFile: File): Uri? {
        return FileProvider.getUriForFile(
            this,
            "ru.aipova.skintracker.fileprovider",
            photoFile
        )
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_statistics -> {
                startActivityWithTransition(StatisticsActivity::class.java)
            }

            R.id.nav_track_type_settings -> {
                startActivityWithTransition(TrackTypeActivity::class.java)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun <T : Activity> startActivityWithTransition(activityClass: Class<T>) {
        startActivity(Intent(this, activityClass), TransitionUtils.makeTransition(this))
    }

    private val pageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                hideMenuFab()
            } else {
                showMenuFab()
            }
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            trackDateTxt.text = getFormattedDate(position)
        }
    }

    override fun onViewTouchDown() {
        hideMenuFab()
    }

    override fun onViewTouchUp() {
        showMenuFab()
    }

    private fun showMenuFab() {
        menuFab.visibility = View.VISIBLE
    }

    private fun hideMenuFab() {
        if (menuFab.isOpened) {
            menuFab.close(true)
        } else {
            menuFab.visibility = View.GONE
        }
    }

    private fun createMenuFabAnimation() {
        val scaleOutX = ObjectAnimator.ofFloat(menuFab.menuIconView, "scaleX", 1.0f, 0.2f)
        val scaleOutY = ObjectAnimator.ofFloat(menuFab.menuIconView, "scaleY", 1.0f, 0.2f)

        val scaleInX = ObjectAnimator.ofFloat(menuFab.menuIconView, "scaleX", 0.2f, 1.0f)
        val scaleInY = ObjectAnimator.ofFloat(menuFab.menuIconView, "scaleY", 0.2f, 1.0f)

        scaleOutX.duration = 50
        scaleOutY.duration = 50

        scaleInX.duration = 150
        scaleInY.duration = 150

        scaleInX.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                menuFab.menuIconView.setImageResource(
                    if (menuFab.isOpened)
                        R.drawable.ic_close
                    else
                        R.drawable.ic_edit
                )
            }
        })

        val set = AnimatorSet().apply {
            play(scaleOutX).with(scaleOutY)
            play(scaleInX).with(scaleInY).after(scaleOutX)
            interpolator = OvershootInterpolator(2f)
        }
        menuFab.iconToggleAnimatorSet = set
    }

    private val viewPagerAdapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
        override fun getItem(position: Int): Fragment {
            return TrackPagerFragment.getInstance(TimeUtils.getDateForPosition(position))
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun getCount(): Int {
            return TimeUtils.DAYS_COUNT
        }
    }

    private fun getFormattedDate(position: Int): String? {
        return TimeUtils.getDateFormatted(position)
    }

    private fun getCurrentDate(position: Int): Date {
        return TimeUtils.getDateForPosition(trackPager.currentItem)
    }


    private fun setCurrentTrack(currentPage: Int) {
        trackPager.currentItem = currentPage
    }

    private fun getTodaysPage() = TimeUtils.getPositionForDate(TimeUtils.today())

    companion object {
        private const val EDIT_REQUEST = 0
        private const val PHOTO_REQUEST = 1
        private const val CURRENT_ITEM = "ru.aipova.skintracker.trackpager.CURRENT_ITEM"
        private const val CREATE_NOTE_DIALOG = "NoteCreateDialog"
        private const val EDIT_NOTE_DIALOG = "NoteEditDialog"
    }
}