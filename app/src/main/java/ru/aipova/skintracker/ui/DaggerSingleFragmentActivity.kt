package ru.aipova.skintracker.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import dagger.android.support.DaggerAppCompatActivity
import ru.aipova.skintracker.R

abstract class DaggerSingleFragmentActivity<T : Fragment> : DaggerAppCompatActivity() {

    protected abstract fun createFragment(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.single_fragment_activity)

        val fm = supportFragmentManager
        fm.findFragmentById(R.id.fragmentContainer) ?: createFragment().also {
            fm.beginTransaction().replace(R.id.fragmentContainer, it).commit()
        }
    }
}
