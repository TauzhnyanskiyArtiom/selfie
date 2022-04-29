package com.example.selfie

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.selfie.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var photoURI: Uri
    private var cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if(it)
            binding.photo.setImageURI(photoURI)
        else
            Toast.makeText(this, "Operation was canceled!", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startApp()

    }

    private fun startApp() {
        binding.btnPhoto.setOnClickListener {
            captureImage()
        }

        binding.btnSend.setOnClickListener {
            val photoBox = binding.photo
            if (photoBox.drawable == null) {
                Toast.makeText(this, "Take a photo first!", Toast.LENGTH_SHORT).show()
            }
            else {
                sendMailIntent()
            }
        }
    }

    @Throws(IOException::class)
    private fun getImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }

    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (intent.resolveActivity(packageManager) != null) {
            val imageFile: File? = getImageFile()

            if (imageFile != null) {
                val imageURI = FileProvider.getUriForFile(
                    this,
                    "com.example.android.fileprovider",
                    imageFile)
                photoURI = imageURI

                cameraLauncher.launch(imageURI)
            }
        }
        else Toast.makeText(this, "No supported application", Toast.LENGTH_SHORT).show()

    }


    private fun sendMailIntent() {
        val intent = Intent(Intent.ACTION_SEND)

        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("hodovychenko.labs@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "КПП АИ-192 Таужняский А.В.")
        intent.putExtra(Intent.EXTRA_TEXT, "Sending nudes")
        intent.putExtra(Intent.EXTRA_STREAM, photoURI)
        intent.type = "message/rfc822"

        startActivity(Intent.createChooser(intent, "Select an application"))
    }
}