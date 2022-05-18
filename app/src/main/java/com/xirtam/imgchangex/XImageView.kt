package com.xirtam.imgchangex

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.min
import kotlin.math.sqrt


class XImageView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {


    private var bitmap: Bitmap? = null
    private var paint: Paint = Paint()

    private var withMesh = false
    private val WIDTH = 20
    private val HEIGHT = 20
    private val COUNT = (WIDTH + 1) * (HEIGHT + 1)
    private val orig = FloatArray(COUNT * 2)
    private var verts = FloatArray(COUNT * 2)

    private var withLines = false
    var interval = 15
    var lineWidth = 2

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let {
            canvas.translate((width - it.width) / 2f, 0f)


            paint.color = 0
            paint.alpha = 0xFF

            if (withMesh) {
                canvas.drawBitmapMesh(it, 4, 4, verts, 0, null, 0, null);
                Log.d("xxxxxx", "withMesh")
            } else {
                canvas.drawBitmap(it, 0f, 0f, paint)
                Log.d("xxxxxx", "no mesh")
            }

            if (withLines) {

                paint.color = 0xee2222
                paint.alpha = 0x7f

                for (i in 0..it.height / interval) {
                    canvas.drawRect(
                        0f,
                        0f + i * interval,
                        it.width.toFloat(),
                        0f + i * interval + lineWidth,
                        paint
                    )
                }

                for (i in 0..it.width / interval / 2) {
                    canvas.drawRect(
                        0f + i * 2 * interval,
                        0f,
                        0f + i * 2 * interval + lineWidth,
                        it.height.toFloat(),
                        paint
                    )
                }
            }
        }
    }

    fun setBitmap(bitmap: Bitmap) {
        withMesh = false
        withLines = false

        val scaleWight = width * 1f / bitmap.width
        val scaleHeight = height * 1f / bitmap.height
        val scale = min(scaleHeight, scaleWight)

        val matrix = Matrix()
        matrix.postScale(scale, scale)
        this.bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        initDistortion()
    }

    private fun initDistortion() {
        bitmap?.let { bitmap ->
//            val bitmapWidth = bitmap.width.toFloat()
//            val bitmapHeight = bitmap.height.toFloat()
//            var index = 0
//            for (y in 0..HEIGHT) {
//                val fy = bitmapHeight * y / HEIGHT
//                for (x in 0..WIDTH) {
//                    val fx = bitmapWidth * x / WIDTH
//                    verts[index * 2] = fx
//                    orig[index * 2] = verts[index * 2]
//                    verts[index * 2 + 1] = fy
//                    orig[index * 2 + 1] = verts[index * 2 + 1]
//                    index += 1
//                }
//            }
        }
    }

    fun distortion() {
        withMesh = true
//        for (i in 1..30) {
//            Log.d("xxxxxx", i.toString())
//            for (j in 1..30) {
//                warp(100f * i, 100f * j)
//            }
//        }

        val count = 4
        val c = IntArray((count + 1) * (count + 1))
        val v = FloatArray((count + 1) * (count + 1) * 2)
        var k = 0
        for (i in 0..count) {
            var j = 0
            while (j <= count) {
                v[k] = (j * 400 + 100).toFloat()
                v[k + 1] = (i * 400 + 100).toFloat()
                j++
                k += 2
            }
        }

        verts = v

        this.invalidate()
    }

    private fun warp(cx: Float, cy: Float) {
        var i = 0
        while (i < COUNT * 2) {
            val dx: Float = cx - orig[i]
            val dy: Float = cy - orig[i + 1]
            val dd = dx * dx + dy * dy
            val d = sqrt(dd.toDouble()).toFloat()

            val pull = 80000 / (dd * d)

            if (pull >= 1) {
                verts[i] = cx
                verts[i + 1] = cy
            } else {
                verts[i] = orig[i] + dx * pull
                verts[i + 1] = orig[i + 1] + dy * pull
            }

            i += 2
        }
    }

    fun addLines() {
        withLines = true

        this.invalidate()
    }

    fun convertToBitmap(): Bitmap? {
        bitmap?.let {
            val bmp = Bitmap.createBitmap(it.width, it.height, Bitmap.Config.ARGB_8888)
            draw(Canvas(bmp))
            return bmp
        }
        return null
    }
}