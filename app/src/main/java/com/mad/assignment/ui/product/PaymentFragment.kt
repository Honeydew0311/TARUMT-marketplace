package com.mad.assignment.ui.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.firebase.Timestamp
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.mad.assignment.Models.ProductViewModel
import com.mad.assignment.Models.TransactionViewModel
import com.mad.assignment.Models.UserViewModel
import com.mad.assignment.R
import com.mad.assignment.databinding.FragmentPaymentBinding
import com.mad.assignment.entity.Product
import com.mad.assignment.entity.Transaction
import com.mad.assignment.ui.user.UserHomeActivity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class PaymentFragment : Fragment() {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    private var userHomeActivity : UserHomeActivity? = null
    private val productViewModel : ProductViewModel by viewModels()
    private val userViewModel : UserViewModel by viewModels()
    private val transactionViewModel : TransactionViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()
    private var imageUrl : String = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        val view = binding.root

        userHomeActivity = activity as UserHomeActivity

        binding.ivPaymentBack.setOnClickListener{
            userHomeActivity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.TVcheckoutProductPriceAmount.text = userHomeActivity?.productCount.toString()

        binding.BcheckoutPlaceOrder.setOnClickListener{

            val deliveryAddress = binding.tvDeliveryAddress.text.toString()
            val deliveryStatus = "notShippedOut"
            val paymentMethod = binding.ScheckoutPaymentOption.selectedItem.toString()
            val productCount = userHomeActivity?.productCount
            val productID = userHomeActivity?.productId
            val shippingOption = binding.ScheckoutDeliveryOption.selectedItem.toString()
            val transactionTime = Timestamp.now()

            lifecycleScope.launch {
                val productID = userHomeActivity?.productId
//            val productStatus = intent.getStringExtra("productStatus")
//            binding.btnDelete.visibility = if (productID != null && productStatus != null) View.VISIBLE else View.GONE
//            binding.btnPost.text = if (productID != null && productStatus != null) "Save" else "Post"

                productID?.let { id ->
                    val product = productViewModel.getProduct(id)
                    val productQtyLeft = product?.productQtyLeft
                    val finalProductCount = userHomeActivity?.productCount

                    val tempPrice = product?.productPrice

                    val merchandiseSubtotal : Double

                    if (tempPrice != null && productCount != null){
                        merchandiseSubtotal = productCount * tempPrice
                    } else
                    {
                        merchandiseSubtotal = 0.0
                    }
                    val shippingSubTotal = 4.9
                    val shippingSST = shippingSubTotal * 0.06
                    val totalAmount = merchandiseSubtotal + shippingSubTotal + shippingSST

                    if (finalProductCount != null && productQtyLeft != null && productQtyLeft >= finalProductCount) {
                        lifecycleScope.launch {
                            val transaction = Transaction(
                                "",
                                deliveryAddress,
                                deliveryStatus,
                                merchandiseSubtotal,
                                paymentMethod,
                                productCount,
                                product,
                                shippingOption,
                                shippingSST,
                                shippingSubTotal,
                                totalAmount,
                                transactionTime,
                                null,
                                null,
                                FirebaseAuth.getInstance().currentUser?.email
                            )
                            transaction.transactionTime = Timestamp.now()
                            transactionViewModel.addTransaction(transaction)
                            Toast.makeText(context,"Order Placed Successfully", Toast.LENGTH_SHORT).show()
                        }
                        lifecycleScope.launch {
                            val updateedProduct = Product(
                                product.productID,
                                product.productImage,
                                product.productName,
                                product.productQty,
                                product.productPrice,
                                product.productType,
                                product.productBrand,
                                product.productCondition,
                                product.description,
                                product.productCategory,
                                product.productModel,
                                product.productWeight,
                                product.productSize,
                                product.productWidth,
                                product.productHeight,
                                product.productDepth,
                                product.productStatus,
                                product.productTimestamp,
                                productQtyLeft - finalProductCount,
                                product.productSeller
                            )
                            productViewModel.updateProduct(updateedProduct)
                        }
                        (activity as UserHomeActivity).goHome()
                        //userHomeActivity?.replaceFragment(BuyMenuFragment(), requireActivity())
                    } else {
                        Toast.makeText(context, "Product Has Been Sold", Toast.LENGTH_SHORT).show()
                        (activity as UserHomeActivity).goHome()
                        //userHomeActivity?.replaceFragment(BuyMenuFragment(), requireActivity())
                    }
                }
            }


        }

        lifecycleScope.launch {
            val userEmail = FirebaseAuth.getInstance().currentUser?.email
            userEmail?.let { id ->
                val user = userViewModel.getUser(id)
                binding.tvDeliveryAddress.text = user?.address
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
                Picasso.get().load(product?.productImage).into(binding.IVcheckoutProductImage)
                binding.TVcheckoutProductPrice.text = String.format(getString(R.string.myr) + " %.2f", product?.productPrice)
//                binding.etnProductQty.setText(product?.productQty.toString())
//                binding.sCategory.setSelection(product?.productCategory ?: 0)
//                updateVisibility(product?.productCategory ?: 0)
//                binding.sCategory.isEnabled = false
                binding.TVcheckoutProductName.text = product?.productName

                val price = product?.productPrice
                val productCount = userHomeActivity?.productCount
                val merchandiseSubtotal : Double
                val shippingSubTotal : Double
                val shippingSST : Double
                val totalPayment : Double

                if (price != null && productCount != null)
                {
                    merchandiseSubtotal = productCount * price
                    shippingSubTotal = 4.9
                    shippingSST = shippingSubTotal * 0.06
                    totalPayment = merchandiseSubtotal + shippingSubTotal + shippingSST
                    binding.TVcheckoutProductTotalAmount.text = String.format(getString(R.string.myr) + " %.2f", merchandiseSubtotal)
                    binding.TVcheckoutShippingTotalAmount.text = String.format(getString(R.string.myr) + " %.2f", shippingSubTotal)
                    binding.TVcheckoutSSTAmount.text = String.format(getString(R.string.myr) + " %.2f", shippingSST)
                    binding.TVcheckoutTotalPaymentAmount.text = String.format(getString(R.string.myr) + " %.2f", totalPayment)
                    binding.TVcheckoutStickyBarTotalAmount.text = String.format(getString(R.string.myr) + " %.2f", totalPayment)
                }

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
//                binding.TVspecificProductDescription.text = product?.description
//                binding.TVspecificProductSellerName.text
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

        return view
    }

}