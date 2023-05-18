package com.github.factotum_sdp.factotum.ui.bag

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.models.Package
import com.google.android.material.textview.MaterialTextView

class PackagesAdapter: ListAdapter<Package, PackagesAdapter.PackageViewHolder>(FlowerDiffCallback) {

    class PackageViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val packageTextView: MaterialTextView = itemView.findViewById(R.id.packageItemView)
        private var currentPackage: Package? = null

        /* Bind flower name and image. */
        fun bind(pack: Package) {
            currentPackage = pack

            packageTextView.text =
                buildString {
                    append(pack.packageID)
                    append(" |  ")
                    append(pack.senderID)
                    append(" -> ")
                    append(pack.receiverID)
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

    object FlowerDiffCallback : DiffUtil.ItemCallback<Package>() {
        override fun areItemsTheSame(oldItem: Package, newItem: Package): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Package, newItem: Package): Boolean {
            return oldItem.packageID == newItem.packageID
        }
    }
}