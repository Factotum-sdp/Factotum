package com.github.factotum_sdp.factotum.ui.roadbook

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.data.DestinationRecord
import com.github.factotum_sdp.factotum.databinding.FragmentDestrecordBinding

class RoadBookRecyclerViewAdapter(private val values: List<DestinationRecord>
) : RecyclerView.Adapter<RoadBookRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentDestrecordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.rate.toString()
        holder.contentView.text = item.destName
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentDestrecordBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content
        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}