package com.mad.assignment.ui.product

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.mad.assignment.Models.ChatViewModel
import com.mad.assignment.Models.ProductViewModel
import com.mad.assignment.Models.UserViewModel
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentProductSpecificBinding
import com.mad.assignment.databinding.FragmentSellPostedBinding
import com.mad.assignment.entity.Chat
import com.mad.assignment.entity.Product
import com.mad.assignment.ui.chatbox.ChatActivity
import com.mad.assignment.ui.user.UserHomeActivity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class ProductSpecificFragment : Fragment() {
    private var _binding: FragmentProductSpecificBinding? = null
    private var userHomeActivity : UserHomeActivity? = null
    private val PICK_IMAGE_REQUEST = 1
    private val auth = FirebaseAuth.getInstance()
    private var imageUrl : String = ""
    private val productViewModel : ProductViewModel by viewModels()
    private val userViewModel : UserViewModel by viewModels()
    private val chatViewModel : ChatViewModel by viewModels()
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProductSpecificBinding.inflate(inflater, container, false)
        val view = binding.root

        userHomeActivity = activity as UserHomeActivity

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            goBack()
        }

        binding.Bbuy.setOnClickListener{
            userHomeActivity?.productCount = binding.tvSpecificProductCount.text.toString().toInt()
            userHomeActivity?.addFragmentWithStack(PaymentFragment(), requireActivity())
        }

        binding.tvSpecificProductCount.text = userHomeActivity?.productCount.toString()

        binding.ivSpecificBack.setOnClickListener{
            (activity as UserHomeActivity).showNav()
            userHomeActivity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.ivFavourite.setOnClickListener{
            binding.ivNotFavourite.setVisibility(View.VISIBLE)
            binding.ivFavourite.setVisibility(View.GONE)
            Toast.makeText(context,"Removed From Favourites", Toast.LENGTH_SHORT).show()

            userViewModel.removeFromFavourite(userHomeActivity?.productId!!)
        }

        binding.ivNotFavourite.setOnClickListener{
            binding.ivFavourite.setVisibility(View.VISIBLE)
            binding.ivNotFavourite.setVisibility(View.GONE)
            Toast.makeText(context,"Added To Favourites", Toast.LENGTH_SHORT).show()

            userViewModel.addToFavourite(userHomeActivity?.productId!!)
        }

//        binding.svBuySpecificSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(newText: String?): Boolean {
//                if (newText.isNullOrEmpty())
//                {
//                    return false
//                }
//                else {
//                    userHomeActivity?.searchValue = newText
//                    userHomeActivity?.replaceFragment(BuyMenuFragment(), requireActivity())
//                }
//                // Handle search query submission
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return false
//            }
//        })

        var specificProductCount = binding.tvSpecificProductCount.text.toString().toInt()
        var productSeller : String? = null
        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        lifecycleScope.launch {
            userEmail?.let { id ->
                val user = userViewModel.getUser(id)
                if (userHomeActivity?.productId!! in user?.favList!!)
                {
                    binding.ivFavourite.setVisibility(View.VISIBLE)
                    binding.ivNotFavourite.setVisibility(View.GONE)
                }
                else
                {
                    binding.ivNotFavourite.setVisibility(View.VISIBLE)
                    binding.ivFavourite.setVisibility(View.GONE)
                }
            }
        }

        lifecycleScope.launch {
            val productID = userHomeActivity?.productId
//            val productStatus = intent.getStringExtra("productStatus")
//            binding.btnDelete.visibility = if (productID != null && productStatus != null) View.VISIBLE else View.GONE
//            binding.btnPost.text = if (productID != null && productStatus != null) "Save" else "Post"

            productID?.let { id ->
                val product = productViewModel.getProduct(id)

                imageUrl = product?.productImage ?: ""
                Picasso.get().load(product?.productImage).into(binding.IVspecificProductImage)
                binding.TVspecificProductPrice.text =
                    String.format(getString(R.string.myr) + " %.2f", product?.productPrice)
//                binding.etnProductQty.setText(product?.productQty.toString())
//                binding.sCategory.setSelection(product?.productCategory ?: 0)
//                updateVisibility(product?.productCategory ?: 0)
//                binding.sCategory.isEnabled = false
                binding.TVspecificProductName.text = product?.productName

                binding.TVproductSpecification.text = String.format(
                    if (product?.productSize != null && product.productSize.toString() != "") {
                        "Size : " + product.productSize.toString() + "\n"
                    } else {
                        ""
                    } +
                            if (product?.productCondition != null && product.productCondition.toString() != "") {
                                "Condition : " + product.productCondition.toString() + " out of 10 \n"
                            } else {
                                ""
                            } +
//                    if (product?.productCategory != null && product.productCategory.toString() != ""){
//                        "Category : " + product.productCategory.toString() + "\n"
//                    } else {""} +
//                    if (product?.productType != null && product.productType.toString() != ""){
//                        "Type : " + product.productType.toString() + "\n"
//                    } else {""} +
                            if (product?.productBrand != null && product.productBrand.toString() != "") {
                                "Brand : " + product.productBrand.toString() + "\n"
                            } else {
                                ""
                            } +
                            if (product?.productModel != null && product.productModel.toString() != "") {
                                "Model : " + product.productModel.toString() + "\n"
                            } else {
                                ""
                            } +
                            if (product?.productWidth != null && product.productWidth.toString() != "") {
                                "Width : " + product.productWidth.toString() + "\n"
                            } else {
                                ""
                            } +
                            if (product?.productHeight != null && product.productHeight.toString() != "") {
                                "Height : " + product.productHeight.toString() + "cm\n"
                            } else {
                                ""
                            } +
                            if (product?.productDepth != null && product.productDepth.toString() != "") {
                                "Depth : " + product.productDepth.toString()
                            } else {
                                ""
                            }
                )
//                binding.sProductType.setSelection(product?.productType ?: 0)
//                binding.sProductType.isEnabled = false
//                binding.etProductBrand.setText(product?.productBrand)
//                binding.sProductCondition.setSelection(product?.productCondition ?: 0)
//                binding.etProductModel.setText(product?.productModel)
//                binding.etnProductWeight.setText(product?.productWeight.toString())
//                binding.etnProductSize.setText(product?.productSize.toString())
//                binding.etnProductWidth.setText(product?.productWidth.toString())
//                binding.etnProductHeight.setText(product?.productHeight.toString())
//                binding.etnProductDepth.setText(product?.productDepth.toString())
                binding.TVspecificProductDescription.text = product?.description
//                binding.TVspecificProductSellerName.text

                binding.ivAddSpecificProductCount.setOnClickListener {
                    binding.tvSpecificProductCount.text = specificProductCount.toString()
                    val qtyLeft = product?.productQtyLeft
                    if (qtyLeft != null) {
                        if (qtyLeft > specificProductCount) {
                            specificProductCount++
                            binding.tvSpecificProductCount.text = specificProductCount.toString()
                        } else {
                            Toast.makeText(
                                context,
                                "You have reached the maximum count available",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                binding.ivDeductSpecificProductCount.setOnClickListener {
                    if (specificProductCount > 1) {
                        specificProductCount--
                        binding.tvSpecificProductCount.text = specificProductCount.toString()
                    } else {
                        Toast.makeText(
                            context,
                            "You have reached the minimum count available",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                lifecycleScope.launch {
                    val userEmail = product?.productSeller

                    userEmail?.let { id ->
                        val user = userViewModel.getUser(userEmail)

                        imageUrl = user?.profileImgUrl ?: ""
                        Picasso.get().load(user?.profileImgUrl)
                            .into(binding.IVspecificProductSellerImage)
                        binding.TVspecificProductSellerName.text = user?.username

//                        binding.TVspecificProductRating.text = user?
                    }
                }

//                binding.ivSpecificChat.setOnClickListener{
//                    lifecycleScope.launch {
//                        val buyer = userViewModel.getCurrentUser()
//                        val seller = userViewModel.getUser(product?.productSeller!!)
//                        Log.d("product?.productSeller", product?.productSeller!!.toString())
//                        val chat = Chat("", null, buyer!!.toMap(), seller!!.toMap(), null, Timestamp.now())
//                        chatViewModel.newChat(chat)
//
//                        val intent = Intent(requireContext(), ChatActivity::class.java)
//                        startActivity(intent)
//                    }
//                }
//            }

                binding.ivSpecificChat.setOnClickListener {
                    lifecycleScope.launch {
                        val buyer = userViewModel.getCurrentUser()
                        val seller = userViewModel.getUser(product?.productSeller!!)
                        Log.d("product?.productSeller", product?.productSeller!!.toString())

                        // Check if a chat already exists between the buyer and seller
                        val chatExists = chatViewModel.checkChatExists(buyer!!, seller!!)

                        if (!chatExists) {
                            // If no existing chat found, create a new chat
                            val chat =
                                Chat("", null, buyer.toMap(), seller.toMap(), null, Timestamp.now())
                            chatViewModel.newChat(chat)
                        } else {
                            // Chat already exists, handle this case (e.g., show a message)
                            // You can customize this part based on your requirements
                            Toast.makeText(
                                requireContext(),
                                "Chat already exists",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

//            binding.sCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
//                    updateVisibility(position)
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                    // Do nothing if nothing is selected
//                }
//            }
        }
        // Inflate the layout for this fragment
        return view
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