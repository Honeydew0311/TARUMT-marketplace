package com.mad.assignment.dao

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mad.assignment.entity.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProductDAO(){
    private val db = FirebaseFirestore.getInstance()
    private var productList = ArrayList<Product>()
    private val productCollection = db.collection("products")

    fun addProduct(product: Product){

        val data = hashMapOf(
            "productImage" to product.productImage,
            "productName" to product.productName,
            "productQty" to product.productQty,
            "productPrice" to product.productPrice,
            "productType" to product.productType,
            "productBrand" to product.productBrand,
            "productCondition" to product.productCondition,
            "description" to product.description,
            "productCategory" to product.productCategory,
            "productModel" to product.productModel,
            "productWeight" to product.productWeight,
            "productSize" to product.productSize,
            "productWidth" to product.productWidth,
            "productHeight" to product.productHeight,
            "productDepth" to product.productDepth,
            "productStatus" to product.productStatus,
            "productTimestamp" to FieldValue.serverTimestamp()
        )

        productCollection.add(product)
            .addOnSuccessListener { documentReference ->
                productList.add(product)
                println("DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Error adding document")
            }
    }

    suspend fun getProduct(productID: String): Product? {
        return suspendCancellableCoroutine { continuation ->
            productCollection.document(productID).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val product = document.toObject(Product::class.java)
                        product?.productID = document.id
                        continuation.resume(product)
                        Log.d(TAG, "${document.id} => ${document.data}")
                    } else {
                        Log.d(TAG, "No such document")
                        continuation.resume(null)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                    continuation.resumeWithException(exception)
                }
        }
    }

        suspend fun getProductList(): ArrayList<Product> = withContext(Dispatchers.IO) {
        return@withContext suspendCoroutine { continuation ->
            productCollection.get()
                .addOnSuccessListener { result ->
                    val productList = ArrayList<Product>()
                    for (document in result) {
                        var product = document.toObject(Product::class.java)
                        product.productID = document.id
                        productList.add(product)
                        Log.d(TAG, "${document.id}")
                    }
                    continuation.resume(productList)
                }
                .addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                    continuation.resumeWithException(exception)
                }
        }
    }

    fun deleteProduct(productID: String){
        productCollection.document(productID).delete()
            .addOnSuccessListener {
                println("DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                println("Error deleting document")
            }
    }

    fun clearProductList(){
        productList.clear()
    }

    fun updateProduct(product: Product){
        productCollection.document(product.productID!!).set(product)
            .addOnSuccessListener {
                println("DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                println("Error updating document")
            }
    }

}

//class ProductDAO(private val productList : ArrayList<Product>)
//    : RecyclerView.Adapter<ProductDAO.MyViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_view_row, parent, false)
//
//        return MyViewHolder(itemView)
//    }
//
//    override fun getItemCount(): Int {
//        return productList.size
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        val currentItem = productList[position]
//        holder.IVproductImage.setImageResource(currentItem.IVproductImage)
//        holder.TVproductName.text = currentItem.TVproductName
//        holder.TVproductPrice.text = "RM" + currentItem.TVproductPrice
//        holder.TVproductRating.text = currentItem.TVproductRating
//
//        holder.itemView.setOnClickListener{
//            val context = holder.itemView.context
//            //val intent = Intent(context, SpecificProduct::class.java)
//
//            //context.startActivity(intent)
//        }
//    }
//
//    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
//        val CLproduct : ConstraintLayout = itemView.findViewById(R.id.CLproduct)
//        val IVproductImage : ImageView = itemView.findViewById(R.id.IVproductImage)
//        val TVproductName : TextView = itemView.findViewById(R.id.TVproductName)
//        val TVproductPrice : TextView = itemView.findViewById(R.id.TVproductPrice)
//        val TVproductRating : TextView = itemView.findViewById(R.id.TVproductRating)
//    }
//}