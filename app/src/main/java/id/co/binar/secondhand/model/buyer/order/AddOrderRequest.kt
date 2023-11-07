package id.co.binar.secondhand.model.buyer.order

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddOrderRequest(
    @SerializedName("product_id")
    val product_id: Int? = null,

    @SerializedName("bid_price")
    val bid_price: Long? = null
) : Parcelable
