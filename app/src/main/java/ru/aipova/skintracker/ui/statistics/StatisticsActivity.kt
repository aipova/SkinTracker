package ru.aipova.skintracker.ui.statistics

import ru.aipova.skintracker.InjectionStub
import ru.aipova.skintracker.ui.MenuItemActivity

class StatisticsActivity : MenuItemActivity<StatisticsFragment>() {

    override fun createFragment(): StatisticsFragment {
        return StatisticsFragment.newInstance()
    }

    override fun setupPresenter(fragment: StatisticsFragment) {
        StatisticsPresenter(fragment, InjectionStub.trackRepository, InjectionStub.trackTypeRepository)
    }
}
