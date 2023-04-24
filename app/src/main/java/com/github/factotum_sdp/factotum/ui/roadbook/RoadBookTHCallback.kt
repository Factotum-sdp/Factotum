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
abstract class RoadBookTHCallback :
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
        try { // Drag & Drop by swapping the DRecords of the RoadBookViewModel
            val fromPosition = viewHolder.absoluteAdapterPosition
            val toPosition = target.absoluteAdapterPosition
            getRbViewModel().moveRecord(fromPosition, toPosition)
            recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)
            return true
        } catch (e: java.lang.Exception) {
            return false
        }
    }

    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
        when (direction) {
            RIGHT -> { // Record Edition
                editOnSwipeRight(viewHolder)
            }
            LEFT -> { // Record Deletion
                val pos = viewHolder.absoluteAdapterPosition
                if (getRbViewModel().isRecordAtArchived(pos)) {
                    unarchiveOnSwipeLeft(pos)
                } else {
                    if (getRbViewModel().isRecordAtTimeStamped(pos))
                        archiveOnSwipeLeft(pos)
                    else
                        deleteOnSwipeLeft(pos)
                }
            }
        }
    }

    private fun editOnSwipeRight(viewHolder: ViewHolder) {
        val dial = // Launch & Delegate the event to a custom AlertDialog
            DRecordEditDialogBuilder(
                getHost().requireContext(), getHost(),
                getRbViewModel(), getRecyclerView()
            )
                .forExistingRecordEdition(viewHolder)
                .create()
        dial.window?.attributes?.windowAnimations = R.style.DialogAnimLeftToRight
        dial.show()
    }

    private fun deleteOnSwipeLeft(position: Int) {
        swipeLeftDialogAndAction(
            position,
            R.string.delete_dialog_title,
            R.string.snap_text_on_rec_delete
        ) {
            getRbViewModel().deleteRecordAt(position)
        }
    }

    private fun unarchiveOnSwipeLeft(position: Int) {
        swipeLeftDialogAndAction(
            position,
            R.string.unarchive_dialog_title,
            R.string.snap_text_on_rec_unarchive
        ) {
            getRbViewModel().unarchiveRecordAt(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun archiveOnSwipeLeft(position: Int) {
        getRbViewModel().archiveRecordAt(position) // No confirmation dialog for the archive action
        getRecyclerView().adapter!!.notifyDataSetChanged()
        swipeLeftSnapMessage(R.string.snap_text_on_rec_archive)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun swipeLeftDialogAndAction(
        position: Int, dialogTitleID: Int,
        snapMessageID: Int, action: () -> Unit
    ) {
        val builder = AlertDialog.Builder(getHost().requireContext())
        builder.setTitle(getHost().getString(dialogTitleID))
        builder.setCancelable(false)
        builder.setPositiveButton(getHost().getString(R.string.swipeleft_confirm_button_label)) { _, _ ->
            action()
            getRecyclerView().adapter!!.notifyDataSetChanged()
            swipeLeftSnapMessage(snapMessageID)
        }
        builder.setNegativeButton(getHost().getString(R.string.swipeleft_cancel_button_label)) { _, _ ->
            getRecyclerView().adapter!!.notifyItemChanged(position)
        }
        val dial = builder.create()
        dial.window?.attributes?.windowAnimations = R.style.DialogAnimLeftToRight
        dial.show()
    }

    private fun swipeLeftSnapMessage(snapMessageID: Int) {
        Snackbar
            .make(getHost().requireView(), getHost().getString(snapMessageID), 700)
            .setAction("Action", null).show()
    }
}