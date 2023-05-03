package com.github.factotum_sdp.factotum.ui.display

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.UserViewModel
import com.github.factotum_sdp.factotum.databinding.FragmentDisplayBinding
import com.github.factotum_sdp.factotum.models.Role
import com.github.factotum_sdp.factotum.ui.display.boss.BossDisplayViewModel
import com.github.factotum_sdp.factotum.ui.display.boss.BossFolderAdapter
import com.github.factotum_sdp.factotum.ui.display.client.ClientDisplayViewModel
import com.github.factotum_sdp.factotum.ui.display.client.ClientDisplayViewModelFactory
import com.github.factotum_sdp.factotum.ui.display.client.ClientPhotoAdapter
import com.google.firebase.storage.StorageReference


// Fragment responsible for displaying a list of images from Firebase Storage
class DisplayFragment : Fragment() {

    // View model for this fragment
    private val clientViewModel: ClientDisplayViewModel by viewModels { ClientDisplayViewModelFactory(userID) }
    private val bossViewModel: BossDisplayViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private var _binding: FragmentDisplayBinding? = null
    private val binding get() = _binding!!
    private val userID = MutableLiveData("Unknown")
    private val userRole = MutableLiveData(Role.UNKNOWN)
    private val PHONE_NUMBER = "1234567890"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the fragment layout
        _binding = FragmentDisplayBinding.inflate(inflater, container, false)

        userViewModel.loggedInUser.observe(viewLifecycleOwner) { user ->
            userID.value = user.name
            userRole.value = user.role
        }

        userRole.observe(viewLifecycleOwner) { role ->
            when (role) {
                Role.BOSS -> setupBossUI()
                Role.CLIENT -> setupClientUI()
                else -> setupClientUI()
            }
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

    private fun setupBossUI() {
        binding.refreshButton.setOnClickListener {
            bossViewModel.refreshFolders()
        }

        val bossFolderAdapter = BossFolderAdapter(
            onCardClick = { folderReference ->
                //
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bossFolderAdapter
        }

        bossViewModel.folderReferences.observe(viewLifecycleOwner) { folderReferences ->
            bossFolderAdapter.submitList(folderReferences)
        }

    }

    private fun setupClientUI() {
        // Set up the refresh button click listener for the client
        binding.refreshButton.setOnClickListener {
            userID.value?.let { clientViewModel.refreshImages(it) }
        }

        // Set up the recycler view with a photo adapter
        val clientPhotoAdapter = ClientPhotoAdapter(
            onShareClick = { storageReference ->
                shareImage(storageReference, PHONE_NUMBER)
            },
            onCardClick = { uri ->
                openImage(uri)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = clientPhotoAdapter
        }

        // Observe changes in the list of photo references and update the adapter
        clientViewModel.photoReferences.observe(viewLifecycleOwner) { photoReferences ->
            clientPhotoAdapter.submitList(photoReferences)
        }
    }

}