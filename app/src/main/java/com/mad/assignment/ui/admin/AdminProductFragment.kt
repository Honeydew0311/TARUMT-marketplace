package com.mad.assignment.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentAdminProductBinding
import com.mad.assignment.ui.admin.AdminPendingFragment
import com.mad.assignment.ui.admin.AdminPostedFragment

class AdminProductFragment : Fragment(R.layout.fragment_admin_product) {
    private var _binding: FragmentAdminProductBinding? = null
    private val binding get() = _binding!!
    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAdminProductBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set default fragment
        replaceFragment(AdminPendingFragment())

        // Switch fragment through tab
        binding.tlProduct.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab!!.position == 0){
                    replaceFragment(AdminPendingFragment())

                }else if(tab.position == 1){
                    replaceFragment(AdminPostedFragment())

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // do nothing
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // do nothing
            }
        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fcvProduct, fragment)
        fragmentTransaction.commit()
    }
}