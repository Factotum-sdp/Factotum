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

class RoadBookRecyclerViewAdapter(
) : RecyclerView.Adapter<RoadBookRecyclerViewAdapter.RecordViewHolder>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_destrecord, parent, false)
        return RecordViewHolder(view)
    }
    fun submitList(ls: List<DestinationRecord>) {
        asyncList.submitList(ls)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val item = asyncList.currentList[position]
        holder.idView.text = item.rate.toString()
        holder.contentView.text = item.destName
    }

    override fun getItemCount(): Int = asyncList.currentList.size

    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idView: TextView = itemView.findViewById(R.id.item_number)
        val contentView: TextView = itemView.findViewById(R.id.content)
        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}