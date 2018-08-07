package ru.aipova.skintracker.ui.statistics

import ru.aipova.skintracker.ui.SingleFragmentActivity

class StatisticsActivity : SingleFragmentActivity<StatisticsFragment>() {

    override fun createFragment(): StatisticsFragment {
        return StatisticsFragment.newInstance()
    }

    override fun setupPresenter(fragment: StatisticsFragment) {
        //TODO no presenter yet
    }
}
