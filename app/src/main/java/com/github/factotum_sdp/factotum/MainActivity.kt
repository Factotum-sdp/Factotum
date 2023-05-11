package com.github.factotum_sdp.factotum

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.github.factotum_sdp.factotum.databinding.ActivityMainBinding
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance.getDatabase
import com.github.factotum_sdp.factotum.models.Role
import com.github.factotum_sdp.factotum.repositories.SettingsRepository
import com.github.factotum_sdp.factotum.ui.directory.ContactsViewModel
import com.github.factotum_sdp.factotum.ui.login.LoginViewModel
import com.github.factotum_sdp.factotum.ui.picture.UploadWorker
import com.github.factotum_sdp.factotum.ui.settings.SettingsViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

private const val INTERVAL_UPLOAD_PICTURE_TIME_MINUTE = 5L
private const val INTERVAL_UPLOAD_LOCATION_TIME_SECOND = 15L

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var user: UserViewModel
    private lateinit var login: LoginViewModel
    private lateinit var settings: SettingsViewModel
    private lateinit var contactsViewModel: ContactsViewModel
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val logVMFact = LoginViewModel.LoginViewModelFactory(applicationContext)
        login =
            ViewModelProvider(this, logVMFact)[LoginViewModel::class.java]

        contactsViewModel = ViewModelProvider(this)[ContactsViewModel::class.java]
        contactsViewModel.setDatabase(getDatabase())

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Passing each menu ID considered as top level destinations.
        // In order to keep out the NavigateUpButton which would mask the HamburgerButton
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.roadBookFragment, R.id.directoryFragment,
                R.id.loginFragment, R.id.routeFragment,
                R.id.displayFragment,R.id.bossMapFragment,
                R.id.settingsFragment
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        // Set the OnNavigationItemSelectedListener for the NavigationView
        navView.setNavigationItemSelectedListener(onNavigationItemSelectedListener)

        // Launch the Application Settings ViewModel
        val repository = SettingsRepository(preferencesDataStore)
        val settingsFactory = SettingsViewModel.SettingsViewModelFactory(repository)
        settings = ViewModelProvider(this, settingsFactory)[SettingsViewModel::class.java]

        // Bind user data displayed in the Navigation Header
        setUserHeader()

        // Set listener on logout button
        listenLogoutButton()
    }

    fun applicationSettingsViewModel(): SettingsViewModel {
        return settings
    }

    fun applicationUser(): UserViewModel {
        return user
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setUserHeader() {
        val headerView = binding.navView.inflateHeaderView(R.layout.nav_header_main)

        // Fetch Header Views
        val userName = headerView.findViewById<TextView>(R.id.userName)
        val email = headerView.findViewById<TextView>(R.id.textView)

        // Instantiate the current user
        user = ViewModelProvider(this)[UserViewModel::class.java]

        user.loggedInUser.observe(this) {
            val format = "${it.name} (${it.role})"
            userName.text = format
            //email.text = it.email // Commented temporarily to show user live location

            // Update the menu items and navigate to the predefined fragment
            // according to the user role
            updateMenuItems(it.role)
            navigateToFragment(it.role)
            scheduleUpload(it.role)
        }
        user.userLocation.observe(this) {
            val coordinatesFormat =
                "[${it?.latitude} ; ${it?.longitude}]"
            email.text = coordinatesFormat
        }
    }

    private fun listenLogoutButton() {
        binding.navView.menu.findItem(R.id.signoutButton).setOnMenuItemClickListener {
            login.logout()
            finish()
            startActivity(intent)
            true
        }
    }

    // Update the menu items according to the user roledisplayFragment
    private fun updateMenuItems(role: Role) {
        val navMenu = binding.navView.menu

        when (role) {
            Role.CLIENT -> {
                navMenu.findItem(R.id.roadBookFragment).isVisible = false
                navMenu.findItem(R.id.directoryFragment).isVisible = false
                navMenu.findItem(R.id.routeFragment).isVisible = false
                navMenu.findItem(R.id.settingsFragment).isVisible = false
                navMenu.findItem(R.id.bossMapFragment).isVisible = false
            }
            Role.COURIER -> {
                navMenu.findItem(R.id.displayFragment).isVisible = false
                navMenu.findItem(R.id.bossMapFragment).isVisible = false
            }
            else -> {
                navMenu.findItem(R.id.roadBookFragment).isVisible = true
                navMenu.findItem(R.id.directoryFragment).isVisible = true
                navMenu.findItem(R.id.routeFragment).isVisible = true
                navMenu.findItem(R.id.displayFragment).isVisible = true
                navMenu.findItem(R.id.settingsFragment).isVisible = true
            }
        }
    }

    // Depending on the role, navigate to the corresponding fragment after login
    private fun navigateToFragment(role: Role) {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(navController.graph.startDestinationId, false)
            .build()

        val destinationFragmentId = when (role) {
            Role.CLIENT -> R.id.displayFragment
            else -> {
                R.id.roadBookFragment
            }
        }
        navController.navigate(destinationFragmentId, null, navOptions)
    }

    // Schedule the upload of the database every INTERVAL_UPLOAD_TIME minutes
    private fun scheduleUpload(role: Role) {
        if (role == Role.COURIER) {
            CoroutineScope(Dispatchers.IO).launch {
                val uploadWorkRequest =
                    PeriodicWorkRequestBuilder<UploadWorker>(INTERVAL_UPLOAD_PICTURE_TIME_MINUTE, TimeUnit.MINUTES)
                        .build()
                WorkManager.getInstance(this@MainActivity).enqueue(uploadWorkRequest)
            }

            handler.postDelayed(object : Runnable {
                override fun run() {
                    // Upload courier's location to Firebase
                    uploadCourierLocation()
                    handler.postDelayed(this, INTERVAL_UPLOAD_LOCATION_TIME_SECOND * 1000)
                }
            }, INTERVAL_UPLOAD_LOCATION_TIME_SECOND * 1000)
        }
    }


    private fun uploadCourierLocation() {
        // Get the courier's current location from the location tracker
        if (user.loggedInUser.value == null) return
        val courierUID = user.loggedInUser.value!!.uid
        val location = user.userLocation.value
        val database = Firebase.database.reference.child("Location").child(courierUID)
        if (location != null) {
            // Upload the longitude and latitude to Firebase
            database.child("name").setValue(user.loggedInUser.value?.name)
            database.child("longitude").setValue(location.longitude)
            database.child("latitude").setValue(location.latitude)
        } else {
            // Delete the "courierUID" entry from the "Location" node
            database.removeValue()
        }
    }


    // Set the OnNavigationItemSelectedListener for the NavigationView
    private val onNavigationItemSelectedListener =
        NavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.signoutButton -> {
                    login.logout()
                    finish()
                    startActivity(intent)
                    true
                }
                else -> {
                    val navController = findNavController(R.id.nav_host_fragment_content_main)
                    val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)
                    if (handled) {
                        binding.drawerLayout.closeDrawers()
                    }
                    handled
                }
            }
        }
}