package com.mad.assignment.ui.sell

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mad.assignment.Models.ProductViewModel
import com.mad.assignment.Models.UserViewModel
import com.mad.assignment.R
import com.mad.assignment.dao.ProductDAO
import com.mad.assignment.databinding.ActivitySellCategoryBinding
import com.mad.assignment.entity.Product
import com.mad.assignment.entity.User
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class SellCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySellCategoryBinding
    private val PICK_IMAGE_REQUEST = 1
    private val auth = FirebaseAuth.getInstance()
    private var imageUrl : String = ""
    private val productViewModel : ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellCategoryBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        var selectedCategory = 0
        if (intent.hasExtra("category")) {
            selectedCategory = intent.getIntExtra("category", 0)
        }

        // Set the initial selection based on the category passed from the previous activity
        binding.sCategory.setSelection(selectedCategory)
        updateVisibility(selectedCategory)

        // Select image from gallery
        binding.ivProductImg.setOnClickListener {
            // Open file picker
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Set up button listener
        binding.btnPost.setOnClickListener {
            val name        = binding.etProductName.text.toString()
            val qty         = binding.etnProductQty.text.toString().toIntOrNull()?:-1
            val price       = binding.etnProductPrice.text.toString().toDoubleOrNull()?:-1.0
            val type        = binding.sProductType.selectedItemPosition
            val brand       = binding.etProductBrand.text.toString()
            val condition   = binding.sProductCondition.selectedItemPosition
            val description = binding.etProductDescription.text.toString()
            val category    = binding.sCategory.selectedItemPosition
            val model       = binding.etProductModel.text.toString()
            val weight      = binding.etnProductWeight.text.toString().ifEmpty { "0.0" }.toDoubleOrNull()?:0.0
            val size        = binding.etnProductSize.text.toString().ifEmpty { "0.0" }.toDoubleOrNull()?:0.0
            val width       = binding.etnProductWidth.text.toString().ifEmpty { "0.0" }.toDoubleOrNull()?:0.0
            val height      = binding.etnProductHeight.text.toString().ifEmpty { "0.0" }.toDoubleOrNull()?:0.0
            val depth       = binding.etnProductDepth.text.toString().ifEmpty { "0.0" }.toDoubleOrNull()?:0.0

            var isValid = false

            when {
                imageUrl.isEmpty()  -> Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                name.isEmpty()      -> binding.etProductName.error      = "Please enter a valid name"
                weight < 0          -> binding.etnProductWeight.error   = "Please enter a valid weight"
                size < 0            -> binding.etnProductSize.error     = "Please enter a valid size"
                width < 0           -> binding.etnProductWidth.error    = "Please enter a valid width"
                height < 0          -> binding.etnProductHeight.error   = "Please enter a valid height"
                depth < 0           -> binding.etnProductDepth.error    = "Please enter a valid depth"
                qty <= 0            -> binding.etnProductQty.error      = "Please enter a valid quantity"
                price <= 0          -> binding.etnProductPrice.error    = "Please enter a valid price"
                type <= 0          -> Toast.makeText(this, "Please select a valid type", Toast.LENGTH_SHORT).show()
                else -> isValid = true
            }

            if (!isValid) {
                return@setOnClickListener
            }

            val productSeller = auth.currentUser?.email
            // Create a new product object and add it to the database
            if (intent.hasExtra("productID")) {
                val productID = intent.getStringExtra("productID")
                if (productID != null) {
                    lifecycleScope.launch {
                        val product = Product(
                            productID, imageUrl, name, qty, price, type, brand, condition, description,
                            category, model, weight, size, width, height, depth, "Pending", null, qty, productSeller
                        )
                        productViewModel.updateProduct(product)
                    }
                }
            } else {
                lifecycleScope.launch {
                    val product = Product(
                        "", imageUrl, name, qty, price, type, brand, condition, description,
                        category, model, weight, size, width, height, depth, "Pending", null, qty, productSeller

                    )
                    product.productTimestamp = Timestamp.now()
                    productViewModel.addProduct(product)
                }
            }

            val intent = Intent()
            intent.putExtra("productAdded", true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        lifecycleScope.launch {
            val productID = intent.getStringExtra("productID")
            val productStatus = intent.getStringExtra("productStatus")
            binding.btnDelete.visibility = if (productID != null && productStatus == "Pending") View.VISIBLE else View.GONE
            binding.btnPost.text = if (productID != null && productStatus == "Pending") "Save" else "Post"

            productID?.let { id ->
                val product = productViewModel.getProduct(id)
                imageUrl = product?.productImage ?: ""
                Picasso.get().load(product?.productImage).into(binding.ivProductImg)
                binding.etnProductPrice.setText(product?.productPrice.toString())
                binding.etnProductQty.setText(product?.productQty.toString())
                binding.sCategory.setSelection(product?.productCategory ?: 0)
                updateVisibility(product?.productCategory ?: 0)
                binding.etProductName.setText(product?.productName)
                binding.sProductType.setSelection(product?.productType ?: 0)
                binding.etProductBrand.setText(product?.productBrand)
                binding.sProductCondition.setSelection(product?.productCondition ?: 0)
                binding.etProductModel.setText(product?.productModel)
                binding.etnProductWeight.setText(product?.productWeight.toString())
                binding.etnProductSize.setText(product?.productSize.toString())
                binding.etnProductWidth.setText(product?.productWidth.toString())
                binding.etnProductHeight.setText(product?.productHeight.toString())
                binding.etnProductDepth.setText(product?.productDepth.toString())
                binding.etProductDescription.setText(product?.description)
            }

            binding.sCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,view: View?,position: Int,id: Long) {
                    updateVisibility(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing if nothing is selected
                }
            }
        }

        lifecycleScope.launch {
            val productID = intent.getStringExtra("productID")
            val productStatus = intent.getStringExtra("productStatus")
            binding.btnDelete.visibility = if (productID != null && productStatus == "Posted") View.VISIBLE else View.GONE
            if (productID != null && productStatus == "Posted"){
                binding.btnPost.visibility = View.GONE
            }

            productID?.let { id ->
                val product = productViewModel.getProduct(id)
                imageUrl = product?.productImage ?: ""
                Picasso.get().load(product?.productImage).into(binding.ivProductImg)
                binding.etnProductPrice.setText(product?.productPrice.toString())
                binding.etnProductQty.setText(product?.productQty.toString())
                binding.sCategory.setSelection(product?.productCategory ?: 0)
                updateVisibility(product?.productCategory ?: 0)
                binding.etProductName.setText(product?.productName)
                binding.sProductType.setSelection(product?.productType ?: 0)
                binding.etProductBrand.setText(product?.productBrand)
                binding.sProductCondition.setSelection(product?.productCondition ?: 0)
                binding.etProductModel.setText(product?.productModel)
                binding.etnProductWeight.setText(product?.productWeight.toString())
                binding.etnProductSize.setText(product?.productSize.toString())
                binding.etnProductWidth.setText(product?.productWidth.toString())
                binding.etnProductHeight.setText(product?.productHeight.toString())
                binding.etnProductDepth.setText(product?.productDepth.toString())
                binding.etProductDescription.setText(product?.description)

                // make all disabled
                binding.ivProductImg.isEnabled = false
                binding.etnProductPrice.isEnabled = false
                binding.etnProductQty.isEnabled = false
                binding.sCategory.isEnabled = false
                binding.etProductName.isEnabled = false
                binding.sProductType.isEnabled = false
                binding.etProductBrand.isEnabled = false
                binding.sProductCondition.isEnabled = false
                binding.etProductModel.isEnabled = false
                binding.etnProductWeight.isEnabled = false
                binding.etnProductSize.isEnabled = false
                binding.etnProductWidth.isEnabled = false
                binding.etnProductHeight.isEnabled = false
                binding.etnProductDepth.isEnabled = false
                binding.etProductDescription.isEnabled = false
            }

            binding.sCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,view: View?,position: Int,id: Long) {
                    updateVisibility(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing if nothing is selected
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            val productID = intent.getStringExtra("productID")
            if (productID != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    productViewModel.deleteProduct(productID)
                    finish()
                }
            }
        }

    }

    // Handle image selection from gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data
            // Now you can upload the selected image to Firebase Storage
            if (selectedImageUri != null) {
                binding.ivProductImg.setImageURI(selectedImageUri)
                uploadImageToFirebaseStorage(selectedImageUri)
            }
        }
    }

    // Upload image to Firebase Storage
    private fun uploadImageToFirebaseStorage(uri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageName = UUID.randomUUID().toString() + ".jpg"
        val imageRef = storageRef.child("product_images/${auth.currentUser?.email}/$imageName")

        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
                Log.d(TAG, "Image uploaded successfully")
                // Get the download URL of the uploaded image
                imageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        imageUrl = uri.toString()
                        Log.d(TAG, "Image uploaded successfully: $imageUrl")
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Failed to get download URL: ${exception.message}")
                    }
            }
            .addOnFailureListener { exception ->
                // Handle unsuccessful uploads
                Toast.makeText(this, "Failed to upload image to Firebase Storage： ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Failed to upload image to Firebase Storage: ${exception.message}")
            }
    }

    // Adjust input fields
    private fun updateVisibility(currentCategory: Int) {
        when (currentCategory) {
            0 -> {
                // Electronics
                binding.tvProductFaculty.visibility = View.GONE
                binding.sProductFaculty.visibility = View.GONE
                binding.tvProductWidth.visibility = View.GONE
                binding.etnProductWidth.visibility = View.GONE
                binding.tvProductHeight.visibility = View.GONE
                binding.etnProductHeight.visibility = View.GONE
                binding.tvProductDepth.visibility = View.GONE
                binding.etnProductDepth.visibility = View.GONE

                binding.tvProductModel.visibility = View.VISIBLE
                binding.etProductModel.visibility = View.VISIBLE
                binding.tvProductWeight.visibility = View.VISIBLE
                binding.etnProductWeight.visibility = View.VISIBLE
                binding.tvProductSize.visibility = View.VISIBLE
                binding.etnProductSize.visibility = View.VISIBLE

                val adapter = ArrayAdapter.createFromResource(this, R.array.electronicsType, android.R.layout.simple_spinner_item)
                binding.sProductType.adapter = adapter
            }
            1 -> {
                // Furniture
                binding.tvProductFaculty.visibility = View.GONE
                binding.sProductFaculty.visibility = View.GONE
                binding.tvProductWeight.visibility = View.GONE
                binding.etnProductWeight.visibility = View.GONE
                binding.tvProductSize.visibility = View.GONE
                binding.etnProductSize.visibility = View.GONE

                binding.tvProductWidth.visibility = View.VISIBLE
                binding.etnProductWidth.visibility = View.VISIBLE
                binding.tvProductHeight.visibility = View.VISIBLE
                binding.etnProductHeight.visibility = View.VISIBLE
                binding.tvProductDepth.visibility = View.VISIBLE
                binding.etnProductDepth.visibility = View.VISIBLE

                val adapter = ArrayAdapter.createFromResource(this, R.array.housingType, android.R.layout.simple_spinner_item)
                binding.sProductType.adapter = adapter
            }
            2 -> {
                // Education
                binding.tvProductModel.visibility = View.GONE
                binding.etProductModel.visibility = View.GONE
                binding.tvProductWeight.visibility = View.GONE
                binding.etnProductWeight.visibility = View.GONE
                binding.tvProductSize.visibility = View.GONE
                binding.etnProductSize.visibility = View.GONE
                binding.tvProductWidth.visibility = View.GONE
                binding.etnProductWidth.visibility = View.GONE
                binding.tvProductHeight.visibility = View.GONE
                binding.etnProductHeight.visibility = View.GONE
                binding.tvProductDepth.visibility = View.GONE
                binding.etnProductDepth.visibility = View.GONE

                binding.tvProductFaculty.visibility = View.VISIBLE
                binding.sProductFaculty.visibility = View.VISIBLE

                val adapter = ArrayAdapter.createFromResource(this, R.array.educationType, android.R.layout.simple_spinner_item)
                binding.sProductType.adapter = adapter
            }
            3 -> {
                // Sports
                binding.tvProductWeight.visibility = View.GONE
                binding.etnProductWeight.visibility = View.GONE
                binding.tvProductSize.visibility = View.GONE
                binding.etnProductSize.visibility = View.GONE
                binding.tvProductWidth.visibility = View.GONE
                binding.etnProductWidth.visibility = View.GONE
                binding.tvProductHeight.visibility = View.GONE
                binding.etnProductHeight.visibility = View.GONE
                binding.tvProductDepth.visibility = View.GONE
                binding.etnProductDepth.visibility = View.GONE
                binding.tvProductFaculty.visibility = View.GONE
                binding.sProductFaculty.visibility = View.GONE

                binding.tvProductModel.visibility = View.VISIBLE
                binding.etProductModel.visibility = View.VISIBLE

                val adapter = ArrayAdapter.createFromResource(this, R.array.sportType, android.R.layout.simple_spinner_item)

                binding.sProductType.adapter = adapter
            }
        }
    }
}