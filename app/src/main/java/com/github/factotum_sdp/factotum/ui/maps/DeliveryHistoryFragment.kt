package com.github.factotum_sdp.factotum.ui.bossmap

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.model.DeliveryStatus
import com.google.gson.Gson


/**
 * A fragment representing a list of Items.
 */
class DeliveryHistoryFragment : Fragment() {

    private var list : List<DeliveryStatus> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.delivery_history)
        val jsonDelivery = arguments?.getString("History") ?: ""
        val deliveries = Gson().fromJson(jsonDelivery, Array<DeliveryStatus>::class.java)
        if(deliveries != null) {
            list = deliveries.toList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_delivery_history_list, container, false)
        // Set the adapter
        if (view is RecyclerView) {
            view.layoutManager = LinearLayoutManager(context)
            view.adapter = MyDeliveryHistoryRecyclerViewAdapter(list)
        }
        return view
    }
}