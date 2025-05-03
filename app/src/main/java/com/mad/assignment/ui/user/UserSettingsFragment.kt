package com.mad.assignment.ui.user

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.auth.User
import com.mad.assignment.MainActivity
import com.mad.assignment.Models.UserViewModel
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentUserSettingsBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class UserSettingsFragment : Fragment(R.layout.fragment_user_settings) {
    private var _binding: FragmentUserSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        val sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val darkMode = sharedPreferences.getBoolean(getString(R.string.dark_key), false)
        val notif = sharedPreferences.getBoolean(getString(R.string.notif_key), false)

        if (darkMode) {
            binding.swDark.isChecked = true
        }

        if (notif) {
            binding.swNotif.isChecked = true
        }

        binding.btnBack.setOnClickListener {
            goBack()
        }

        binding.swDark.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putBoolean(getString(R.string.dark_key), false)
                editor.apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putBoolean(getString(R.string.dark_key), true)
                editor.apply()
            }
        }

        binding.swNotif.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                editor.putBoolean(getString(R.string.notif_key), false)
                editor.apply()
            } else {
                editor.putBoolean(getString(R.string.notif_key), true)
                editor.apply()
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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