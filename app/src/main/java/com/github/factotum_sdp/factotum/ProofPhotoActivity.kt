package com.github.factotum_sdp.factotum


import android.content.Intent
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context as AndroidContext
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.File
import java.util.*

class ProofPhotoActivity : AppCompatActivity() {
    //private var db : DatabaseReference = Firebase.database.reference

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proofphoto)

        val button: Button = findViewById(R.id.buttonProofPhoto)

        button.setOnClickListener {
            val permissions = arrayOf(Manifest.permission.CAMERA)

            val grantResults = permissions.map {
                ContextCompat.checkSelfPermission(this, it)
            }

            // Ask for permission to access camera
            if (grantResults.contains(PackageManager.PERMISSION_DENIED)) {
                ActivityCompat.requestPermissions(this, permissions,  PERMISSIONS_REQUEST_CODE)
            }

            // Ask for permission to access external storage
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }

            openCamera()
        }

    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            //TODO: Create a file to store the image
        } else {
            Log.e(TAG, "No camera app found to handle the intent.")
        }
    }



    companion object {
        private const val  PERMISSIONS_REQUEST_CODE = 1
    }

}