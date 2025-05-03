package com.mad.assignment.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mad.assignment.R
import com.mad.assignment.databinding.ActivityAdminHomeBinding

class AdminHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminHomeBinding
    private val nav by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_admin)!!.findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        getSupportActionBar()?.hide()
        setContentView(binding.root)

        // set up navigation controller for bottom navigation
        binding.navView.setupWithNavController(nav)
    }
}