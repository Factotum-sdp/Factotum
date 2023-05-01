package com.github.factotum_sdp.factotum.ui.display

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.UserViewModel
import com.github.factotum_sdp.factotum.databinding.FragmentDisplayBinding
import com.github.factotum_sdp.factotum.ui.display.utils.PhotoAdapter
import com.google.firebase.storage.StorageReference


// Fragment responsible for displaying a list of images from Firebase Storage
class DisplayFragment : Fragment() {

    // View model for this fragment
    private val viewModel: DisplayViewModel by viewModels { DisplayViewModelFactory(userID) }
    private val userViewModel: UserViewModel by activityViewModels()
    private var _binding: FragmentDisplayBinding? = null
    private val binding get() = _binding!!
    private val userID = MutableLiveData("Unknown")
    private val PHONE_NUMBER = "1234567890"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the fragment layout
        _binding = FragmentDisplayBinding.inflate(inflater, container, false)

        // Set up the recycler view with a photo adapter
        val photoAdapter = PhotoAdapter(
            onShareClick = { storageReference ->
                shareImage(storageReference, PHONE_NUMBER)
            },
            onCardClick = { uri ->
                openImage(uri)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = photoAdapter
        }

        // Observe changes in the list of photo references and update the adapter
        viewModel.photoReferences.observe(viewLifecycleOwner) { photoReferences ->
            photoAdapter.submitList(photoReferences)
        }

        userViewModel.loggedInUser.observe(viewLifecycleOwner) { user ->
            userID.value = user.name
        }

        // Set up the refresh button click listener
        binding.refreshButton.setOnClickListener {
            userID.value?.let { viewModel.refreshImages(it) }
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
            val chooserIntent =
                Intent.createChooser(generalShareIntent, getString(R.string.share)).apply {
                    putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(whatsappIntent, smsIntent))
                }

            startActivity(chooserIntent)
        }
    }

    private fun openImage(imageUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(imageUri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

}



