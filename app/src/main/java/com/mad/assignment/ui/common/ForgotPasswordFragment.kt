package com.mad.assignment.ui.common

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentForgotPasswordBinding


class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val MY_PERMISSIONS_REQUEST_SEND_SMS = 0
    var sendBtn: Button? = null
    var txtphoneNo: EditText? = null
    var txtMessage: EditText? = null
    var phoneNo: String? = "0194971711"
    var message: String? = "FCK U!"


    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        val view = binding.root

//        sendBtn!!.setOnClickListener { sendSMSMessage() }
//        sendSMSMessage()
        sendEmail();
        return view
    }

    // Send OTP through SMS
    protected fun sendEmail() {
        Log.i("Send email", "")
        val TO = arrayOf("")
        val CC = arrayOf("")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.setData(Uri.parse("mailto:"))
        emailIntent.setType("text/plain")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO)
        emailIntent.putExtra(Intent.EXTRA_CC, CC)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here")
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."))
            Log.i("Finished sending email...", "")
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(requireActivity(),"There is no email client installed.", Toast.LENGTH_SHORT).show()
        }
    }

    // Send OTP through SMS
//    protected fun sendSMSMessage() {
////        phoneNo = txtphoneNo!!.getText().toString()
////        message = txtMessage!!.getText().toString()
//        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.SEND_SMS)) {
//
//            } else {
//                ActivityCompat.requestPermissions( requireActivity(), arrayOf<String>(Manifest.permission.SEND_SMS),MY_PERMISSIONS_REQUEST_SEND_SMS)
//            }
//        }else{
//            onRequestPermissionsResult(MY_PERMISSIONS_REQUEST_SEND_SMS, arrayOf<String>(Manifest.permission.SEND_SMS), intArrayOf(PackageManager.PERMISSION_GRANTED))
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        when (requestCode) {
//            MY_PERMISSIONS_REQUEST_SEND_SMS -> {
//                if (grantResults.size > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
//                ) {
//                    val smsManager: SmsManager = SmsManager.getDefault()
//                    smsManager.sendTextMessage(phoneNo, null, message, null, null)
//                    Toast.makeText(context, "SMS sent.", Toast.LENGTH_LONG).show()
//                } else {
//                    Toast.makeText(
//                        context,
//                        "SMS faild, please try again.",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    return
//                }
//            }
//        }
//    }
}