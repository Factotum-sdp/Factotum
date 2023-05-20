package com.github.factotum_sdp.factotum.ui.bag

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.models.DestinationRecord.Companion.timeStampFormat
import com.github.factotum_sdp.factotum.models.Pack
import com.google.android.material.textview.MaterialTextView

/**
 * Bag Adapter
 *
 * Adapts a List<Pack> to display it in a recyclerView
 */
class BagAdapter: ListAdapter<Pack, BagAdapter.PackViewHolder>(PackDiffCallback) {

    /**
     * The PackageViewHolder
     *
     * Holds a specific view corresponding to a current "package" state
     *
     * @property itemView: View
     */
    class PackViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val textView: MaterialTextView = itemView.findViewById(R.id.packageItemView)
        private val icon: ImageView = itemView.findViewById(R.id.packageIcon)
        private var currentPackage: Pack? = null

        /* Bind the pack to the textView */
        fun bind(pack: Pack) {
            currentPackage = pack

            pack.deliveredAt?.let {
                icon.setImageResource(R.drawable.send)
            }

            textView.text =
                buildString {
                    append(pack.name)
                    append(" |  ")
                    append(pack.senderID)
                    append(" -> ")
                    append(pack.recipientID)
                    append("\n")
                    append(TAKEN_TIMESTAMP_PREFIX)
                    append(timeStampFormat(pack.takenAt))
                    append("\n")
                    pack.deliveredAt?.let { append(DELIVERED_TIMESTAMP_PREFIX) }
                    val arrivalTimeString = pack.deliveredAt?.let { timeStampFormat(it) } ?: ""
                    append(arrivalTimeString)
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.package_item, parent, false)
        return PackViewHolder(view)
    }

    override fun onBindViewHolder(holder: PackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /** DiffUtil callback needed to computer if a this displayed list has to be updated */
    object PackDiffCallback : DiffUtil.ItemCallback<Pack>() {
        override fun areItemsTheSame(oldItem: Pack, newItem: Pack): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Pack, newItem: Pack): Boolean {
            return oldItem.packageID == newItem.packageID
        }
    }

    companion object {
        const val DELIVERED_TIMESTAMP_PREFIX = "Delivered at : "
        const val TAKEN_TIMESTAMP_PREFIX = "Taken at : "
    }
}