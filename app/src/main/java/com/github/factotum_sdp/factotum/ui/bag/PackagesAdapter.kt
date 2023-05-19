package com.github.factotum_sdp.factotum.ui.bag

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.models.DestinationRecord.Companion.timeStampFormat
import com.github.factotum_sdp.factotum.models.Pack
import com.google.android.material.textview.MaterialTextView

class PackagesAdapter: ListAdapter<Pack, PackagesAdapter.PackageViewHolder>(FlowerDiffCallback) {

    class PackageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val textView: MaterialTextView = itemView.findViewById(R.id.packageItemView)
        private var currentPackage: Pack? = null

        /* Bind flower name and image. */
        fun bind(pack: Pack) {
            currentPackage = pack


            textView.text =
                buildString {
                    append(pack.name)
                    append(" |  ")
                    append(pack.senderID)
                    append(" -> ")
                    append(pack.recipientID)
                    append("\n")
                    append("Taken at : ")
                    append(timeStampFormat(pack.takenAt))
                    append("\n")
                    append("Delivered at : ")
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
        val flower = getItem(position)
        holder.bind(flower)
    }

    object FlowerDiffCallback : DiffUtil.ItemCallback<Pack>() {
        override fun areItemsTheSame(oldItem: Pack, newItem: Pack): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Pack, newItem: Pack): Boolean {
            return oldItem.packageID == newItem.packageID
        }
    }
}