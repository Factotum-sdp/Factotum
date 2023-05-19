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

class PackagesAdapter: ListAdapter<Pack, PackagesAdapter.PackageViewHolder>(PackDiffCallback) {

    class PackageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val textView: MaterialTextView = itemView.findViewById(R.id.packageItemView)
        private val icon: ImageView = itemView.findViewById(R.id.packageIcon)
        private var currentPackage: Pack? = null

        /* Bind flower name and image. */
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.package_item, parent, false)
        return PackageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

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