package com.github.factotum_sdp.factotum.ui.roadbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.DestinationRecord.Action
import com.github.factotum_sdp.factotum.data.DestinationRecord
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for the RecyclerView which will display a dynamic list of DestinationRecord
 * Choice of the RecyclerView instead of a ListAdapter for later facilities with drageNdrop
 */
class RoadBookViewAdapter(private val onItemViewHolderL: View.OnClickListener?) :
    RecyclerView.Adapter<RoadBookViewAdapter.RecordViewHolder>() {

    // Call back needed to instantiate the async list attribute
    private val differCallback = object : DiffUtil.ItemCallback<DestinationRecord>(){
        override fun areItemsTheSame(oldItem: DestinationRecord,
                                     newItem: DestinationRecord): Boolean {
            return  oldItem == newItem
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
            LayoutInflater
                .from(parent.context)
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
        val context = holder.itemView.context

        // Format and bind the TextViews
        holder.destID.text = item.destID
        holder.timeStamp.text =
            timestampStringFormat(item.timeStamp, context.getString(R.string.arrival_text_view))
        holder.waitingTime.text =
            waitingTimeStringFormat(item.waitingTime, context.getString(R.string.wait_text_view))
        holder.rate.text =
            rateStringFormat(item.rate, context.getString(R.string.rate_text_view))
        holder.actions.text =
            actionsStringFormat(item.actions, context.getString(R.string.actions_text_view))
    }

    private fun rateStringFormat(rate: Int, label: String): String {
        return "$label : $rate"
    }

    // Only displayed if there is a non zero waiting time
    private fun waitingTimeStringFormat(waitTime: Int, label: String): String {
        if (waitTime != 0)
            return "$label : $waitTime'"
        return ""
    }
    // arrival : _ or arrival : HH:MM:SS AM-PM
    private fun timestampStringFormat(date: Date?, label: String): String {
        val sb = java.lang.StringBuilder("$label : ")
        if (date == null)
            return sb.append('_').toString()
        date.let {
            sb.append(SimpleDateFormat.getTimeInstance().format(it))
        }
        return sb.toString()
    }
    // actions : () or actions : (p | p | c)
    private fun actionsStringFormat(actions: List<Action>, label: String): String {
        val actionsWithOcc: EnumMap<Action, Int> = EnumMap(Action::class.java)
        actions.forEach {
            actionsWithOcc.compute(it) { _, occ ->
                var newOcc = occ ?: 0
                ++newOcc
            }
        }
        val actionsFormatList = actionsWithOcc.map {
            if (it.value == 1)
                it.key.toString()
            else
                "${it.key} x${it.value}"
        }

        return actionsFormatList.joinToString("| ", "$label : (", ")")
    }

    override fun getItemCount(): Int = asyncList.currentList.size

    /**
     * ViewHolder of a (destination) record
     */
    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val destID: TextView = itemView.findViewById(R.id.dest_id)
        val timeStamp: TextView = itemView.findViewById(R.id.timestamp)
        val waitingTime: TextView = itemView.findViewById(R.id.waiting_time)
        val rate: TextView = itemView.findViewById(R.id.rate)
        val actions: TextView = itemView.findViewById(R.id.dest_actions)

        init {
            itemView.setOnClickListener(onItemViewHolderL)
        }
    }
}