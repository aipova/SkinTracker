package ru.aipova.skintracker.ui.trackpager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.track_pager_activity.*
import ru.aipova.skintracker.R

class TrackPagerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.track_pager_activity)

        trackPager.adapter = viewPagerAdapter
        trackPager.addOnPageChangeListener(pageChangeListener)

        setCurrentTrack()

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

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, TrackPagerActivity::class.java)
        }
    }
}