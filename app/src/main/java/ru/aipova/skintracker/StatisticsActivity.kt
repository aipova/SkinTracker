package ru.aipova.skintracker

import android.support.v4.app.Fragment

class StatisticsActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return StatisticsFragment.newInstance()
    }
}
