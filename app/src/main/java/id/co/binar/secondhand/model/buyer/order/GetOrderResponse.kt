package id.co.binar.secondhand.model.buyer.order

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import id.co.binar.secondhand.model.seller.order.Product
import id.co.binar.secondhand.model.seller.order.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetOrderResponse(

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("User")
    val user: User? = null,

    @SerializedName("price")
    val price: Long? = null,

    @SerializedName("base_price")
    val basePrice: Long? = null,

    @SerializedName("product_id")
    val productId: Int? = null,

    @SerializedName("Product")
    val product: Product? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("buyer_id")
    val buyerId: Int? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
) : Parcelable