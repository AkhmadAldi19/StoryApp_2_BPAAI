package com.akhmadaldi.storyapp.utils

import org.junit.Assert
import org.junit.Test

class MapsUtilTest {
    @Test
    fun `given correct Latitude and Longitude range then should return true`() {
        val latitude = 	-8.722285
        val longitude = 129.1406238
        Assert.assertTrue(MapsUtil.latLng(latitude, longitude))
    }

    @Test
    fun `given wrong Latitude or Longitude format then should return false`() {
        val wrongLatitude = 100.2
        val wrongLongitude = 200.5
        val validLatitude = -8.722285
        val validLongitude = 129.1406238
        Assert.assertFalse(MapsUtil.latLng(wrongLatitude, wrongLongitude))
        Assert.assertFalse(MapsUtil.latLng(wrongLatitude, validLongitude))
        Assert.assertFalse(MapsUtil.latLng(validLatitude, wrongLongitude))
        Assert.assertFalse(MapsUtil.latLng(validLongitude, validLatitude))
    }
}