package com.mad.assignment.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentAdminPostedBinding

class AdminPostedFragment : Fragment(R.layout.fragment_admin_posted) {
    private var _binding: FragmentAdminPostedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAdminPostedBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

}