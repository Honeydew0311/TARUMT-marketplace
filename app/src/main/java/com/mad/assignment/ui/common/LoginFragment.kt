package com.mad.assignment.ui.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.mad.assignment.MainActivity
import com.mad.assignment.Models.UserViewModel
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentLoginBinding
import com.mad.assignment.ui.user.UserHomeActivity
import com.mad.assignment.dao.UserDAO

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val userViewModel : UserViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()

        // Check if user is already logged in
        val user = auth.currentUser
        if(user != null){
            val intent: Intent = Intent(requireContext(), UserHomeActivity::class.java)
            startActivity(intent)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Login with Google
        binding.btnLogin.setOnClickListener {
            signInGoogle()
        }

        return view
    }

    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>){
        if(task.isSuccessful){
            val account: GoogleSignInAccount? = task.result
            if(account != null){
                updateUI(account)
            }
        }else{
            Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val email: String = account?.email!!
                    val domain: String = email.split("@")[1]
                    if (domain == "student.tarc.edu.my") {
                        val intent: Intent = Intent(requireContext(), UserHomeActivity::class.java)
                        intent.putExtra("email", account.email)
                        intent.putExtra("name", account.displayName)
                        userViewModel.addUser(account.email.toString(), account.displayName.toString(), account.photoUrl.toString())
                        startActivity(intent)
                    } else {
                        auth.signOut()
                        Toast.makeText(requireContext(), getString(R.string.wrong_domain), Toast.LENGTH_SHORT).show()
                        GoogleSignIn.getClient(requireActivity(), GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                    }

                    /*val intent: Intent = Intent(requireContext(), UserHomeActivity::class.java)
                    intent.putExtra("email", account.email)
                    intent.putExtra("name", account.displayName)
                    userDAO.addUser(account.email.toString(), account.displayName.toString(), account.photoUrl.toString())
                    startActivity(intent)*/

                } else {
                    Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}