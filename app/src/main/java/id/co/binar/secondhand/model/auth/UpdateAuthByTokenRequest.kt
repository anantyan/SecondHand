package id.co.binar.secondhand.model.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateAuthByTokenRequest(

    @SerializedName("password")
    val password: String? = null,

    @SerializedName("full_name")
    val fullName: String? = null,

    @SerializedName("phone_number")
    val phoneNumber: Long? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("city")
    val city: String? = null
) : Parcelable
