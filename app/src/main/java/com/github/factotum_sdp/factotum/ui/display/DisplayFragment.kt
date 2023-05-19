package com.github.factotum_sdp.factotum.ui.display

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.UserViewModel
import com.github.factotum_sdp.factotum.databinding.FragmentDisplayBinding
import com.github.factotum_sdp.factotum.models.Contact
import com.github.factotum_sdp.factotum.models.Role
import com.github.factotum_sdp.factotum.ui.directory.ContactsViewModel
import com.github.factotum_sdp.factotum.ui.display.courier_boss.CourierBossDisplayViewModel
import com.github.factotum_sdp.factotum.ui.display.courier_boss.CourierBossDisplayViewModelFactory
import com.github.factotum_sdp.factotum.ui.display.courier_boss.CourierBossFolderAdapter
import com.github.factotum_sdp.factotum.ui.display.client.ClientDisplayViewModel
import com.github.factotum_sdp.factotum.ui.display.client.ClientDisplayViewModelFactory
import com.github.factotum_sdp.factotum.ui.display.client.ClientPhotoAdapter

class DisplayFragment : Fragment(), MenuProvider {

    private lateinit var displayMenu : Menu
    private lateinit var calendarButton: MenuItem

    private val clientViewModel: ClientDisplayViewModel by viewModels{ ClientDisplayViewModelFactory(userFolder, requireContext()) }
    private val courierBossDisplayViewModel : CourierBossDisplayViewModel by viewModels{ CourierBossDisplayViewModelFactory(requireContext()) }
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.STARTED)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.display_menu, menu)
        displayMenu = menu

        calendarButton = menu.findItem(R.id.menu_date_picker)
        calendarButton.setOnMenuItemClickListener {
            showDatePickerDialog()
            true
        }

        setupObservers()
    }

    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireContext(), { _, year, monthOfYear, dayOfMonth ->
            val selectedDate = GregorianCalendar(year, monthOfYear, dayOfMonth).time
            clientViewModel.filterImagesByDate(selectedDate)
        }, year, month, day)
        dpd.show()
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            return false
        }
        return true
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
                observeCourierBossFolders()
                setupCourierBossUI()
            }
            Role.COURIER -> {
                observeCourierBossFolders()
                setupCourierBossUI()
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

    private fun observeCourierBossFolders() {
        courierBossDisplayViewModel.folderReferences.observe(viewLifecycleOwner) { folderReferences ->
            (binding.recyclerView.adapter as? CourierBossFolderAdapter)?.submitList(folderReferences)
        }

        courierBossDisplayViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun setupCourierBossUI() {
        courierBossDisplayViewModel.refreshFolders()
        calendarButton.isVisible = false

        binding.refreshButton.setOnClickListener {
            courierBossDisplayViewModel.refreshFolders()
        }

        val courierBossFolderAdapter = CourierBossFolderAdapter(
            onCardClick = { clientFolder ->
                userFolder.value = clientFolder.value
                observeClientPhotos()
                setupClientUI()
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = courierBossFolderAdapter
        }

    }

    //================================================================
    // Client UI
    //================================================================

    private fun observeClientPhotos() {
        clientViewModel.photoReferences.observe(viewLifecycleOwner) { photoReferences ->
            (binding.recyclerView.adapter as? ClientPhotoAdapter)?.submitList(photoReferences)
        }

        clientViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun setupClientUI() {
        clientViewModel.refreshImages()
        calendarButton.isVisible = true

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
            if (userRole.value == Role.BOSS || userRole.value == Role.COURIER
                && userFolder.value != userID.value) {

                userFolder.value = userID.value
                setupCourierBossUI()
                observeCourierBossFolders()
            }

            // Fix a glitch where the client could go to the RoadBook by
            // pressing the back button
            else if (userRole.value == Role.BOSS || userRole.value == Role.COURIER) {
                findNavController().navigate(R.id.roadBookFragment)
            }
        }
    }

    //================================================================
    // Sharing
    //================================================================

    private fun shareImage(uri: Uri) {
        contactsViewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            val phone = getUserContactPhone(contacts)
            shareViaAppropriateChannel(phone, "Here is your delivery: $uri")
        }
    }

    private fun getUserContactPhone(contacts: List<Contact>): String? {
        val userContact = contacts.find { it.username == userFolder.value }
        return userContact?.phone
    }

    private fun shareViaAppropriateChannel(phone: String?, shareText: String) {
        if (!phone.isNullOrEmpty()) {
            val (generalShareIntent, smsIntent, whatsappIntent) = prepareIntents(phone, shareText)
            startChooserIntent(generalShareIntent, smsIntent, whatsappIntent)
        } else {
            Toast.makeText(
                requireContext(),
                "No phone number found for ${userFolder.value}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun prepareIntents(phone: String, shareText: String): Triple<Intent, Intent, Intent> {
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

        return Triple(generalShareIntent, smsIntent, whatsappIntent)
    }

    private fun startChooserIntent(generalShareIntent: Intent, smsIntent: Intent, whatsappIntent: Intent) {
        val chooserIntent = Intent.createChooser(generalShareIntent, getString(R.string.share)).apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(whatsappIntent, smsIntent))
        }
        startActivity(chooserIntent)
    }
}