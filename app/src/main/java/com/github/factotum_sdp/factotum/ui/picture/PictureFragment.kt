package com.github.factotum_sdp.factotum.ui.picture

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.factotum_sdp.factotum.databinding.FragmentPictureBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.*

class PictureFragment : Fragment() {

    private var _binding: FragmentPictureBinding? = null
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(PictureViewModel::class.java)

        _binding = FragmentPictureBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    private fun openCamera() {
        val fileProvider = "com.github.factotum_sdp.factotum.file-provider"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photoName = "IMG_${UUID.randomUUID()}.jpg"
        val photoFile = File.createTempFile(photoName, ".jpg", storageDir)
        val photoUri = FileProvider.getUriForFile(requireContext(), fileProvider, photoFile)

        // Register an activity result launcher to take a picture and upload it to Firebase Storage
        val takePictureAndUpload =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
                if (result) {
                    // Upload photo to Firebase Storage
                    val photoRef = storageRef.child(photoName)
                    val uploadTask = photoRef.putFile(photoUri)

                    // Register observers to listen for when the upload is done or if it fails
                    uploadTask.addOnSuccessListener { photoFile.delete() }
                } else {
                    photoFile.delete()
                }
            }

        // Launch the camera given the URI of the photo
        takePictureAndUpload.launch(photoUri)
    }


    companion object {
        private const val TAG = "ProofPhotoFragment: "
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}