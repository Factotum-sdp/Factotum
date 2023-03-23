package com.github.factotum_sdp.factotum

import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.github.factotum_sdp.factotum.databinding.ActivityMainBinding
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: DatabaseReference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Firebase.database.reference
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID considered as top level destinations.
        // In order to keep out the NavigateUpButton which would mask the HamburgerButton
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.roadBookFragment, R.id.pictureFragment,
                R.id.directoryFragment, R.id.loginFragment, R.id.routeFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        // Bind user data displayed in the Navigation Header
        setUserHeader()

        // Set listener on logout button
        binding.navView.menu.findItem(R.id.signoutButton).setOnMenuItemClickListener {
            auth.signOut()
            drawerLayout.closeDrawers()
            navController.navigate(R.id.loginFragment)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
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
        val userFact =
            UserViewModel.UserViewModelFactory(
                UsersPlaceHolder.USER1.name,
                UsersPlaceHolder.USER1.email
            )
        val user = ViewModelProvider(this, userFact)[UserViewModel::class.java]
        binding.navView.findViewTreeLifecycleOwner()?.let { lco ->
            user.name.observe(lco) {
                userName.text = it
            }
            user.email.observe(lco) {
                email.text = it
            }
        }
    }

    fun getDatabaseRef(): DatabaseReference {
        return db
    }
}