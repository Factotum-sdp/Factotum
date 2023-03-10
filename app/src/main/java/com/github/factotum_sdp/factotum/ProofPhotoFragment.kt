package com.github.factotum_sdp.factotum

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.*

class ProofPhotoFragment : Fragment() {
    private lateinit var photoName: String
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_main, container, false)

        // Check if permission to access camera is granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED) {
            val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                    val isCameraGranted = permissions[Manifest.permission.CAMERA] ?: false

                    if (isCameraGranted) {
                        openCamera()
                    } else {
                        Log.d(TAG, "Permissions denied")
                    }
                }
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        } else {
            // Permissions are already granted
            openCamera()
        }
        return view
    }


    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
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
            Log.d(TAG, "Error")
        }
    }

    private fun openCamera() {
        // Prepare the file where to save the photo
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM)
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
