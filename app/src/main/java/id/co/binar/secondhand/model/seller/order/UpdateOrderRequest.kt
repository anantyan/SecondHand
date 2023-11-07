package id.co.binar.secondhand.model.seller.order

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateOrderRequest(
    @SerializedName("status")
    val status: String? = null
) : Parcelable
