package com.mad.assignment.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mad.assignment.Models.UserViewModel
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentUserProfileEditBinding
import com.mad.assignment.entity.User
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class UserProfileEditFragment : Fragment(R.layout.fragment_user_profile_edit) {
    private var _binding: FragmentUserProfileEditBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()
    private var imageUrl : String = ""
    private lateinit var currentUser : User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserProfileEditBinding.inflate(inflater, container, false)
        val view = binding.root

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            goBack()
        }

        resetField()

        binding.btnReset.setOnClickListener {
            resetField()
        }

        binding.btnConfirm.setOnClickListener {
            if (binding.etRegisterAddress.text.toString().isNullOrBlank()) {
                binding.etRegisterAddress.error = "Please enter a valid address"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                currentUser.phone = binding.etPhNo.text.toString()
                currentUser.address = binding.etRegisterAddress.text.toString()
                currentUser.status = binding.rtAboutMe.text.toString()
                userViewModel.updateUser(currentUser)
            }

            goBack()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun resetField() {
        viewLifecycleOwner.lifecycleScope.launch {
            currentUser = userViewModel.getCurrentUser()!!
            imageUrl = currentUser?.profileImgUrl ?: ""
            Picasso.get().load(currentUser?.profileImgUrl).into(binding.ivProfilePic)
            binding.etPhNo.setText(currentUser?.phone)
            binding.etRegisterAddress.setText(currentUser?.address)
            binding.rtAboutMe.setText(currentUser?.status)
        }
    }

    private fun goBack() {
        val fragmentManager = requireActivity().supportFragmentManager
        val count = fragmentManager.backStackEntryCount
        if (count >= 1) {
            fragmentManager.popBackStack()
        }
    }

}