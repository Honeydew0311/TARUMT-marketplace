package com.mad.assignment.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentAdminMeBinding

class AdminMeFragment : Fragment(R.layout.fragment_admin_me) {

    private var _binding: FragmentAdminMeBinding? = null
    private val binding get() = _binding!!
    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAdminMeBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fcvMe, fragment)
        fragmentTransaction.commit()
    }

    public fun getFragmentAdminMeBinding(): FragmentAdminMeBinding {
        return binding
    }

}