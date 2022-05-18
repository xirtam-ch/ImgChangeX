package com.xirtam.imgchangex

import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var openDoc: ActivityResultLauncher<Array<String>>
    private lateinit var mainImg: XImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainImg = findViewById(R.id.main_img)

        openDoc = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, it)
            mainImg.setBitmap(bitmap)
        }
    }

    fun onClickListener(view: View) {
        if (view.id == R.id.main_open) {
            openFile()
        }
    }

    private fun openFile() {
        openDoc.launch(null)
    }

}