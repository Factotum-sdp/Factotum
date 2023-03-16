package com.github.factotum_sdp.factotum.ui.maps

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.databinding.FragmentRoutesBinding
import com.github.factotum_sdp.factotum.placeholder.RouteRecords.DUMMY_COURSE
import com.github.factotum_sdp.factotum.placeholder.RouteRecords.DUMMY_ROUTE


/**
 * Fragment that represents the routes
 */
class RouteFragment : Fragment() {

    private var _binding: FragmentRoutesBinding? = null
    private val viewModel: MapsViewModel by activityViewModels()
    private val MAPS_PKG = "com.google.android.apps.maps"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRoutesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // resets viewModel
        viewModel.deleteAll()

        setListenerList()

        setListenerButtons()


    }

    private fun setListenerList(){
        // instantiate fake data
        val listCourse = DUMMY_COURSE
        val listRoute = DUMMY_ROUTE

        val listView: ListView = binding.listViewRoutes
        val adapter: ArrayAdapter<String?> = ArrayAdapter(requireContext(),
            android.R.layout.simple_list_item_1,
            listCourse as List<String?>
        )
        listView.adapter = adapter
        adapter.notifyDataSetChanged()
        listView.setOnItemClickListener{_, _, position, _ ->
            viewModel.addRoute(listRoute[position])
            viewModel.setRunRoute(listRoute[position])
            binding.buttonRun.visibility = Button.VISIBLE
            binding.buttonNext.visibility = Button.VISIBLE

        }
    }

    private fun setListenerButtons(){
        binding.buttonNext.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.buttonAll.setOnClickListener{
            for(route in DUMMY_ROUTE){
                viewModel.addRoute(route)
            }
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.buttonRun.setOnClickListener{
            val route = viewModel.runRoute.value!!
            val googleMapsUrl = StringBuilder()
                .append("http://maps.google.com/maps?")
                .append("saddr=${route.src.latitude},")
                .append("${route.src.longitude} &")
                .append("daddr=${route.dst.latitude},")
                .append("${route.dst.longitude} ")
                .append("&mode=b")
                .toString()
            val uri = Uri.parse(googleMapsUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage("com.google.android.apps.maps")
            }
            requireContext().startActivity(intent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}