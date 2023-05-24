package com.github.factotum_sdp.factotum.ui.bossmap

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.models.DeliveryStatus
import com.google.gson.Gson


/**
 * A fragment representing a list of Items.
 */
class DeliveryHistoryFragment : Fragment() {

    private var columnCount = 1
    lateinit var list : List<DeliveryStatus>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val jsonDelivery = arguments?.getString("History") ?: ""
        val deliveries = Gson().fromJson(jsonDelivery, Array<DeliveryStatus>::class.java)
        list = deliveries.toList()

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