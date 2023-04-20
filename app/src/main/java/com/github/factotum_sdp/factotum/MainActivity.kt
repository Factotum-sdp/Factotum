package com.github.factotum_sdp.factotum

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.github.factotum_sdp.factotum.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val auth: FirebaseAuth = getAuth()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Passing each menu ID considered as top level destinations.
        // In order to keep out the NavigateUpButton which would mask the HamburgerButton
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.roadBookFragment, R.id.pictureFragment,
                R.id.directoryFragment, R.id.loginFragment, R.id.routeFragment,
                R.id.displayFragment
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        // Bind user data displayed in the Navigation Header
        setUserHeader()

        // Set listener on logout button
        listenLogoutButton()
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
        val user = ViewModelProvider(this)[UserViewModel::class.java]
        binding.navView.findViewTreeLifecycleOwner()?.let { lco ->
            user.loggedInUser.observe(lco) {
                val format = "${it.displayName} (${it.role})"
                userName.text = format
                email.text = it.email
            }
        }
    }

    private fun listenLogoutButton() {
        binding.navView.menu.findItem(R.id.signoutButton).setOnMenuItemClickListener {
            auth.signOut()
            finish()
            startActivity(intent)
            true
        }
    }

    companion object {
        private var database: FirebaseDatabase = Firebase.database
        private var auth: FirebaseAuth = Firebase.auth

        fun getDatabase(): FirebaseDatabase {
            return database
        }

        fun getAuth(): FirebaseAuth {
            return auth
        }

        fun setDatabase(database: FirebaseDatabase) {
            this.database = database
        }

        fun setAuth(auth: FirebaseAuth) {
            this.auth = auth
        }
    }

}