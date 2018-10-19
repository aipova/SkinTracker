package ru.aipova.skintracker.ui.statistics

import ru.aipova.skintracker.ui.DaggerMenuItemActivity

class StatisticsActivity : DaggerMenuItemActivity<StatisticsFragment>() {
    override fun createFragment(): StatisticsFragment {
        return StatisticsFragment.newInstance()
    }
}
