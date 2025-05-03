package com.mad.assignment.ui.sell

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentSellMenuBinding

class SellMenuFragment : Fragment() {

    private var _binding: FragmentSellMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSellMenuBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set default fragment
        replaceFragment(SellPendingFragment())

        // Switch fragment through tab
        binding.tlSell.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if(tab!!.position == 0){
                    replaceFragment(SellPendingFragment())
                }else if(tab.position == 1){
                    replaceFragment(SellPostedFragment())
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
        fragmentTransaction.replace(R.id.fcvSell, fragment)
        fragmentTransaction.commit()
    }
}