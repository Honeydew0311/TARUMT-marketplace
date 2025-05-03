package com.mad.assignment.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.mad.assignment.MainActivity
import com.mad.assignment.Models.UserViewModel
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentUserMeBinding
import com.mad.assignment.entity.User
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class UserMeFragment : Fragment(R.layout.fragment_user_me) {
    private var _binding: FragmentUserMeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val userViewModel: UserViewModel by viewModels()
    private var imageUrl : String = ""

    public lateinit var homeActivity : UserHomeActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserMeBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()

        viewLifecycleOwner.lifecycleScope.launch {
            val currentUser = userViewModel.getCurrentUser()
            imageUrl = currentUser?.profileImgUrl ?: ""
            Picasso.get().load(currentUser?.profileImgUrl).into(binding.ivMe)
            binding.tvMeUsername.text = currentUser?.username
            binding.tvMeDescription.text = if (currentUser?.status.isNullOrBlank()) "Your Desc" else currentUser?.status
        }

        binding.tvMeListProfile.setOnClickListener {
            (activity as UserHomeActivity).hideNav()
            replaceFragment(UserProfileFragment())
        }

        binding.tvMeListHistory.setOnClickListener {
            (activity as UserHomeActivity).hideNav()
            replaceFragment(UserHistoryMenuFragment())
        }

        binding.tvMeLogOut.setOnClickListener {
            auth.signOut()
            GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
            startActivity(Intent(requireContext(), MainActivity::class.java))
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
}