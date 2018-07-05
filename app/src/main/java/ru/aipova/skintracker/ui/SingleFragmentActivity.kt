package ru.aipova.skintracker.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import ru.aipova.skintracker.R

abstract class SingleFragmentActivity : AppCompatActivity() {

    protected abstract fun createFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.single_fragment_activity)

        val fm = supportFragmentManager
        if (fm.findFragmentById(R.id.fragmentContainer) == null) {
            fm.beginTransaction().add(R.id.fragmentContainer, createFragment()).commit()
        }
    }
}
