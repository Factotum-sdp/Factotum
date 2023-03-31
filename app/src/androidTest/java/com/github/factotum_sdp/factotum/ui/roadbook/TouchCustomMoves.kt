package com.github.factotum_sdp.factotum.ui.roadbook

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.github.factotum_sdp.factotum.R

/**
 * Custom touch screen moves for Espresso testing purpose
 */
object TouchCustomMoves {

    fun swipeRightTheRecordAt(pos: Int) {
        swipeSlowActionOnRecyclerList(pos, 0.5f, 1f, 2f, 1f)
    }

    fun swipeLeftTheRecordAt(pos: Int) {
        swipeSlowActionOnRecyclerList(pos, 0.5f, 1f, -1f, 1f)
    }

    private fun swipeSlowActionOnRecyclerList(pos: Int, startX: Float, startY: Float,
                                              endX: Float, endY: Float) {
        Espresso.onView(ViewMatchers.withId(R.id.list)).perform(
            ViewActions.longClick(),
            RecyclerViewActions.actionOnItemAtPosition<RoadBookViewAdapter.RecordViewHolder>(
                pos, GeneralSwipeAction(
                    Swipe.SLOW,
                    {
                        val xy = IntArray(2).also { ar -> it.getLocationOnScreen(ar) }
                        val x = xy[0] + (it.width - 1) * startX
                        val y = xy[1] + (it.height - 1) * startY
                        floatArrayOf(x, y)
                    },
                    {
                        val xy = IntArray(2).also { ar -> it.getLocationOnScreen(ar) }
                        val x = xy[0] + (it.width - 1) * endX
                        val y = xy[1] + (it.height - 1) * endY
                        floatArrayOf(x, y)
                    },
                    Press.PINPOINT
                )
            )
        )
    }
}