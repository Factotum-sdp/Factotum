package com.github.factotum_sdp.factotum.ui.display

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.databinding.FragmentDisplayBinding
import com.github.factotum_sdp.factotum.ui.display.utils.PhotoAdapter
import com.google.firebase.storage.StorageReference

const val PHONE_NUMBER = "1234567890"

// Fragment responsible for displaying a list of images from Firebase Storage
class DisplayFragment : Fragment() {

    // View model for this fragment
    private val viewModel: DisplayViewModel by viewModels()
    private var _binding: FragmentDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the fragment layout
        _binding = FragmentDisplayBinding.inflate(inflater, container, false)

        // Set up the recycler view with a photo adapter
        val photoAdapter = PhotoAdapter() { storageReference ->
            shareImage(storageReference, PHONE_NUMBER)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = photoAdapter
        }

        // Observe changes in the list of photo references and update the adapter
        viewModel.photoReferences.observe(viewLifecycleOwner) { photoReferences ->
            photoAdapter.submitList(photoReferences)
        }

        // Set up the refresh button click listener
        binding.refreshButton.setOnClickListener {
            viewModel.refreshImages()
        }

        return binding.root
    }

    // Clean up binding when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun shareImage(storageReference: StorageReference, phoneNumber: String) {
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            val shareText = "Here is your delivery: $uri"

            // General sharing intent
            val generalShareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            // Intent for SMS sharing
            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phoneNumber")
                putExtra("sms_body", shareText)
            }

            // Intent for WhatsApp sharing
            val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=$shareText")
                setPackage("com.whatsapp")
            }

            // Create a chooser intent with the option to share via general sharing options and WhatsApp
            val chooserIntent = Intent.createChooser(generalShareIntent, getString(R.string.share)).apply {
                putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(whatsappIntent, smsIntent))
            }

            startActivity(chooserIntent)
        }
    }

}
