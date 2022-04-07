package com.example.selfie

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.selfie.databinding.ActivityMainBinding
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var latestUri: Uri? = null

    private val getCameraImage =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                latestUri?.let { uri ->
                    binding.selfie.setImageURI(uri)
                }
            } else {
                Log.i("INFO", "Picture captured with errors")
            }
        }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(
                baseContext,
                "We cannot do photos without your permission!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                baseContext,
                "We cannot do photos without your permission!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTake.setOnClickListener {
            grantCameraPermission()
            latestUri = getFileUri()
            getCameraImage.launch(latestUri)
        }
        binding.btnSend.setOnClickListener {
            sendMailIntent()
        }
    }

    private fun grantCameraPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                baseContext,
                Manifest.permission.CAMERA
            ) -> {
                Log.i("TAG", "Camera permissions granted already")
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun getFileUri(): Uri {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        return FileProvider.getUriForFile(this, "com.example.selfie.fileprovider", image)
    }

    private fun sendMailIntent() {
        latestUri?.let {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            emailIntent.setDataAndType(it, contentResolver.getType(it))
            emailIntent.putExtra(Intent.EXTRA_STREAM, it)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("hodovychenko.labs@gmail.com"))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "КПП Таужнянский АИ-192")
            startActivity(Intent.createChooser(emailIntent, "Send with..."))
        }
    }
}