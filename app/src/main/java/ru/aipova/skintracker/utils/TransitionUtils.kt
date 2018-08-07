package ru.aipova.skintracker.utils

import android.app.Activity
import android.app.ActivityOptions
import android.os.Build
import android.transition.Slide
import android.view.Gravity
import android.view.Window

class TransitionUtils {

    companion object {
        fun enableTransitions(window: Window) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                with(window) {
                    requestFeature(android.view.Window.FEATURE_CONTENT_TRANSITIONS)
                    enterTransition = Slide(Gravity.END)
                }
            }
        }

        fun makeTransition(activity: Activity) =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
            } else null
    }
}
