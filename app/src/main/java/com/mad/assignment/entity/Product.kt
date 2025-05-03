package com.mad.assignment.entity

import com.google.firebase.Timestamp

data class Product (
    var productID : String? = null,
    var productImage : String? = null,
    var productName : String? = null,
    var productQty: Int? = 0,
    var productPrice : Double? = 0.0,
    var productType : Int? = 0,
    var productBrand : String?  = null,
    var productCondition : Int? = 0,
    var description : String?  = null,
    var productCategory : Int? = 0,
    var productModel : String?  = null,
    var productWeight: Double? = 0.0,
    var productSize : Double? = 0.0,
    var productWidth : Double? = 0.0,
    var productHeight : Double? = 0.0,
    var productDepth : Double? = 0.0,
    var productStatus : String? = null,
    var productTimestamp: Timestamp? = null,
    var productQtyLeft: Int? = 0,
    var productSeller: String? = null
)

{
//    constructor() : this("", "", "", 0, 0.0, 0, "", 0, "", 0, "", 0.0, 0.0, 0.0, 0.0, 0.0, "", null, 0, "")
}