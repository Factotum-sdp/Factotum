package com.github.factotum_sdp.factotum

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.factotum_sdp.factotum.databinding.FragmentFirstBinding


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

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


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}