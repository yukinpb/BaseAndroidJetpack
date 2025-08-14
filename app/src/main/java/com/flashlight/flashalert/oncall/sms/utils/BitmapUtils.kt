package com.flashlight.flashalert.oncall.sms.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
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

@Composable
fun rememberDrawablePainter(drawable: Drawable?): Painter? {
    return remember(drawable) {
        drawable?.let { BitmapPainter(it.toBitmap().asImageBitmap()) }
    }
}
