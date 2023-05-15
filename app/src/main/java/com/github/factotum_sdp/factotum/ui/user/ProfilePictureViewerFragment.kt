package com.github.factotum_sdp.factotum.ui.user

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.factotum_sdp.factotum.UserViewModel
import com.github.factotum_sdp.factotum.databinding.FragmentProfilePictureSelectorBinding

/**
 * Fragment that represents the user's profile picture
 */
class ProfilePictureViewerFragment : Fragment() {

    private var _binding: FragmentProfilePictureSelectorBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var profilePicture: ImageView

    val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePictureSelectorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profilePicture = binding.profilePicture

        profilePicture.setImageURI(userViewModel.loggedInUser.value?.profilePicture ?: Uri.EMPTY)
    }
}