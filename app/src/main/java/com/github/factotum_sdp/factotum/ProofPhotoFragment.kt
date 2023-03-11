package com.github.factotum_sdp.factotum

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.*

class ProofPhotoFragment : Fragment() {
    private lateinit var photoName: String
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    private val storage: FirebaseStorage = Firebase.storage
    private val storageRef: StorageReference = storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Check if permission to access camera is granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val permissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                    val isCameraGranted = permissions[Manifest.permission.CAMERA] ?: false
                    if (isCameraGranted) {
                        openCamera()
                    } else {
                        Toast.makeText(
                            context, "Permission to access camera denied",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        } else {
            // Permission is already granted
            openCamera()
        }
    }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                // Upload photo to Firebase Storage
                val photoRef = storageRef.child(photoName)
                val uploadTask = photoRef.putFile(photoUri)

                // Register observers to listen for when the upload is done or if it fails
                uploadTask.addOnSuccessListener {
                    // Delete photo from local storage
                    photoFile.delete()

                }.addOnFailureListener {
                    Log.e(TAG, "Failed to upload photo: ${it.message}")
                }
            } else {
                photoFile.delete()
                Toast.makeText(context, "The file couldn't be saved or created", Toast.LENGTH_LONG)
                    .show()
            }
        }

    private fun openCamera() {
        // Prepare the file where to save the photo
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        photoName = "JPEG_${UUID.randomUUID()}.jpg"

        photoFile = File.createTempFile(
            photoName,
            ".jpg",
            storageDir
        )
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "com.github.factotum_sdp.factotum.file-provider",
            photoFile
        )
        takePicture.launch(photoUri)
    }


    companion object {
        private const val TAG = "ProofPhotoFragment: "
    }

}
