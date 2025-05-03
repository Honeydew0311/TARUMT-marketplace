package com.mad.assignment.ui.chatbox

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentChatDetailBinding
import com.mad.assignment.databinding.FragmentChatMenuBinding

class ChatDetailFragment : Fragment() {
    private var _binding: FragmentChatDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentChatDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}