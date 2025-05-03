package com.mad.assignment.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentUserRegisterBinding

class UserRegisterFragment : Fragment(R.layout.fragment_user_register) {
    private var _binding: FragmentUserRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUserRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}