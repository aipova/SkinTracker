package ru.aipova.skintracker.ui.trackpager

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.track_pager_activity.*
import kotlinx.android.synthetic.main.track_pager_content.*
import org.joda.time.LocalDate
import ru.aipova.skintracker.R
import ru.aipova.skintracker.ui.statistics.StatisticsActivity
import ru.aipova.skintracker.ui.track.TrackActivity
import ru.aipova.skintracker.ui.tracktype.TrackTypeActivity
import ru.aipova.skintracker.utils.TimeUtils
import ru.aipova.skintracker.utils.TransitionUtils
import java.util.*

class TrackPagerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TransitionUtils.enableTransitions(window)
        setContentView(R.layout.track_pager_activity)
        setSupportActionBar(toolbar)

        setupNavigation()
        setupTrackPager()

        setupEditButton()
        setupNavigationButtons()
    }

    private fun setupEditButton() {
        fabAddParameters.setOnClickListener {
            startActivityForResult(
                TrackActivity.createIntent(
                    this,
                    getCurrentDiaryDate()
                ), EDIT_REQUEST
            )
        }
    }

    private fun setupNavigationButtons() {
        leftBtn.setOnClickListener { trackPager.currentItem-- }
        rightBtn.setOnClickListener { trackPager.currentItem++ }
        calendarBtn.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val currentDate = TimeUtils.getCalendarForPosition(trackPager.currentItem)
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
            trackPager.currentItem = TimeUtils.getPositionForDate(LocalDate.fromCalendarFields(newDate))
        }

    private fun getCurrentDiaryDate() = TimeUtils.getDateForPosition(trackPager.currentItem)


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == EDIT_REQUEST && resultCode == Activity.RESULT_OK) {
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

    private fun setupTrackPager() {
        trackPager.adapter = viewPagerAdapter
        trackPager.addOnPageChangeListener(pageChangeListener)
        setCurrentTrack()
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


    private fun setCurrentTrack() {
        trackPager.currentItem = TimeUtils.getPositionForDate(TimeUtils.today())
    }

    companion object {
        const val EDIT_REQUEST = 0
    }
}