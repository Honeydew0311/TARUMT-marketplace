package com.mad.assignment.ui.admin

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentAdminAccountAdminBinding

class AdminAccountAdminFragment : Fragment(R.layout.fragment_admin_account_admin) {
    private var _binding: FragmentAdminAccountAdminBinding? = null
    private val binding get() = _binding!!
    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAdminAccountAdminBinding.inflate(inflater, container, false)
        val view = binding.root

        val popupLayout = inflater.inflate(R.layout.popup_window_register_admin, container, false)
        val popupWindow = PopupWindow(popupLayout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        binding.btnNewAdmin.setOnClickListener {
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        }

        // Declaration
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bsAccountAdmin)
        val filterButton = binding.imgAccountAdminFilter
        val coordinatorLayout = binding.bspAccountAdmin

        coordinatorLayout.setOnClickListener {
            if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }

        filterButton.setOnClickListener {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}