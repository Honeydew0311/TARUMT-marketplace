package com.mad.assignment.entity

import com.google.firebase.Timestamp

data class Transaction (
    var transactionID : String? = null,
    var deliveryAddress : String? = null,
    var deliveryStatus : String? = null,
    var merchandiseSubtotal : Double? = 0.0,
    var paymentMethod : String? = null,
    var productCount : Int? = 0,
    var product : Product? = null,
    var shippingOption :  String? = null,
    var shippingSST : Double? = 0.0,
    var shippingSubtotal : Double? = 0.0,
    var totalAmount : Double? = 0.0,
    var transactionTime : Timestamp? = null,
    var receivedTime : Timestamp? = null,
    var shippingTime : Timestamp? = null,
    var userEmail : String? = null,
)
{
    constructor() : this("", "", "", 0.0, "", 0, null, "", 0.0, 0.0, 0.0, null, null, null, "")
}
