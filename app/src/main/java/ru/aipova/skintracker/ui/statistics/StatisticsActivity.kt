package ru.aipova.skintracker.ui.statistics

import ru.aipova.skintracker.InjectionStub
import ru.aipova.skintracker.ui.SingleFragmentActivity

class StatisticsActivity : SingleFragmentActivity<StatisticsFragment>() {

    override fun createFragment(): StatisticsFragment {
        return StatisticsFragment.newInstance()
    }

    override fun setupPresenter(fragment: StatisticsFragment) {
        StatisticsPresenter(fragment, InjectionStub.trackRepository, InjectionStub.trackTypeRepository)
    }
}
