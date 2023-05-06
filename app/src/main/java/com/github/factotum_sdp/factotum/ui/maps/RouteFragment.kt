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
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.databinding.FragmentRoutesBinding
import com.github.factotum_sdp.factotum.models.Location
import com.github.factotum_sdp.factotum.placeholder.RouteRecords.DUMMY_COURSE
import com.github.factotum_sdp.factotum.placeholder.RouteRecords.DUMMY_ROUTE
import com.github.factotum_sdp.factotum.ui.roadbook.DRecordDetailsFragment
import com.google.android.material.snackbar.Snackbar


/**
 * Fragment that represents the routes
 */
class RouteFragment : Fragment() {

    companion object {
        const val MAPS_PKG = "com.google.android.apps.maps"
        const val NO_RESULT = "No address found"
    }

    private var _binding: FragmentRoutesBinding? = null
    private val viewModel: MapsViewModel by activityViewModels()


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

        setListenerSearch()
    }

    private fun setListenerList() {
        // instantiate fake data
        val listCourse = DUMMY_COURSE
        val listRoute = DUMMY_ROUTE

        val listView: ListView = binding.listViewRoutes
        val adapter: ArrayAdapter<String?> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            listCourse as List<String?>
        )
        listView.adapter = adapter
        adapter.notifyDataSetChanged()
        listView.setOnItemClickListener { _, _, position, _ ->
            viewModel.addRoute(listRoute[position])
            viewModel.setRunRoute(listRoute[position])
            binding.buttonRun.visibility = Button.VISIBLE
            binding.buttonNext.visibility = Button.VISIBLE
        }
    }

    private fun setListenerButtons() {
        binding.buttonNext.setOnClickListener {
            if (parentFragment is RouteFragment) findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            if (parentFragment is DRecordDetailsFragment) findNavController().navigate(R.id.action_dRecordDetailsFragment_to_MapsFragment)
        }
        binding.buttonAll.setOnClickListener {
            viewModel.addAll(DUMMY_ROUTE)
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.buttonRun.setOnClickListener {
            val route = viewModel.runRouteState.value!!
            val googleMapsUrl = StringBuilder()
                .append("http://maps.google.com/maps?")
                .append("saddr=${route.src.latitude},${route.src.longitude} &")
                .append("daddr=${route.dst.latitude},${route.dst.longitude} ")
                .append("&mode=b")
                .toString()
            val uri = Uri.parse(googleMapsUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage(MAPS_PKG)
            }
            requireContext().startActivity(intent)
        }
    }

    private fun setListenerSearch() {
        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val location = Location.createAndStore(query, requireContext())
                    if (location != null) viewModel.setLocation(location)
                    val toShow = viewModel.location.value?.addressName ?: NO_RESULT
                    Snackbar.make(requireView(), toShow, Snackbar.LENGTH_LONG).show()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}