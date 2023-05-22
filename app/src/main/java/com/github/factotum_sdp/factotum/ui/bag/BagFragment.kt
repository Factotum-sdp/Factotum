package com.github.factotum_sdp.factotum.ui.bag

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.models.Pack
import kotlinx.serialization.descriptors.buildSerialDescriptor

/**
 * The "bag" Fragment
 * Showing on screen all the packets delivered or currently delivered during a Shift
 */
class BagFragment: Fragment() {

    private val bagViewModel: BagViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_bag, container, false)
        val adapter = BagAdapter(setPackOnClickListener())

        bagViewModel.packages.observe(viewLifecycleOwner) {
            adapter.submitList(it.toList())
        }

        val packagesRecyclerView: RecyclerView = view.findViewById(R.id.packagesRecyclerView)
        packagesRecyclerView.adapter = adapter

        return view
    }

    private fun setPackOnClickListener(): (Pack) -> Unit {
        return {
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.package_notes_dialog, null)
            val notesView: EditText = dialogView.findViewById(R.id.postEditTextPackageNotes)
            notesView.setText(it.notes)

            val builder = AlertDialog.Builder(requireContext())
            builder.setView(dialogView)
            builder.setNegativeButton(R.string.edit_dialog_cancel_b, null)
            builder.setPositiveButton(R.string.edit_dialog_update_b) { _, _ ->
                bagViewModel.updateNotesOf(it.packageID, notesView.text.toString())
            }
            builder.setCancelable(false)

            builder.show()
        }
    }
}