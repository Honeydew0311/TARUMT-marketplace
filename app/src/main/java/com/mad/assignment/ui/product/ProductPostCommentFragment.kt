package com.mad.assignment.ui.product

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentProductPostCommentBinding

class ProductPostCommentFragment : Fragment() {
    private var _binding: FragmentProductPostCommentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProductPostCommentBinding.inflate(inflater, container, false)
        val view = binding.root

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data

                binding.IVpostCommentImage.setImageURI(data?.data)
            }
        }

        binding.BpostCommentAddImage.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK)
            gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncher.launch(gallery)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}