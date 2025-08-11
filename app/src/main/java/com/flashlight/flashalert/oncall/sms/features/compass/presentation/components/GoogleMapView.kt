package com.flashlight.flashalert.oncall.sms.features.compass.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.utils.getBitmapDescriptorFromVector
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Composable
fun GoogleMapView(
    currentLocation: LatLng? = null,
    isDirectionEnabled: Boolean = false,
    compassAngle: Float = 0f,
    shouldCenterToCurrentLocation: Boolean = false
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val isMapInitialized = remember { mutableStateOf(false) }
    val currentMarker = remember { mutableStateOf<com.google.android.gms.maps.model.Marker?>(null) }
    val hasSetInitialCamera = remember { mutableStateOf(false) }
    val hasAddedMarker = remember { mutableStateOf(false) }
    val googleMapRef = remember { mutableStateOf<com.google.android.gms.maps.GoogleMap?>(null) }

    DisposableEffect(lifecycle) {
        val observer = object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                mapView.onCreate(null)
            }

            override fun onStart(owner: LifecycleOwner) {
                mapView.onStart()
            }

            override fun onResume(owner: LifecycleOwner) {
                mapView.onResume()
            }

            override fun onPause(owner: LifecycleOwner) {
                mapView.onPause()
            }

            override fun onStop(owner: LifecycleOwner) {
                mapView.onStop()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                mapView.onDestroy()
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    // Handle center to current location command
    LaunchedEffect(shouldCenterToCurrentLocation, currentLocation) {
        if (shouldCenterToCurrentLocation && currentLocation != null && googleMapRef.value != null) {
            googleMapRef.value?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    currentLocation,
                    18f
                )
            )
        }
    }

    // Update marker icon and rotation when direction mode changes
    LaunchedEffect(isDirectionEnabled, currentMarker.value) {
        currentMarker.value?.let { marker ->
            if (isDirectionEnabled) {
                // Arrow mode: use direction icon and rotate
                marker.setIcon(
                    getBitmapDescriptorFromVector(
                        context,
                        R.drawable.ic_current_position_direction
                    )
                )
                marker.rotation = compassAngle
                marker.setAnchor(0.5f, 0.5f) // Center the rotation point
            } else {
                // Location mode: use location icon and no rotation
                marker.setIcon(
                    getBitmapDescriptorFromVector(
                        context,
                        R.drawable.ic_current_position
                    )
                )
                marker.rotation = 0f
                marker.setAnchor(0.5f, 0.5f) // Bottom center for location icon
            }
        }
    }

    // Update marker rotation when compass angle changes (only in direction mode)
    LaunchedEffect(compassAngle, isDirectionEnabled, currentMarker.value) {
        if (isDirectionEnabled) {
            currentMarker.value?.let { marker ->
                marker.rotation = compassAngle
            }
        }
    }

    // Update marker position when location changes (only after map is initialized)
    LaunchedEffect(currentLocation, isMapInitialized.value) {
        if (isMapInitialized.value && currentLocation != null) {
            mapView.getMapAsync { googleMap ->
                if (currentMarker.value != null) {
                    // Update existing marker position
                    currentMarker.value?.position = currentLocation
                } else if (!hasAddedMarker.value) {
                    // Add new marker only if not already added
                    val markerOptions = MarkerOptions()
                        .position(currentLocation)
                        .title("Current Location")
                        .snippet("Your current position")
                        .draggable(false)
                        .visible(true)
                        .zIndex(1000f)

                    // Set icon based on direction mode
                    if (isDirectionEnabled) {
                        markerOptions
                            .icon(
                                getBitmapDescriptorFromVector(
                                    context,
                                    R.drawable.ic_current_position_direction
                                )
                            )
                            .rotation(compassAngle)
                            .anchor(0.5f, 0.5f) // Center the rotation point
                    } else {
                        markerOptions
                            .icon(
                                getBitmapDescriptorFromVector(
                                    context,
                                    R.drawable.ic_current_position
                                )
                            )
                            .anchor(0.5f, 0.5f) // Bottom center for location icon
                    }

                    val marker = googleMap.addMarker(markerOptions)
                    currentMarker.value = marker
                    hasAddedMarker.value = true
                }

                // Only move camera if this is the initial setup
                if (!hasSetInitialCamera.value) {
                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            currentLocation,
                            18f
                        )
                    )
                    hasSetInitialCamera.value = true
                }
            }
        }
    }

    AndroidView(factory = {
        mapView.apply {
            getMapAsync { googleMap ->
                // Store googleMap reference for external access
                googleMapRef.value = googleMap

                // Enable map controls
                googleMap.uiSettings.isMyLocationButtonEnabled = false
                googleMap.uiSettings.isScrollGesturesEnabled = true
                googleMap.uiSettings.isZoomGesturesEnabled = true

                // Mark map as initialized
                isMapInitialized.value = true

                if (currentLocation != null && !hasSetInitialCamera.value) {
                    // If we have current location, use it
                    googleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            currentLocation,
                            18f
                        )
                    )

                    // Add user location marker
                    val markerOptions = MarkerOptions()
                        .position(currentLocation)
                        .title("Current Location")
                        .snippet("Your current position")
                        .draggable(false)
                        .visible(true)
                        .zIndex(1000f)

                    // Set icon based on direction mode
                    if (isDirectionEnabled) {
                        markerOptions
                            .icon(
                                getBitmapDescriptorFromVector(
                                    context,
                                    R.drawable.ic_current_position_direction
                                )
                            )
                            .rotation(compassAngle)
                            .anchor(0.5f, 0.5f) // Center the rotation point
                    } else {
                        markerOptions
                            .icon(
                                getBitmapDescriptorFromVector(
                                    context,
                                    R.drawable.ic_current_position
                                )
                            )
                            .anchor(0.5f, 0.5f) // Bottom center for location icon
                    }

                    val marker = googleMap.addMarker(markerOptions)
                    currentMarker.value = marker
                    hasAddedMarker.value = true

                    hasSetInitialCamera.value = true
                }
            }
        }
    }, modifier = Modifier.fillMaxSize())
}