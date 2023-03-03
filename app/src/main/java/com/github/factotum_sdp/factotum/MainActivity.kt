package com.github.factotum_sdp.factotum

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val topAppBar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            drawerLayout.open()
        }

        val navigationView = findViewById<com.google.android.material.navigation.NavigationView>(R.id.navigation)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selected
            menuItem.isChecked = true
            drawerLayout.close()
            true
        }
    }

}