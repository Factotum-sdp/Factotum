package com.github.factotum_sdp.factotum.ui.display

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.github.factotum_sdp.factotum.ui.directory.ContactsViewModel
import com.github.factotum_sdp.factotum.ui.display.boss.BossDisplayViewModel
import com.github.factotum_sdp.factotum.ui.display.boss.BossDisplayViewModelFactory
import com.github.factotum_sdp.factotum.ui.display.boss.BossFolderAdapter
import com.github.factotum_sdp.factotum.ui.display.client.ClientDisplayViewModel
import com.github.factotum_sdp.factotum.ui.display.client.ClientDisplayViewModelFactory
import com.github.factotum_sdp.factotum.ui.display.client.ClientPhotoAdapter
import com.google.firebase.storage.StorageReference

class DisplayFragment : Fragment() {

    private val clientViewModel: ClientDisplayViewModel by viewModels{ ClientDisplayViewModelFactory(userFolder, requireContext()) }
    private val bossViewModel: BossDisplayViewModel by viewModels{ BossDisplayViewModelFactory(requireContext()) }
    private val userViewModel: UserViewModel by activityViewModels()
    private val contactsViewModel: ContactsViewModel by activityViewModels()
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
            userFolder.value = user.name

            setupUIBasedOnUserRole()
        }

        userFolder.observe(viewLifecycleOwner) { folder ->
            if (folder !=clientViewModel.folderName.value) {
                clientViewModel.setFolderName(folder)
                clientViewModel.refreshImages()
            }
        }
    }

    private fun setupUIBasedOnUserRole() {
        when (userRole.value) {
            Role.BOSS -> {
                observeBossFolders()
                setupBossUI()
            }
            Role.CLIENT -> {
                observeClientPhotos()
                setupClientUI()
            }
            else -> {
                observeClientPhotos()
                setupClientUI()
            }
        }
    }

    private fun openImage(imageUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(imageUri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(intent)
    }

    //================================================================
    // Boss UI
    //================================================================

    private fun observeBossFolders() {
        bossViewModel.folderReferences.observe(viewLifecycleOwner) { folderReferences ->
            (binding.recyclerView.adapter as? BossFolderAdapter)?.submitList(folderReferences)
        }
    }

    private fun setupBossUI() {
        bossViewModel.refreshFolders()

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

    //================================================================
    // Client UI
    //================================================================

    private fun observeClientPhotos() {
        clientViewModel.photoReferences.observe(viewLifecycleOwner) { photoReferences ->
            (binding.recyclerView.adapter as? ClientPhotoAdapter)?.submitList(photoReferences)
        }
    }

    private fun setupClientUI() {
        clientViewModel.refreshImages()

        binding.refreshButton.setOnClickListener {
            clientViewModel.refreshImages()
        }

        val clientPhotoAdapter = ClientPhotoAdapter(
            lifecycleOwner = viewLifecycleOwner,
            viewModel = clientViewModel,
            userRole = userRole.value!!,
            onShareClick = { uri ->
                shareImage(uri)
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


    private fun shareImage(uri: Uri) {
        val shareText = "Here is your delivery: $uri"

        contactsViewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            val userContact = contacts.find { it.username == userFolder.value }
            val phone = userContact?.phone

            if (!phone.isNullOrEmpty()) {
                val generalShareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }

                val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:$phone")
                    putExtra("sms_body", shareText)
                }

                val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=$shareText")
                    setPackage("com.whatsapp")
                }

                val chooserIntent =
                Intent.createChooser(generalShareIntent, getString(R.string.share)).apply {
                    putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(whatsappIntent, smsIntent))
                }

                startActivity(chooserIntent)
            } else {
                Toast.makeText(
                    requireContext(),
                    "No phone number found for ${userFolder.value}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
