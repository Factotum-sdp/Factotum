package com.github.factotum_sdp.factotum.ui.maps

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.Route
import com.github.factotum_sdp.factotum.databinding.FragmentRoutesBinding
import com.github.factotum_sdp.factotum.ui.maps.MapsViewModel


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class RouteFragment : Fragment() {

    private var _binding: FragmentRoutesBinding? = null
    private val viewModel: MapsViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRoutesBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        viewModel.deleteAll()
        val listCourse = arrayListOf("BC -> Satelitte", "EPFL -> Chauderon", "Genève -> Lausanne")
        val listRoute = arrayListOf<Route>(
            Route(46.51869448523383, 6.561842896370142, 46.520742473314236, 6.567824983999257),
            Route(46.51916261132295, 6.566773558010879, 46.52369559941859, 6.6250423828801654),
            Route(46.205062260846894, 6.1430383670835464, 46.517234720289416, 6.6291717405531605),
        )
        val listView: ListView = binding.listView
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
            binding.buttonFirst.visibility = Button.VISIBLE

        }

        binding.buttonAll.setOnClickListener{
            for(route in listRoute){
                viewModel.addRoute(route)
            }
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.buttonRun.setOnClickListener{
            val route = viewModel.runRoute.value!!
            val googleMapsUrl = "http://maps.google.com/maps?" +
                    "saddr=${route.src.latitude}," +
                    "${route.src.longitude} &" +
                    "daddr=${route.dst.latitude}," +
                    "${route.dst.longitude} &mode=b"
            val uri = Uri.parse(googleMapsUrl)
            val googleMapsPackage = "com.google.android.apps.maps"
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage(googleMapsPackage)
            }
            requireContext().startActivity(intent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}