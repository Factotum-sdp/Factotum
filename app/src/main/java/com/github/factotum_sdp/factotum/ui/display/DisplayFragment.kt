package com.github.factotum_sdp.factotum.ui.display

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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

class DisplayFragment : Fragment() {

    private val clientViewModel: ClientDisplayViewModel by viewModels { ClientDisplayViewModelFactory(userFolder) }
    private val bossViewModel: BossDisplayViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private var _binding: FragmentDisplayBinding? = null
    private val binding get() = _binding!!
    private val userID = MutableLiveData("Unknown")
    private val userRole = MutableLiveData(Role.UNKNOWN)
    private val userFolder = MutableLiveData("Unknown")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDisplayBinding.inflate(inflater, container, false)
        setupObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setupObservers() {
        userViewModel.loggedInUser.observe(viewLifecycleOwner) { user ->
            userID.value = user.name
            userRole.value = user.role
        }

        userRole.observe(viewLifecycleOwner) { role ->
            userFolder.value = userID.value
            when (role) {
                Role.BOSS -> {
                    setupBossUI()
                    observeBossFolders()
                }
                Role.CLIENT -> {
                    setupClientUI()
                    observeClientPhotos()
                }
                else -> {
                    setupClientUI()
                    observeClientPhotos()
                }
            }
        }
    }


    private fun shareImage(storageReference: StorageReference, phoneNumber: String) {
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            val shareText = "Here is your delivery: $uri"

            val generalShareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phoneNumber")
                putExtra("sms_body", shareText)
            }

            val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=$shareText")
                setPackage("com.whatsapp")
            }

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
            onCardClick = { clientFolder ->
                userFolder.value = clientFolder.value
                observeClientPhotos()
                setupClientUI()
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bossFolderAdapter
        }
    }

    private fun observeBossFolders() {
        bossViewModel.folderReferences.observe(viewLifecycleOwner) { folderReferences ->
            (binding.recyclerView.adapter as? BossFolderAdapter)?.submitList(folderReferences)
        }
    }

    private fun setupClientUI() {
        binding.refreshButton.setOnClickListener {
            clientViewModel.refreshImages(userFolder.value!!)
        }

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
    }

    private fun observeClientPhotos() {
        clientViewModel.photoReferences.observe(viewLifecycleOwner) { photoReferences ->
            (binding.recyclerView.adapter as? ClientPhotoAdapter)?.submitList(photoReferences)
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (userRole.value == Role.BOSS && userFolder.value != userID.value) {
                userFolder.value = userID.value
                setupBossUI()
                observeBossFolders()
            } else {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    companion object{
        const val PHONE_NUMBER = "1234567890"
    }
}