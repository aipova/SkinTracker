package ru.aipova.skintracker.ui.statistics

import android.os.Bundle
import android.view.MenuItem
import dagger.android.support.DaggerAppCompatActivity
import ru.aipova.skintracker.R
import javax.inject.Inject

class StatisticsActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var statisticsPresenter: StatisticsPresenter

//    @set:Inject
//    var statisticsFragment: StatisticsFragment? = null

    fun createFragment(): StatisticsFragment {
        return StatisticsFragment.newInstance()
    }

//    override fun setupPresenter(fragment: StatisticsFragment) {
//        StatisticsPresenter(fragment, InjectionStub.trackRepository, InjectionStub.trackTypeRepository)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.single_fragment_activity)

        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.fragmentContainer)
        (fragment as StatisticsFragment).presenter = statisticsPresenter
        if (fragment == null) {
//            fragment = statisticsFragment
            fm.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.slide_out)
    }
}
