package ru.aipova.skintracker

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

abstract class SingleFragmentActivity : AppCompatActivity() {

    protected abstract fun createFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        val fm = supportFragmentManager
        if (fm.findFragmentById(R.id.fragment_container) == null) {
            fm.beginTransaction().add(R.id.fragment_container, createFragment()).commit()
        }
    }
}
