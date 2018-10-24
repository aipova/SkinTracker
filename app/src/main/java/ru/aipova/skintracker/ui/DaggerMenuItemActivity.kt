package ru.aipova.skintracker.ui

import android.support.v4.app.Fragment
import android.view.MenuItem
import ru.aipova.skintracker.R

abstract class DaggerMenuItemActivity<T : Fragment>: DaggerSingleFragmentActivity<T>() {
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
