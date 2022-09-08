package com.xirtam.imgchangex

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min
import kotlin.math.sqrt


class XImageView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {


    private var bitmap: Bitmap? = null
    private var paint: Paint = Paint()

    private var withMesh = false
    private val WIDTH = 20
    private val HEIGHT = 20
    private var srcWidth = 0
    private var srcHeight = 0
    private var srcScale = 0f
    private val COUNT = (WIDTH + 1) * (HEIGHT + 1)
    private var verts = FloatArray(COUNT * 2)

    private var withLines = false
    var interval = 15
    var lineWidth = 2

    private var withBin = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let {
            canvas.translate((width - it.width) / 2f, 0f)

            paint.color = 0
            paint.alpha = 0xFF

            if (withMesh) {
                canvas.drawBitmapMesh(it, WIDTH, HEIGHT, verts, 0, null, 0, null);
            } else {
                canvas.drawBitmap(it, 0f, 0f, paint)
            }

            if (withLines) {

//                paint.color = 0xee2222
                paint.color = 0x222222
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

        srcWidth = bitmap.width
        srcHeight = bitmap.height
        val scaleWight = width * 1f / bitmap.width
        val scaleHeight = height * 1f / bitmap.height
        val scale = min(scaleHeight, scaleWight)
        srcScale = scale
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        this.bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        initDistortion()
    }

    private fun initDistortion() {
        bitmap?.let { bitmap ->
            val bitmapWidth = bitmap.width.toFloat()
            val bitmapHeight = bitmap.height.toFloat()
            var index = 0
            for (y in 0..HEIGHT) {
                val fy = bitmapHeight * y / HEIGHT
                for (x in 0..WIDTH) {
                    val fx = bitmapWidth * x / WIDTH
                    verts[index * 2] = fx
                    verts[index * 2 + 1] = fy
                    index += 1
                }
            }
        }
    }

    fun distortion() {
        withMesh = true
        bitmap?.let {
            for (i in 0..it.width / 50) {
                for (j in 0..it.height / 50) {
                    warp(50f * i, 50f * j)
                }
            }
        }
        this.invalidate()
    }

    private fun warp(cx: Float, cy: Float) {
        var i = 0
        while (i < COUNT * 2) {
            val dx: Float = cx - verts[i]
            val dy: Float = cy - verts[i + 1]
            val dd = dx * dx + dy * dy
            val d = sqrt(dd.toDouble()).toFloat()

            val pull = 10000 / (dd * d)

            if (pull >= 1) {
                verts[i] = cx
                verts[i + 1] = cy
            } else {
                verts[i] = verts[i] + dx * pull
                verts[i + 1] = verts[i + 1] + dy * pull
            }

            i += 2
        }
    }

    fun addLines() {
        withLines = true
        this.invalidate()
    }

    fun bin() {
        bitmap?.let {
            withBin = true
            this.bitmap = zero(it)
        }
        this.invalidate()
    }

    fun zero(bm: Bitmap): Bitmap {
        val width = bm.width
        val height = bm.height
        var color: Int
        var r: Int
        var g: Int
        var b: Int
        var a: Int
        val bmp = Bitmap.createBitmap(
            width, height, Bitmap.Config.ARGB_8888
        )
        val oldPx = IntArray(width * height)
        val newPx = IntArray(width * height)
        bm.getPixels(oldPx, 0, width, 0, 0, width, height)
        for (i in 0 until width * height) {
            color = oldPx[i]
            r = Color.red(color)
            g = Color.green(color)
            b = Color.blue(color)
            a = Color.alpha(color)

            var gray = (r.toFloat() * 0.3 + g.toFloat() * 0.59 + b.toFloat() * 0.11).toInt()

            if (gray > 255) {
                gray = 255
            }
            if (gray < 180) {
                gray = 0
            }
            if (gray != 0) {
                gray = 255
            }
            newPx[i] = Color.argb(a, gray, gray, gray)
        }
        bmp.setPixels(newPx, 0, width, 0, 0, width, height)
        return bmp
    }

    fun convertToBitmap(): Bitmap? {
        bitmap?.let {
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            draw(Canvas(bmp))
            return bmp
        }
        return null
    }
}