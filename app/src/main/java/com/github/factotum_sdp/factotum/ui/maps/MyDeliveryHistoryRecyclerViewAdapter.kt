package com.github.factotum_sdp.factotum.ui.bossmap

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.factotum_sdp.factotum.R

import com.github.factotum_sdp.factotum.databinding.FragmentDeliveryHistoryBinding
import com.github.factotum_sdp.factotum.models.DeliveryStatus

/**
 * [RecyclerView.Adapter] that can display a [DeliveryStatus].
 *
 */
class MyDeliveryHistoryRecyclerViewAdapter(
    private val values: List<DeliveryStatus>
) : RecyclerView.Adapter<MyDeliveryHistoryRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentDeliveryHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.timeStamp.toString()
        holder.contentView.text = item.courier
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentDeliveryHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.historyDate
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}