package com.github.factotum_sdp.factotum.ui.roadbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.DestinationRecord.Action
import com.github.factotum_sdp.factotum.data.DestinationRecord
import java.util.*

/**
 * Adapter for the RecyclerView which will display a dynamic list of DestinationRecord
 * Choice of the RecyclerView instead of a ListAdapter for later facilities with drag & drop
 */
class RoadBookViewAdapter(private val onClickListenerFromDestId: (String) -> View.OnClickListener?) :
    RecyclerView.Adapter<RoadBookViewAdapter.RecordViewHolder>() {

    private var displayedDRecords: DRecordList = DRecordList()

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
    fun submitList(ls: DRecordList) {
        displayedDRecords = ls
    }

    // Bind each displayed Record to the corresponding current async list slot
    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val item = displayedDRecords[position]
        val itemView = holder.itemView
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
        holder.archivedIcon.visibility =
            if (displayedDRecords.isArchived(position))
                View.VISIBLE
            else
                View.INVISIBLE

        // Note that transition has to be done through ID and not the position
        // as there is some asynchrony between displayed list and the RBViewModel's one
        itemView.setOnClickListener(onClickListenerFromDestId(holder.destID.text.toString()))
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
        return "$label : ${DestinationRecord.timeStampFormat(date)}"
    }
    // actions : () or actions : (pick x2 |contact)
    private fun actionsStringFormat(actions: List<Action>, label: String): String {
        return "$label : ${DestinationRecord.actionsFormat(actions)}"
    }
    override fun getItemCount(): Int = displayedDRecords.size

    /**
     * ViewHolder of a (destination) record
     */
    inner class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val destID: TextView = itemView.findViewById(R.id.dest_id)
        val timeStamp: TextView = itemView.findViewById(R.id.timestamp)
        val waitingTime: TextView = itemView.findViewById(R.id.waiting_time)
        val rate: TextView = itemView.findViewById(R.id.rate)
        val actions: TextView = itemView.findViewById(R.id.dest_actions)
        val archivedIcon: ImageView = itemView.findViewById(R.id.archivedIcon)
    }
}