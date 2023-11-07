package id.co.binar.secondhand.model.auth

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetAuthResponse(

	@SerializedName("access_token")
	val accessToken: String? = null,

	@SerializedName("name")
	val name: String? = null,

	@SerializedName("email")
	val email: String? = null,

	@SerializedName("id")
	val id: Int? = null
) : Parcelable
