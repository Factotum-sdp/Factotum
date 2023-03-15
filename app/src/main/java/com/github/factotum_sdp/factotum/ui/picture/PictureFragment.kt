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
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.databinding.FragmentPictureBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class PictureFragment : Fragment() {

    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private lateinit var photoName: String
    private var _binding: FragmentPictureBinding? = null
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference
    private val readCameraPermissionResult: ActivityResultLauncher<String> by requestPermission(
        Manifest.permission.CAMERA,
        granted = { openCamera() },
        denied = { Log.d("PictureFragment", getString(R.string.camera_permission_not_granted)) })


    // Register an activity result launcher to take a picture and upload it to Firebase Storage
    private val takePictureAndUpload =
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

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readCameraPermissionResult.launch(Manifest.permission.CAMERA)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val pictureViewModel =
            ViewModelProvider(this)[PictureViewModel::class.java]

        _binding = FragmentPictureBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        pictureViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }


    private fun openCamera() {
        val fileProvider = getString(R.string.file_provider)
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val dateFormat = SimpleDateFormat("dd-MM-yyyy_HH:mm:ss", Locale.getDefault())
        val currentDateAndTime = dateFormat.format(Date())

        photoName = "USER_${currentDateAndTime}.jpg"
        photoFile = File.createTempFile(photoName, ".jpg", storageDir)
        photoUri = FileProvider.getUriForFile(requireContext(), fileProvider, photoFile)

        // Launch the camera given the URI of the photo
        takePictureAndUpload.launch(photoUri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inline fun <reified R : ActivityResultLauncher<String>> Fragment.requestPermission(
        permission: String,
        noinline granted: (permission: String) -> Unit = {},
        noinline denied: (permission: String) -> Unit = {},
        noinline explained: (permission: String) -> Unit = {}

    ): ReadOnlyProperty<Fragment, R> = PermissionResultDelegate(this, permission, granted, denied, explained)

}

class PermissionResultDelegate<R : ActivityResultLauncher<String>>(
    private val fragment: Fragment, private val permission: String,
    private val granted: (permission: String) -> Unit,
    private val denied: (permission: String) -> Unit,
    private val explained: (permission: String) -> Unit
) :
    ReadOnlyProperty<Fragment, R> {

    private var permissionResult: ActivityResultLauncher<String>? = null


    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                fragment.apply {
                    permissionResult = registerForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted: Boolean ->
                        when {
                            isGranted -> granted(permission)
                            shouldShowRequestPermissionRationale(permission) -> denied(permission)
                            else -> explained(permission)
                        }
                    }
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                permissionResult = null
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): R {
        permissionResult?.let { return (it as R) }
        error("Failed to Initialize Permission")
    }

}

