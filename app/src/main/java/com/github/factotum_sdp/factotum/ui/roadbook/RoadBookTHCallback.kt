package com.github.factotum_sdp.factotum.ui.roadbook

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.github.factotum_sdp.factotum.R
import com.google.android.material.snackbar.Snackbar

/**
 * RoadBookTHCallback abstract class :
 *
 * ItemTouchHelper callback providing the different RoadBook events on
 * specific touch gesture events.
 *
 * The default gesture detections are :
 * - UP ans DOWN drag directions
 * - LEFT and RIGHT directions on a Swipe State Action
 *
 * The resulted events are :
 * - On Drag&Drop :
 *          RoadBook ViewModel data list reordering with several
 *          swap actions appearing only in the back-end and
 *          one final push observable by the front-end when the ViewItem is finally dropped.
 * - On Swipe Left :
 *          Deletion of the DestinationRecord swiped through an AlertDialog.
 * - On Swipe Right :
 *          Edition of the DestinationRecord swiped through a custom AlertDialog.
 */
abstract class RoadBookTHCallback() :
    SimpleCallback(
    UP or DOWN,
    LEFT or RIGHT or ACTION_STATE_SWIPE
    ) {

    abstract fun getHost(): Fragment
    abstract fun getRbViewModel(): RoadBookViewModel
    abstract fun getRecyclerView(): RecyclerView

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: ViewHolder,
        target: ViewHolder
    ): Boolean {
        try {
            val fromPosition = viewHolder.absoluteAdapterPosition
            val toPosition = target.absoluteAdapterPosition

            // Only the front-end is updated when drag-travelling for a smoother UX
            recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)

            // Back-end swap job not published here, @see pushSwapsResult() call
            if (toPosition < fromPosition) {
                getRbViewModel().swapRecords(toPosition, fromPosition - 1)
            } else {
                getRbViewModel().swapRecords(fromPosition, toPosition - 1)
            }
            return true
        } catch (e: java.lang.Exception) {
            return false
        }
    }

    // Hacky move to update the ViewModel only when the Drag&Drop has ended
    override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ACTION_STATE_IDLE) {
            // Push only if the STATE_IDLE arrives after a Drag and Drop move
            getRbViewModel().pushSwapsResult()
        }
    }

    @SuppressLint("NotifyDataSetChanged") // Don't update the front-end, want to keep the hole
    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        when(direction) {
            RIGHT -> { // Record Edition
                editOnSwipeRight(viewHolder)
            }
            LEFT -> { // Record Deletion
                deleteOnSwipeLeft(viewHolder)
            }
        }
    }

    private fun editOnSwipeRight(viewHolder: ViewHolder) {
        val dial =
            DRecordAlertDialogBuilder(getHost().requireContext(), getHost(),
                                        getRbViewModel(), getRecyclerView())
            .forExistingRecordEdition(viewHolder)
            .create()
        dial.window?.attributes?.windowAnimations = R.style.MyDialogAnimation
        dial.show()
    }
    private fun deleteOnSwipeLeft(viewHolder: ViewHolder) {
        val position = viewHolder.absoluteAdapterPosition
        val builder = AlertDialog.Builder(getHost().requireContext())
        builder.setTitle(getHost().getString(R.string.delete_dialog_title))
        builder.setCancelable(false)
        builder.setPositiveButton(getHost().getString(R.string.delete_confirm_button_label)) { _, _ ->
            getRbViewModel().deleteRecordAt(position)
            Snackbar
                .make(getHost().requireView(), getHost().getString(R.string.snap_text_on_rec_delete), 700)
                .setAction("Action", null).show()
        }
        builder.setNegativeButton(getHost().getString(R.string.delete_cancel_button_label)) { _, _ ->
            getRecyclerView().adapter!!.notifyItemChanged(position)
        }
        val dial = builder.create()
        dial.window?.attributes?.windowAnimations = R.style.MyDialogAnimation
        dial.show()
    }
}