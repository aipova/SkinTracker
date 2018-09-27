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
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.OvershootInterpolator
import kotlinx.android.synthetic.main.track_pager_activity.*
import kotlinx.android.synthetic.main.track_pager_content.*
import ru.aipova.skintracker.InjectionStub
import ru.aipova.skintracker.R
import ru.aipova.skintracker.ui.data.TrackData
import ru.aipova.skintracker.ui.statistics.StatisticsActivity
import ru.aipova.skintracker.ui.trackpager.dialog.NoteCreateDialog
import ru.aipova.skintracker.ui.trackpager.dialog.NoteEditDialog
import ru.aipova.skintracker.ui.trackpager.track.TrackFragment
import ru.aipova.skintracker.ui.tracktype.TrackTypeActivity
import ru.aipova.skintracker.ui.trackvalues.TrackValuesActivity
import ru.aipova.skintracker.utils.TimeUtils
import java.io.File
import java.util.*


class TrackPagerActivity :
    AppCompatActivity(),
    TrackPagerContract.View,
    NavigationView.OnNavigationItemSelectedListener,
    TrackFragment.Callbacks,
    NoteCreateDialog.Callbacks,
    NoteEditDialog.Callbacks {
    override lateinit var presenter: TrackPagerContract.Presenter

    override fun isActive(): Boolean {
        return !isFinishing
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.track_pager_activity)
        setSupportActionBar(toolbar)
        presenter = TrackPagerPresenter(
            this,
            InjectionStub.trackRepository,
            InjectionStub.photoFileConstructor
        )

        setupNavigationDrawer()
        setupNavigationButtons()
        setupTrackPager()
        setupMenuFab()

        presenter.start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_diary, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_remove_day -> {
                showRemoveDayDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showRemoveDayDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.attention)
            .setMessage(getString(R.string.msg_remove_diary_day))
            .setPositiveButton(android.R.string.ok) { dialog, which -> presenter.onRemoveDaySelected() }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        dialog.show()
    }

    override fun showPhotoChooserDialog() {
        val dialog = AlertDialog.Builder(this)
            .setItems(R.array.photo_choose_array) { dialog, which ->
                when (which) {
                    0 -> presenter.onPhotoFromCameraSelected()
                    1 -> presenter.onPhotoFromGallerySelected()
                }
            }
            .create()
        dialog.show()
    }

    private fun setupNavigationButtons() {
        leftBtn.setOnClickListener { presenter.onLeftButtonClicked() }
        rightBtn.setOnClickListener { presenter.onRightButtonClicked() }
        calendarBtn.setOnClickListener { presenter.onCalendarButtonClicked() }
    }

    override fun showDatePickerDialog(calendar: Calendar) {
        DatePickerDialog(
            this,
            dateChangedListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private val dateChangedListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            presenter.onDateSelected(year, month, dayOfMonth)
        }

    override fun setCurrentPage(newPage: Int) {
        trackPager.currentItem = newPage
    }


    override fun getCurrentPage() = trackPager.currentItem

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                MAKE_PHOTO_REQUEST -> presenter.onPhotoCreated()
                EDIT_PARAMETERS_REQUEST -> presenter.onParametersUpdated()
                CHOOSE_PHOTO_REQUEST -> data?.run {
                    presenter.onPhotoChosen(contentResolver.openInputStream(getData()))
                }
            }
        }
    }


    private fun setupNavigationDrawer() {
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

    private fun setupTrackPager() {
        trackPager.adapter = viewPagerAdapter
        trackPager.addOnPageChangeListener(pageChangeListener)
    }

    private fun setupMenuFab() {
        createMenuFabAnimation()
        setupParametersItem()
        setupNoteItem()
        setupPhotoItem()
    }

    private fun setupPhotoItem() {
        with(fabAddPhoto) {
            setImageDrawable(getImage(R.drawable.ic_photo))
            setOnClickListener {
                closeMenuFab()
                presenter.onPhotoItemSelected()
            }
        }
    }

    private fun setupNoteItem() {
        with(fabAddNote) {
            setImageDrawable(getImage(R.drawable.ic_note))
            setOnClickListener {
                closeMenuFab()
                presenter.onNoteItemSelected()
            }
        }
    }

    override fun showNoteCreateDialog() {
        NoteCreateDialog.newInstance().show(supportFragmentManager, CREATE_NOTE_DIALOG)
    }

    override fun showNoteEditDialog(trackNote: String) {
        NoteEditDialog.newInstance(trackNote).show(supportFragmentManager, EDIT_NOTE_DIALOG)
    }

    private fun setupParametersItem() {
        with(fabAddParameters) {
            setImageDrawable(getImage(R.drawable.ic_params))
            setOnClickListener {
                closeMenuFab()
                presenter.onParametersItemSelected()
            }
        }
    }

    override fun openParametersScreen(date: Date) {
        val intent = TrackValuesActivity.createIntent(this@TrackPagerActivity, date)
        startActivityForResult(intent, EDIT_PARAMETERS_REQUEST)
    }

    private fun closeMenuFab() {
        menuFab.close(false)
    }

    private fun getImage(drawable: Int) = ContextCompat.getDrawable(this, drawable)

    override fun onCreateNewNote(note: String) {
        presenter.onCreateNewNote(note)
    }

    override fun onEditNote(note: String) {
        presenter.onEditNote(note)
    }

    override fun updateWholeView() {
        viewPagerAdapter.notifyDataSetChanged()
    }

    override fun openGallery() {
        val imageFromGalleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(imageFromGalleryIntent, CHOOSE_PHOTO_REQUEST)
    }

    override fun makePhoto(photoFile: File) {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePhotoIntent.resolveActivity(packageManager) != null) {
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoUri(photoFile))
            startActivityForResult(takePhotoIntent, MAKE_PHOTO_REQUEST)
        }
    }

    private fun getPhotoUri(photoFile: File): Uri? {
        return FileProvider.getUriForFile(
            this,
            FILE_PROVIDER,
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
            R.id.nav_statistics -> startActivityWithTransition(StatisticsActivity::class.java)
            R.id.nav_track_type_settings -> startActivityWithTransition(TrackTypeActivity::class.java)
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun <T : Activity> startActivityWithTransition(activityClass: Class<T>) {
        startActivity(Intent(this, activityClass))
        overridePendingTransition(R.anim.slide_in, R.anim.stay)
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

    override fun updateNote(note: TrackData) {
        viewPagerAdapter.updateNote(note)
    }

    private val viewPagerAdapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
        val observers = FragmentObserver()
        override fun getItem(position: Int): Fragment {
            val fragment = TrackFragment.getInstance(TimeUtils.getDateForPosition(position))
            observers.addObserver(fragment)
            return fragment
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun getCount(): Int {
            return TimeUtils.DAYS_COUNT
        }

        fun updateNote(note: TrackData) {
            observers.notifyObservers(note)
        }
    }

    private fun getFormattedDate(position: Int): String? {
        return TimeUtils.getDateFormatted(position)
    }


    companion object {
        private const val EDIT_PARAMETERS_REQUEST = 0
        private const val MAKE_PHOTO_REQUEST = 1
        private const val CHOOSE_PHOTO_REQUEST = 2
        private const val CREATE_NOTE_DIALOG = "NoteCreateDialog"
        private const val EDIT_NOTE_DIALOG = "NoteEditDialog"
        private const val FILE_PROVIDER = "ru.aipova.skintracker.fileprovider"
    }

    class FragmentObserver : Observable() {

        override fun notifyObservers(arg: Any?) {
            setChanged()
            super.notifyObservers(arg)
        }
    }
}