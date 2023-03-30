package com.github.factotum_sdp.factotum.data.localisation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.ui.maps.MapsViewModel
import com.google.android.gms.maps.model.LatLng
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RouteTest {
    @Test
    fun routeRightlyCreatedWithDouble(){
        val srcLat = 46.5190999750367
        val srcLng = 6.566757598226489
        val dstLat = 46.52072048076863
        val dstLng = 6.567838722207785
        val route = Route(srcLat, srcLng, dstLat, dstLng)
        assertEquals(srcLat, route.src.latitude)
        assertEquals(srcLng, route.src.longitude)
        assertEquals(dstLat, route.dst.latitude)
        assertEquals(dstLng, route.dst.longitude)
    }

    fun routeRightlyUpdates(){
        val srcLat = 46.5190999750367
        val srcLng = 6.566757598226489
        val dstLat = 46.52072048076863
        val dstLng = 6.567838722207785
        val route = Route(srcLat, srcLng, dstLat, dstLng)
        assertEquals(srcLat, route.src.latitude)
        assertEquals(srcLng, route.src.longitude)
        assertEquals(dstLat, route.dst.latitude)
        assertEquals(dstLng, route.dst.longitude)
    }

    @Test
    fun routeRightlyCreatedWithLatLon(){
        val srcLat = 46.5190999750367
        val srcLng = 6.566757598226489
        val dstLat = 46.52072048076863
        val dstLng = 6.567838722207785
        val src = LatLng(srcLat, srcLng)
        val dst = LatLng(dstLat, dstLng)
        val route = Route(src, dst)
        assertEquals(srcLat, route.src.latitude)
        assertEquals(srcLng, route.src.longitude)
        assertEquals(dstLat, route.dst.latitude)
        assertEquals(dstLng, route.dst.longitude)
    }

    @Test
    fun routePrintsRightCoordinates(){
        val srcLat = 46.5190999750367
        val srcLng = 6.566757598226489
        val dstLat = 46.52072048076863
        val dstLng = 6.567838722207785
        val route = Route(srcLat, srcLng, dstLat, dstLng)
        val str = "The route starts at coordinates ($srcLat, $srcLng) and finishes at coordinates ($dstLat, $dstLng)"
        assertEquals(str, route.toString())
    }

    @Test
    fun routeViewInitializesCorrectly(){
        val mapView = MapsViewModel()
        assertEquals(0, mapView.routesState.value!!.size)
    }

    @Test
    fun routeViewAdd(){
        val mapView = MapsViewModel()
        val route = Route(10.0, 10.0, 10.0, 10.0)
        val route2 = Route(10.0, 10.0, 10.0, 10.0)
        val route3 = Route(10.0, 10.0, 10.0, 10.0)
        mapView.addRoute(route)
        mapView.addRoute(route2)
        mapView.addRoute(route3)
        assertEquals(3, mapView.routesState.value!!.size)
    }

    @Test
    fun routeViewAddAll(){
        val mapView = MapsViewModel()
        val route = Route(10.0, 10.0, 10.0, 10.0)
        val route2 = Route(10.0, 10.0, 10.0, 10.0)
        val route3 = Route(10.0, 10.0, 10.0, 10.0)
        val ls = listOf(route, route2, route3)
        mapView.addAll(ls)
        assertEquals(3, mapView.routesState.value!!.size)
    }

    @get:Rule
    val rule = InstantTaskExecutorRule()
    @Test
    fun routeViewSetsRun(){
        val mapView = MapsViewModel()
        val route = Route(10.0, 10.0, 10.0, 10.0)
        mapView.setRunRoute(route)
        assertEquals(route, mapView.runRouteState.value)
    }

    @Test
    fun routeViewDeletes(){
        val mapView = MapsViewModel()
        val route = Route(10.0, 10.0, 10.0, 10.0)
        mapView.addRoute(route)
        mapView.deleteAll()
        assertEquals(0, mapView.routesState.value!!.size)
    }

    @Test
    fun routeViewSetsLocation(){
        val mapView = MapsViewModel()
        val query =  "Lausanne"
        val location = Location(query, getApplicationContext())
        mapView.setLocation(location)
        assertEquals(location.address!!.latitude, mapView.location.value!!.address!!.latitude)
        assertEquals(location.address!!.longitude, mapView.location.value!!.address!!.longitude)

    }
}