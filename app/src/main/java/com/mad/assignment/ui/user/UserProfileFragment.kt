package com.mad.assignment.ui.user

import android.content.Intent
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
import com.mad.assignment.databinding.FragmentUserProfileBinding
import com.mad.assignment.entity.User
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()
    private var imageUrl : String = ""
    private lateinit var currentUser : User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            goBack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            currentUser = userViewModel.getCurrentUser()!!
            imageUrl = currentUser?.profileImgUrl ?: ""
            Picasso.get().load(currentUser?.profileImgUrl).into(binding.ivUser)
            binding.tvUserUsername.text = currentUser?.username
            binding.tvEmail.text = currentUser?.email
            binding.tvUserPhone.text = currentUser?.phone
            binding.tvAddress.text = currentUser?.address
            binding.tvUserDescription.text = currentUser?.status
        }

        binding.btnEdit.setOnClickListener {
            replaceFragment(UserProfileEditFragment())
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_user, fragment, fragment.toString())
        fragmentTransaction.addToBackStack(fragment.toString())
        fragmentTransaction.commit()
    }

    private fun goBack() {
        (activity as UserHomeActivity).showNav()
        val fragmentManager = requireActivity().supportFragmentManager
        val count = fragmentManager.backStackEntryCount
        if (count >= 1) {
            fragmentManager.popBackStack()
        }
    }
}