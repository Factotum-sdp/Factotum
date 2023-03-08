package com.github.factotum_sdp.factotum.ui.roadbook

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.data.DestinationRecord
import com.github.factotum_sdp.factotum.databinding.FragmentDestrecordBinding

class RoadBookRecyclerViewAdapter(
) : RecyclerView.Adapter<RoadBookRecyclerViewAdapter.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<DestinationRecord>(){
        override fun areItemsTheSame(oldItem: DestinationRecord, newItem: DestinationRecord): Boolean {
            return  oldItem.destName == newItem.destName
        }

        override fun areContentsTheSame(oldItem: DestinationRecord, newItem: DestinationRecord): Boolean {
            return oldItem == newItem
        }
    }
    private val asyncList: AsyncListDiffer<DestinationRecord> = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentDestrecordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    fun submitList(ls: List<DestinationRecord>): Unit  {
        asyncList.submitList(ls)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = asyncList.currentList[position]
        holder.idView.text = item.rate.toString()
        holder.contentView.text = item.destName
    }

    override fun getItemCount(): Int = asyncList.currentList.size

    inner class ViewHolder(binding: FragmentDestrecordBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content
        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}