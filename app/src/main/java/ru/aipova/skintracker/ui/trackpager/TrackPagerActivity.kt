package ru.aipova.skintracker.ui.trackpager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.track_pager_activity.*
import kotlinx.android.synthetic.main.track_pager_content.*
import ru.aipova.skintracker.R
import ru.aipova.skintracker.ui.statistics.StatisticsActivity
import ru.aipova.skintracker.ui.track.TrackActivity
import ru.aipova.skintracker.ui.tracktype.TrackTypeActivity
import ru.aipova.skintracker.utils.TransitionUtils

class TrackPagerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TransitionUtils.enableTransitions(window)
        setContentView(R.layout.track_pager_activity)
        setSupportActionBar(toolbar)

        setupNavigation()
        setupTrackPager()

        trackAddFab.setOnClickListener { startActivity(TrackActivity.createIntent(this)) }
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

    private val viewPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager) {
        override fun getItem(position: Int): Fragment {
            return TrackPagerFragment.getInstance(TimeUtils.getDateForPosition(position))
        }

        override fun getCount(): Int {
            return TimeUtils.DAYS_COUNT
        }
    }

    private fun getFormattedDate(position: Int): String? {
        return TimeUtils.getDateFormatted(this, position)
    }


    private fun setCurrentTrack() {
        trackPager.currentItem = TimeUtils.getPositionForDate(TimeUtils.today())
    }
}