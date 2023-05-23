package com.github.factotum_sdp.factotum.ui.display

import android.animation.ObjectAnimator
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.UserViewModel
import com.github.factotum_sdp.factotum.databinding.FragmentDisplayBinding
import com.github.factotum_sdp.factotum.models.Contact
import com.github.factotum_sdp.factotum.models.Role
import com.github.factotum_sdp.factotum.ui.directory.ContactsViewModel
import com.github.factotum_sdp.factotum.ui.display.client.ClientDisplayViewModel
import com.github.factotum_sdp.factotum.ui.display.client.ClientDisplayViewModelFactory
import com.github.factotum_sdp.factotum.ui.display.client.ClientPhotoAdapter
import com.github.factotum_sdp.factotum.ui.display.courier_boss.CourierBossDisplayViewModel
import com.github.factotum_sdp.factotum.ui.display.courier_boss.CourierBossDisplayViewModelFactory
import com.github.factotum_sdp.factotum.ui.display.courier_boss.CourierBossFolderAdapter
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Date

private const val ANIMATION_DURATION = 400L

class DisplayFragment : Fragment(), MenuProvider {

    private lateinit var displayMenu : Menu
    private lateinit var calendarButton: MenuItem
    private lateinit var refreshButton: ImageView

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
            showMaterialDatePickerDialog()
            true
        }

        val menuRefresh = menu.findItem(R.id.menu_refresh)
        refreshButton = menuRefresh.actionView as ImageView
        refreshButton.setImageResource(R.drawable.refresh)

        setupObservers()
    }


    private fun rotateRefreshButton(view: ImageView) {
        val rotation = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f)
        rotation.duration = ANIMATION_DURATION
        rotation.interpolator = LinearInterpolator()
        rotation.start()
    }

    private fun showMaterialDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentDate = calendar.timeInMillis

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(currentDate)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            // The selection returns a Long value representing the selected date
            val selectedDate = Date(selection)
            clientViewModel.filterImagesByDate(selectedDate)
        }

        datePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
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
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.folders)

        refreshButton.setOnClickListener { menuItem ->
            rotateRefreshButton(menuItem as ImageView)
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
        val clientName = userFolder.value?.replaceFirstChar { it.uppercase() }
        (activity as AppCompatActivity).supportActionBar?.title = clientName

        refreshButton.setOnClickListener { menuItem ->
            rotateRefreshButton(menuItem as ImageView)
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
            val currentUser = userID.value
            val currentRole = userRole.value
            val currentFolder = userFolder.value

            if ((currentRole == Role.BOSS || currentRole == Role.COURIER) && currentFolder != currentUser) {
                userFolder.value = userID.value

                setupCourierBossUI()
                observeCourierBossFolders()
            }
            else if (currentRole == Role.BOSS || currentRole == Role.COURIER) {
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }


    //================================================================
    // Sharing
    //================================================================
    private fun shareImage(uri: Uri) {
        contactsViewModel.contacts.value?.let { contacts ->
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