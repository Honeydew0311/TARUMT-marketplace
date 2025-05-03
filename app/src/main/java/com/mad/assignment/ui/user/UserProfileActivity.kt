package com.mad.assignment.ui.user

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mad.assignment.Models.UserViewModel
import com.mad.assignment.databinding.ActivityUserProfileBinding
import com.mad.assignment.entity.User
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private val userViewModel: UserViewModel by viewModels()
    private var imageUrl : String = ""
    private lateinit var user : User
    private lateinit var email : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("user")) {
            email = intent.getStringExtra("user")!!
        } else {
            lifecycleScope.launch {
                email = (userViewModel.getCurrentUser())?.email!!
            }
        }

        lifecycleScope.launch {
            user = userViewModel.getUser(email)!!
            imageUrl = user?.profileImgUrl ?: ""
            Picasso.get().load(user?.profileImgUrl).into(binding.ivUser)
            binding.tvUserUsername.text = user?.username
            binding.tvEmail.text = user?.email
            binding.tvUserPhone.text = user?.phone
            binding.tvAddress.text = user?.address
            binding.tvUserDescription.text = user?.status
        }

    }
}