package id.co.binar.secondhand.model.seller.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddProductRequest(

	@SerializedName("name")
	val name: String? = null,

	@SerializedName("base_price")
	val basePrice: Long? = null,

	@SerializedName("description")
	val description: String? = null,

	@SerializedName("location")
	val location: String? = null,

	@SerializedName("category_ids")
	val categoryIds: String? = null
) : Parcelable
