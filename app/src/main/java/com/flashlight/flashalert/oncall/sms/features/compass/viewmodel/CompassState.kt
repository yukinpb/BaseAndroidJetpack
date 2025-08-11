package com.flashlight.flashalert.oncall.sms.features.compass.viewmodel

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

data class CompassState(
    val angle: Float = 0f,
    val currentLocation: LatLng? = null,
    val isDirectionEnabled: Boolean = false,
    val isFlashEnabled: Boolean = false,
    val cameraPosition: CameraPosition = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 15f)
) 