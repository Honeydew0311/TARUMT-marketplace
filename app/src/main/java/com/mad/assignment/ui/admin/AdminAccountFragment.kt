package com.mad.assignment.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentAdminAccountBinding
import com.mad.assignment.ui.admin.AdminAccountAdminFragment
import com.mad.assignment.ui.admin.AdminAccountUserFragment

class AdminAccountFragment : Fragment(R.layout.fragment_admin_account) {

    private var _binding: FragmentAdminAccountBinding? = null
    private val binding get() = _binding!!
    private val nav by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAdminAccountBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set the default fragment to AdminFragment
        replaceFragment(AdminAccountAdminFragment())

        // Switch fragment through tab layout
        binding.tlAccount.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab!!.position == 0){
                    replaceFragment(AdminAccountAdminFragment())
                }else if(tab.position == 1){
                    replaceFragment(AdminAccountUserFragment())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
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
        fragmentTransaction.replace(R.id.fcvAccount, fragment)
        fragmentTransaction.commit()
    }
}
