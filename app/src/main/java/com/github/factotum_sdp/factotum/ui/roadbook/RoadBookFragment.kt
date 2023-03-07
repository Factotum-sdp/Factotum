package com.github.factotum_sdp.factotum.ui.roadbook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.factotum_sdp.factotum.databinding.FragmentRoadbookBinding

class RoadBookFragment : Fragment() {

    private var _binding: FragmentRoadbookBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView. otherwise throw a NullPointerException
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(RoadBookViewModel::class.java)

        _binding = FragmentRoadbookBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textRoadbook
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}