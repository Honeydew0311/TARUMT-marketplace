package com.mad.assignment.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentAdminAccountUserBinding

class AdminAccountUserFragment : Fragment(R.layout.fragment_admin_account_user) {
    private var _binding: FragmentAdminAccountUserBinding? = null
    private val binding get() = _binding!!
    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAdminAccountUserBinding.inflate(inflater, container, false)
        val view = binding.root


        // Declaration
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bsAccountUser)
        val filterButton = binding.imgAccountUserFilter
        val coordinatorLayout = binding.bspAccountUser

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