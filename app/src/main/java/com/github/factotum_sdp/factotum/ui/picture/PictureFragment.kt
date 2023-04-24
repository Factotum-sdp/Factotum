package com.github.factotum_sdp.factotum.ui.picture

import android.Manifest
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.factotum_sdp.factotum.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.*

class PictureFragment(clientID: String) : Fragment() {

    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private lateinit var photoName: String
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference
    private val folderName: String = clientID.ifBlank { "default" }

    override fun onStart() {
        super.onStart()
        readCameraPermissionResult.launch(Manifest.permission.CAMERA)
    }

    private fun openCamera() {
        val fileProvider = getString(R.string.file_provider)
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val tempFolder = File(storageDir, folderName)
        if (!tempFolder.exists()) {
            tempFolder.mkdir()
        }

        val dateFormat = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault())
        val currentDateAndTime = dateFormat.format(Date())

        photoName = "USER_${currentDateAndTime}.jpg"
        photoFile = File(tempFolder, photoName) // Create the photo file in the temporary folder
        photoUri = FileProvider.getUriForFile(requireContext(), fileProvider, photoFile)

        // Launch the camera given the URI of the photo
        takePictureAndUpload.launch(photoUri)
    }

    // Register an activity result launcher to take a picture and upload it to Firebase Storage
    private val takePictureAndUpload =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                // Upload photo to Firebase Storage
                val photoRef = storageRef.child("$folderName/$photoName")
                val uploadTask = photoRef.putFile(photoUri)

                // Register observers to listen for when the upload is done or if it fails
                uploadTask.addOnSuccessListener { photoFile.delete() }
            } else {
                photoFile.delete()
            }

            findNavController().popBackStack()
        }

    private val readCameraPermissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            findNavController().popBackStack()
        }
    }

}
