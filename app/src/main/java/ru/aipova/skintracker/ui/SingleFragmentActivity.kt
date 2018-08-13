package ru.aipova.skintracker.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import ru.aipova.skintracker.R
import ru.aipova.skintracker.utils.TransitionUtils

abstract class SingleFragmentActivity<T : Fragment> : AppCompatActivity() {

    protected abstract fun createFragment(): T
    protected abstract fun setupPresenter(fragment: T)

    override fun onCreate(savedInstanceState: Bundle?) {
        TransitionUtils.enableTransitions(window)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.single_fragment_activity)

        val fm = supportFragmentManager
        val fragment = fm.findFragmentById(R.id.fragmentContainer) ?: createFragment().also {
            fm.beginTransaction().replace(R.id.fragmentContainer, it).commit()
        }
        setupPresenter(fragment as T)
    }
}
