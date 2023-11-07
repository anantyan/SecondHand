package id.co.binar.secondhand.model.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddAuthResponse(

	@SerializedName("createdAt")
	val createdAt: String? = null,

	@SerializedName("password")
	val password: String? = null,

	@SerializedName("full_name")
	val fullName: String? = null,

	@SerializedName("address")
	val address: String? = null,

	@SerializedName("image_url")
	val imageUrl: String? = null,

	@SerializedName("phone_number")
	val phoneNumber: Long? = null,

	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("email")
	val email: String? = null,

	@SerializedName("updatedAt")
	val updatedAt: String? = null
) : Parcelable
