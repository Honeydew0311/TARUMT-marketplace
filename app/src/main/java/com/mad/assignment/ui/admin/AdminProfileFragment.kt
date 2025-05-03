package com.mad.assignment.ui.admin

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.navigation.fragment.findNavController
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentAdminProfileBinding

class AdminProfileFragment : Fragment(R.layout.fragment_admin_profile) {
    private var _binding: FragmentAdminProfileBinding? = null
    private val binding get() = _binding!!
    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        val popupLayout = inflater.inflate(R.layout.popup_window_change_password, container, false)
        val popupWindow = PopupWindow(popupLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        binding.btnChangePassword.setOnClickListener {
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        }

        return view
    }
}