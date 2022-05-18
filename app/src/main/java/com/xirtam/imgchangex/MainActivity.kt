package com.xirtam.imgchangex

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private var tmpUri: Uri? = null
    private lateinit var saveDoc: ActivityResultLauncher<String>
    private lateinit var openDoc: ActivityResultLauncher<Array<String>>
    private lateinit var mainImg: XImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainImg = findViewById(R.id.main_img)

        openDoc = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                tmpUri = it
                mainImg.setBitmap(bitmap)
            } else {
                Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show()
            }

        }

        saveDoc = registerForActivityResult(ActivityResultContracts.CreateDocument()) {

            val output = contentResolver.openOutputStream(it)
            output?.bufferedWriter().use {
                mainImg.convertToBitmap()?.compress(Bitmap.CompressFormat.JPEG, 80, output)
            }
            output?.flush()
            output?.close()
        }
    }

    fun onClickListener(view: View) {
        if (view.id == R.id.main_open) {
            openFile()
        } else if (view.id == R.id.main_distortion) {
            mainImg.distortion()
        } else if (view.id == R.id.main_with_line) {
            mainImg.addLines()
        } else if (view.id == R.id.main_save) {
            if (tmpUri == null) {
                Toast.makeText(this, "还没打开图片", Toast.LENGTH_SHORT).show()
            } else {
                save()
            }
        }
    }

    private fun save() {
//        mainImg.convertToBitmap()
        saveDoc.launch("${System.currentTimeMillis()}.jpg")
    }

    private fun openFile() {
        openDoc.launch(null)
    }

}