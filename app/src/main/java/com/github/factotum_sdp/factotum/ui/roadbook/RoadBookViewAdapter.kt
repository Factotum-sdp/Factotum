package com.github.factotum_sdp.factotum.ui.roadbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.DestinationRecord

/**
 * Adapter for the RecyclerView which will display a dynamic list of DestinationRecord
 * Choice of the RecyclerView instead of a ListAdapter for later facilities with drageNdrop
 */
class RoadBookViewAdapter(
) : RecyclerView.Adapter<RoadBookViewAdapter.RecordViewHolder>() {

    // Call back needed to instantiate the async list attribute
    private val differCallback = object : DiffUtil.ItemCallback<DestinationRecord>(){
        override fun areItemsTheSame(oldItem: DestinationRecord,
                                     newItem: DestinationRecord): Boolean {
            return  oldItem.destName == newItem.destName
        }

        override fun areContentsTheSame(oldItem: DestinationRecord,
                                        newItem: DestinationRecord): Boolean {
            return oldItem == newItem
        }
    }
    private val asyncList = AsyncListDiffer(this, differCallback)

    // Inflate a new view hierarchy according to fragment_destrecord.xml design
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_destrecord, parent, false)
        return RecordViewHolder(view)
    }

    /**
     * Updates the async list displayed by this RoadBookViewAdapter
     */
    fun submitList(ls: List<DestinationRecord>) {
        asyncList.submitList(ls)
    }

    // Bind each displayed Record to the corresponding current async list slot
    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val item = asyncList.currentList[position]
        holder.idView.text = item.rate.toString()
        holder.contentView.text = item.destName
    }

    override fun getItemCount(): Int = asyncList.currentList.size

    /**
     * ViewHolder of a (destination) record
     */
    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idView: TextView = itemView.findViewById(R.id.item_number)
        val contentView: TextView = itemView.findViewById(R.id.content)
        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }
}