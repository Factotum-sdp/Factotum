package com.github.factotum_sdp.factotum.utils

import android.content.Context
import com.github.factotum_sdp.factotum.MainActivity

object PreferencesSetting {

    const val SWIPE_L_SHARED_KEY = "SwipeLeftButton"
    const val SWIPE_R_SHARED_KEY = "SwipeRightButton"
    const val DRAG_N_DROP_SHARED_KEY = "DragNDropButton"
    const val TOUCH_CLICK_SHARED_KEY = "TouchClickButton"
    const val SHOW_ARCHIVED_KEY = "ShowArchived"

    fun setPrefs(sharedKey: String, activity: MainActivity, value: Boolean) {
        val sp = activity.getSharedPreferences(sharedKey, Context.MODE_PRIVATE)
        val edit = sp.edit()
        edit.putBoolean(sharedKey, value)
        edit.apply()
    }
    fun setAllPrefs(activity: MainActivity) {
        setPrefs(SWIPE_L_SHARED_KEY, activity, true)
        setPrefs(SWIPE_R_SHARED_KEY, activity, true)
        setPrefs(DRAG_N_DROP_SHARED_KEY, activity, true)
        setPrefs(TOUCH_CLICK_SHARED_KEY, activity, false)
        setPrefs(SHOW_ARCHIVED_KEY, activity, false)
    }
}