package com.flashlight.flashalert.oncall.sms.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.google.android.gms.maps.model.BitmapDescriptorFactory

fun getBitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int
): com.google.android.gms.maps.model.BitmapDescriptor {
    val vectorDrawable: Drawable = ContextCompat.getDrawable(context, vectorResId)!!
    val bitmap = createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    vectorDrawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
