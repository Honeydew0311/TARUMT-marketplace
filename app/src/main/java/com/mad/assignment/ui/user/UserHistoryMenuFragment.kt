package com.mad.assignment.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentUserHistoryMenuBinding

class UserHistoryMenuFragment : Fragment(R.layout.fragment_user_history_menu) {
    private var _binding: FragmentUserHistoryMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserHistoryMenuBinding.inflate(inflater, container, false)
        val view = binding.root

        replaceFragment(UserHistoryBuyFragment())

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            goBack()
        }

        binding.tlHistory.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab!!.position == 0){
                    replaceFragment(UserHistoryBuyFragment())
                }else if(tab.position == 1){
                    replaceFragment(UserHistorySellFragment())
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
        fragmentTransaction.replace(R.id.fcvHistory, fragment)
        fragmentTransaction.commit()
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