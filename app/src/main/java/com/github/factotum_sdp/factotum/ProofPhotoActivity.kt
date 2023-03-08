package com.github.factotum_sdp.factotum


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.*

class ProofPhotoActivity : AppCompatActivity() {
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference
    private var photoFileName: String = ""
    private var photoUri: Uri = Uri.EMPTY

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
                ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE)
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

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            // Upload photo to Firebase Storage
            val photoRef = storageRef.child(photoFileName)
            val uploadTask = photoRef.putFile(photoUri)

            // Register observers to listen for when the upload is done or if it fails
            uploadTask.addOnSuccessListener {
                Log.d(TAG, "Photo uploaded successfully.")
            }.addOnFailureListener {
                Log.e(TAG, "Failed to upload photo: ${it.message}")
            }
        } else {
            Log.e(TAG, "Failed to take photo.")
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create a unique filename for the photo
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            photoFileName = "COURSIER_${timeStamp}"
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            // Create the file and get its URI
            val photoFile = File.createTempFile(
                photoFileName,
                ".jpg",
                storageDir
            )

            photoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.file-provider",
                photoFile
            )

            // Pass the file URI to the camera intent
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            takePictureLauncher.launch(photoUri)
        } else {
            Log.e(TAG, "No camera app found to handle the intent.")
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 1
        private const val TAG = "ProofPhotoActivity"
    }
}
