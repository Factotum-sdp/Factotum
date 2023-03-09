package com.github.factotum_sdp.factotum

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.factotum_sdp.factotum.databinding.FragmentFirstBinding
import com.github.factotum_sdp.factotum.databinding.FragmentSecondBinding
import com.google.android.gms.maps.model.LatLng


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val viewModel: MainView by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            val satellite = LatLng(46.520544, 6.567825)
            val incBuilding = LatLng(46.51864288439962, 6.561958064149488)
            val postCoursRoute = Route(incBuilding, satellite)
            viewModel.setRoute(postCoursRoute)
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        val list = arrayListOf("hello", "my", "name", "is", "Daniel")
        val listView: ListView = binding.listView
        val adapter: ArrayAdapter<String?> = ArrayAdapter(requireContext(),
            android.R.layout.simple_list_item_1,
            list as List<String?>
        )
        listView.adapter = adapter
        adapter.notifyDataSetChanged()
        listView.setOnItemClickListener{_, _, position, _ ->
            Toast.makeText(
                requireContext(),
                binding.listView.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}