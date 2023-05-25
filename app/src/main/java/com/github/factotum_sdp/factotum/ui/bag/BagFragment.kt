package com.github.factotum_sdp.factotum.ui.bag

import android.app.AlertDialog
import android.content.ContextWrapper
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.models.Pack
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * The "bag" Fragment
 * Showing on screen all the packets delivered or currently delivered during a Shift
 */
class BagFragment: Fragment(), MenuProvider {

    private val bagViewModel: BagViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_bag, container, false)
        val adapter = BagAdapter(setPackOnClickListener())

        bagViewModel.displayedPackages.observe(viewLifecycleOwner) {
            adapter.submitList(it.toList())
        }

        val packagesRecyclerView: RecyclerView = view.findViewById(R.id.packagesRecyclerView)
        packagesRecyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setPackOnClickListener(): (Pack) -> Unit {
        return {
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.package_notes_dialog, null)
            val notesView: EditText = dialogView.findViewById(R.id.postEditTextPackageNotes)
            notesView.setText(it.notes)

            val builder = MaterialAlertDialogBuilder(
                ContextThemeWrapper(requireContext(), R.style.Theme_Factotum_Dialog))
            builder.setTitle(R.string.bag_edit_dialog_title)
            builder.setView(dialogView)
            builder.setNegativeButton(R.string.edit_dialog_cancel_b, null)
            builder.setPositiveButton(R.string.edit_dialog_update_b) { _, _ ->
                bagViewModel.updateNotesOf(it.packageID, notesView.text.toString())
            }
            builder.setCancelable(false)

            builder.show()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.bag_menu, menu)

        val withSendPackButton = menu.findItem(R.id.menu_filter_button)

        withSendPackButton.setOnMenuItemClickListener {
            it.isChecked = !it.isChecked

            if(it.isChecked) it.setIcon(R.drawable.blue_work_history)
            else it.setIcon(R.drawable.work_history)

            bagViewModel.displayDeliveredPacks(it.isChecked)
            true
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        // Needed to have the onSupportNavigateUp() called
        // when clicking on the home button after an onMenuItemSelected() override
        if (menuItem.itemId == android.R.id.home) {
            return false
        }
        return true
    }
}