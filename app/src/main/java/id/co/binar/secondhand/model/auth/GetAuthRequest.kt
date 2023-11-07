package id.co.binar.secondhand.model.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetAuthRequest(

	@SerializedName("password")
	val password: String? = null,

	@SerializedName("email")
	val email: String? = null
) : Parcelable
