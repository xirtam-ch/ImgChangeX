package com.xirtam.imgchangex

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min


class XImageView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var bitmap: Bitmap? = null
    private var paint: Paint = Paint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let {
            canvas.drawBitmap(it, (width - it.width) / 2f, 0f, paint)
        }
    }

    fun setBitmap(bitmap: Bitmap) {
        val scaleWight = width * 1f / bitmap.width
        val scaleHeight = height * 1f / bitmap.height
        val scale = min(scaleHeight, scaleWight)

        val matrix = Matrix()
        matrix.postScale(scale, scale)
        this.bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}