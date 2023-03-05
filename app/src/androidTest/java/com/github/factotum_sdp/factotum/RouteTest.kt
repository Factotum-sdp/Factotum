package com.github.factotum_sdp.factotum

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RouteTest {
    @Test
    fun routeRightlyCreated(){
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
}