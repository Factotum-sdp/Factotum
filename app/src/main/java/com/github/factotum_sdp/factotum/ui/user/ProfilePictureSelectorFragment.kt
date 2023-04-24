package com.github.factotum_sdp.factotum.ui.user

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.databinding.FragmentProfilePictureSelectorBinding

/**
 *
 */
class ProfilePictureSelectorFragment : Fragment() {

    private var _binding: FragmentProfilePictureSelectorBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var gridView: GridView
    private lateinit var profilePicture: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePictureSelectorBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find views by ID
        gridView = binding.photoGallery
        profilePicture = binding.profilePicture

        //get all photos from gallery
        val cursor = requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )
        //get paths to all images
        val paths = mutableListOf<String>()
        if (cursor != null) {
            Log.d("ProfilePictureSelector", "cursor count: ${cursor.count}")
            while (cursor.moveToNext()) {
                if (cursor.getColumnIndex(MediaStore.Images.Media.DATA) == -1) {
                    continue
                }
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                paths.add(path)
                Log.d("ProfilePictureSelector", "path: $path")
            }
            cursor.close()
        }
        Log.d("ProfilePictureSelector", "paths: $paths")
        // Set up the grid view adapter
        // override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val adapter = PhotoAdapter(requireContext(), paths)
        gridView.adapter = adapter

        // Set up the on item click listener for the grid view
        gridView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                // Get the selected photo
                val selectedPhoto = adapter.getItem(position) as ContactsContract.Contacts.Photo

                // Set the profile picture to the selected photo
                // profilePicture.setImageResource(selectedPhoto.resourceId)
            }

        // Set up the on click listener for the edit button
        /*profile_picture_edit_button.setOnClickListener {
            // TODO: Handle the edit profile picture button click
        }*/
    }
}

class PhotoAdapter(context: Context, private val photos: List<String>) :
    ArrayAdapter<String>(context, 0, photos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_item_photo, parent, false)
        }
        val imageView = view!!.findViewById<ImageView>(R.id.photo)
        val imagePath = photos[position]
        imageView.setImageURI(Uri.parse(imagePath))
        return view
    }
}