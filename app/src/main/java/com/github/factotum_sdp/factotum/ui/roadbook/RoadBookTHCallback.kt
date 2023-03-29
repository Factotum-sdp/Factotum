package com.github.factotum_sdp.factotum.ui.roadbook

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.google.android.material.snackbar.Snackbar

abstract class RoadBookTHCallback() :
    ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT or ItemTouchHelper.ACTION_STATE_SWIPE) {

    abstract fun getHost(): Fragment
    abstract fun getRbViewModel(): RoadBookViewModel
    abstract fun getRecyclerView(): RecyclerView

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
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
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            // Push only if the STATE_IDLE arrives after a Drag and Drop move
            getRbViewModel().pushSwapsResult()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when(direction){
            ItemTouchHelper.RIGHT -> { // Record Edition
                DRecordAlertDialogBuilder(getHost().requireContext(), getHost(),
                    getRbViewModel(), getRecyclerView())
                    .forExistingRecordEdition(viewHolder)
                    .show()
            }
            ItemTouchHelper.LEFT -> { // Record Deletion
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
                builder.show()
            }
        }
    }
}