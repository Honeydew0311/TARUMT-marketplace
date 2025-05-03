package com.mad.assignment.ui.user

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.mad.assignment.R
import com.mad.assignment.databinding.ActivityUserHomeBinding
import com.mad.assignment.ui.product.ProductSpecificFragment

class UserHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserHomeBinding
    private val nav by lazy { supportFragmentManager.findFragmentById(R.id.nav_host_user)!!.findNavController() }
    var productId : String? = null
    var productCount : Int? = null
    var searchValue : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserHomeBinding.inflate(layoutInflater)
        getSupportActionBar()?.hide()
        setContentView(binding.root)

        if (intent.getStringExtra("productID") != null)
        {
            productId = intent.getStringExtra("productID")
            productCount = 1
            hideNav()
            addFragmentWithStack(ProductSpecificFragment(), this)
        }
        // set up navigation controller for bottom navigation
        binding.navView.setupWithNavController(nav)
    }
    
    public fun hideNav() {
        binding.navView.visibility = View.GONE
    }

    public fun showNav() {
        binding.navView.visibility = View.VISIBLE
    }

    public fun goHome() {
        showNav()
        val fragmentManager = supportFragmentManager
        val count = fragmentManager.backStackEntryCount
        while (count >= 1) {
            fragmentManager.popBackStack()
        }
    }
      
    public fun replaceFragment(fragment: Fragment, fragmentActivity: FragmentActivity){
        val fragmentManager = fragmentActivity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_user, fragment)
        fragmentTransaction.commit()
    }

    public fun addFragmentWithStack(fragment: Fragment, fragmentActivity: FragmentActivity){
        val fragmentManager = fragmentActivity.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_user, fragment)
        fragmentTransaction.addToBackStack("")
        fragmentTransaction.commit()
    }
}