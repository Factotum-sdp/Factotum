package com.github.factotum_sdp.factotum.ui.picture

import android.Manifest
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.databinding.FragmentPictureBinding
import com.github.factotum_sdp.factotum.ui.roadbook.DRecordDetailsFragment
import com.github.factotum_sdp.factotum.ui.roadbook.RoadBookFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date
import java.util.Locale

class PictureFragment(clientID : String) : Fragment() {

    private var _binding: FragmentPictureBinding? = null
    private val binding get() = _binding!!
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private lateinit var photoName: String
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault())
    private var folderName: String = clientID
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference
    private val userID : String = FirebaseAuth.getInstance().currentUser?.uid.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPictureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        readCameraPermissionResult.launch(Manifest.permission.CAMERA)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createPhotoFile(): File {
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        if (folderName.isBlank()) {
            folderName = "default"
        }

        val tempFolder = File(storageDir, folderName).apply { mkdirs() }
        val currentDateAndTime = dateFormat.format(Date())

        photoName = "${userID}_${currentDateAndTime}.jpg"
        return File(tempFolder, photoName)
    }

    private fun openCamera() {
        CoroutineScope(Dispatchers.IO).launch {
            val fileProvider = getString(R.string.file_provider)
            photoFile = createPhotoFile()
            photoUri = FileProvider.getUriForFile(requireContext(), fileProvider, photoFile)
            withContext(Dispatchers.Main) {
                takePictureAndUpload.launch(photoUri)
            }
        }
    }

    private val takePictureAndUpload =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            if (result) {
                val photoRef = storageRef.child("$folderName/$photoName")
                val uploadTask = photoRef.putFile(photoUri)

                uploadTask.addOnSuccessListener {
                    photoFile.delete()
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to upload photo", Toast.LENGTH_SHORT).show()
                }
            } else {
                photoFile.delete()
            }

            findNavController().navigateUp()
        }

    private val readCameraPermissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            findNavController().navigateUp()
        }
    }
}