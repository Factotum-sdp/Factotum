package com.github.factotum_sdp.factotum.models

import DirectionsJSONParser
import android.graphics.Color
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Class that represents a route
 *
 * @constructor constructs a route object from the source latitude/longitude
 * and destination latitude/longitude
 *
 *
 * @param srcLat: Double. Latitude of the starting point
 * @param srcLon: Double. Longitude of the starting point
 * @param dstLat: Double. Latitude of the ending point
 * @param dstLon: Double. Longitude of the ending point
 */
class Route(srcLat: Double, srcLon: Double, dstLat: Double, dstLon: Double) {

    val src = LatLng(srcLat, srcLon)
    val dst = LatLng(dstLat, dstLon)
    val id = hashCode()

    constructor(src: LatLng, dst: LatLng) : this(
        src.latitude,
        src.longitude,
        dst.latitude,
        dst.longitude
    )

    /**
     * Returns the coordinates of the start and destination of the route
     *
     * @return string : sentence explaining where the route starts and ends
     */
    override fun toString(): String {
        return "The route starts at coordinates (${src.latitude}, ${src.longitude}) and finishes at coordinates (${dst.latitude}, ${dst.longitude})"
    }

    /**
     * Adds a src to the map
     *
     *  @param googleMap : map to which the route is added
     */
    fun addSrcToMap(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(this.src)
                .title("Source of $id")
        )
    }

    /**
     * Adds a dst to the map
     *
     *@param googleMap : map to which the route is added
     */
    fun addDstToMap(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(this.dst)
                .title("Destination of $id")
        )
    }

    /**
     * Draws the route on the map
     *
     * @param googleMap : map to which the route is added
     */
    fun drawRoute(googleMap: GoogleMap) {
        drawRoute(this.src, this.dst, googleMap)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun drawRoute(origin: LatLng, dest: LatLng, googleMaps: GoogleMap) {
        GlobalScope.launch(Dispatchers.Main) {
            val url = getDirectionsUrl(origin, dest)
            val data = downloadUrl(url)
            Log.d("data", data)
            val routes = parseDirections(data)
            drawPolylines(routes, googleMaps)
        }
    }

    private suspend fun downloadUrl(strUrl: String): String = withContext(Dispatchers.IO) {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()
            iStream = urlConnection.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuilder()
            var line: String?
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
        } catch (e: Exception) {
            Log.d("Exception", e.toString())
        } finally {
            iStream?.close()
            urlConnection?.disconnect()
        }
        data
    }

    private suspend fun parseDirections(data: String): List<List<LatLng>> = withContext(Dispatchers.Default) {
        val jObject = JSONObject(data)
        val parser = DirectionsJSONParser()
        parser.parse(jObject)
    }

    private fun drawPolylines(routes: List<List<LatLng>>, googleMap: GoogleMap) {
        for (path in routes) {
            val lineOptions = PolylineOptions()
            lineOptions.addAll(path)
            lineOptions.width(12f)
            lineOptions.color(Color.RED)
            lineOptions.geodesic(true)
            val polyline = googleMap.addPolyline(lineOptions)
            polyline.tag = this
        }
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        val strDest = "destination=${dest.latitude},${dest.longitude}"
        val sensor = "sensor=false"
        val mode = "mode=bicycling"
        val apiKey = "AIzaSyBEx_kuzj2xCiWvH5ewLj9LGoPznh8XTc0"
        val parameters = "$strOrigin&$strDest&$sensor&$mode&key=$apiKey"
        val output = "json"
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

}