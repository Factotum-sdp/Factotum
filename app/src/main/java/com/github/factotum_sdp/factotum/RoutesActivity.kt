package com.github.factotum_sdp.factotum

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.maps.model.LatLng

class RoutesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routes)
        val button: Button = findViewById(R.id.startRoute)
        button.setOnClickListener{
            val intent = Intent(this, MapsMarkerActivity::class.java)
            val satellite = LatLng(46.520544, 6.567825)
            val incBuilding = LatLng(46.51864288439962, 6.561958064149488)
            val postCoursRoute = Route(incBuilding, satellite)
            sendRoute(postCoursRoute, intent)
            startActivity(intent)
        }
    }

    private fun sendRoute(route: Route, intent: Intent){
        intent.putExtra("srcLat", route.src.latitude.toString())
        intent.putExtra("srcLng", route.src.longitude.toString())
        intent.putExtra("dstLat", route.dst.latitude.toString())
        intent.putExtra("dstLng", route.dst.longitude.toString())
        Log.d("DANIEL", route.src.latitude.toString())
    }
}