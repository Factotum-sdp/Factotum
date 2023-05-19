package com.github.factotum_sdp.factotum.ui.bag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R

class BagFragment: Fragment() {

    private val bagViewModel: BagViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_bag, container, false)
        val adapter = PackagesAdapter()

        bagViewModel.packages.observe(viewLifecycleOwner) {
            adapter.submitList(it.toList())
        }

        //adapter.submitList(listOf(Package("packID#1", "X17", "Buhagiat", null, "")))

        val packagesRecyclerView: RecyclerView = view.findViewById(R.id.packagesRecyclerView)
        packagesRecyclerView.adapter = adapter

        return view
    }
}