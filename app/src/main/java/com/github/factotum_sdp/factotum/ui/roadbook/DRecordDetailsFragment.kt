package com.github.factotum_sdp.factotum.ui.roadbook

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.models.DestinationRecord
import com.github.factotum_sdp.factotum.models.Route
import com.github.factotum_sdp.factotum.ui.directory.ContactDetailsFragment
import com.github.factotum_sdp.factotum.ui.directory.ContactsViewModel
import com.github.factotum_sdp.factotum.ui.directory.DirectoryFragment.Companion.IS_SUB_FRAGMENT_NAV_KEY
import com.github.factotum_sdp.factotum.ui.directory.DirectoryFragment.Companion.USERNAME_NAV_KEY
import com.github.factotum_sdp.factotum.ui.maps.MapsFragment
import com.github.factotum_sdp.factotum.ui.maps.MapsViewModel
import com.github.factotum_sdp.factotum.ui.picture.PictureFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

/**
 * The container fragment which display all the details of a DestinationRecord
 *
 * The sliding event shows different fragments related to the DestinationRecord
 */
class DRecordDetailsFragment : Fragment() {

    private val rbViewModel: RoadBookViewModel by activityViewModels()
    private val mapsViewModel: MapsViewModel by activityViewModels()
    private val contactsViewModel: ContactsViewModel by activityViewModels()
    private lateinit var rec: DestinationRecord
    private lateinit var viewPager: ViewPager2
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_drecord_details, container, false)

        val destID = arguments?.getString(RoadBookFragment.DEST_ID_NAV_ARG_KEY) ?: "UNKNOWN"
        rbViewModel.recordsListState.value?.let {
            rec = it.first { d -> d.destID == destID }
        }

        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Dest. Record : $destID"

        pagerSetup(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabFrag = TabFragment.values()[position]
            tab.setIcon(tabFrag.iconID)
        }.attach()
    }

    enum class TabFragment(val iconID: Int) {
        INFO(R.drawable.ic_info),
        MAPS(R.drawable.ic_map),
        CONTACT(R.drawable.contact_image),
        PICTURE(R.drawable.ic_menu_camera)
    }

    private fun pagerSetup(view: View) {
        viewPager = view.findViewById(R.id.viewPager)
        val adapter = DetailsFragmentsAdapter(this)

        val detailsFragment = ContactDetailsFragment::class.java.newInstance()
        detailsFragment.arguments = Bundle().apply {
            putString(USERNAME_NAV_KEY, rec.clientID)
            putBoolean(IS_SUB_FRAGMENT_NAV_KEY, true)
        }

        val mapsFragment = MapsFragment()
        contactsViewModel.contacts.value?.apply {
            try {
                val bundle = Bundle().apply {
                    putBoolean(MapsFragment.IN_NAV_PAGER, true)
                    putBoolean(MapsFragment.DRAW_ROUTE, false)
                }
                val currentContact = first { c -> c.username == rec.clientID }
                if (currentContact.hasCoordinates()) {
                    val route = Route(
                        currentContact.latitude ?: 0.0, // should be current location
                        currentContact.longitude ?: 0.0,
                        currentContact.latitude ?: 0.0,
                        currentContact.longitude ?: 0.0
                    )
                    val routeJson = Gson().toJson(route)
                    bundle.putString(MapsFragment.ROUTE_NAV_KEY, routeJson)
                }
                mapsFragment.arguments = bundle

            } catch (e: NoSuchElementException) {
                // Handle the exception here
                Log.e("Error", "Could not find contact with ID ${rec.clientID}")
            }
        }

        adapter.addFragment(DRecordInfoFragment(rec))
        adapter.addFragment(mapsFragment)
        adapter.addFragment(detailsFragment)
        adapter.addFragment(PictureFragment(rec.clientID))

        viewPager.adapter = adapter
    }


    class DetailsFragmentsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        private val fragments: MutableList<Fragment> = mutableListOf()

        fun addFragment(fragment: Fragment) {
            fragments.add(fragment)
        }

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }
    }
}