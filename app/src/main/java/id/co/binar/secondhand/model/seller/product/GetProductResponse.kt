package id.co.binar.secondhand.model.seller.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import id.co.binar.secondhand.model.seller.category.GetCategoryResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetProductResponse(

	@SerializedName("image_name")
	val imageName: String? = null,

	@SerializedName("updated_at")
	val updatedAt: String? = null,

	@SerializedName("user_id")
	val userId: Int? = null,

	@SerializedName("image_url")
	val imageUrl: String? = null,

	@SerializedName("name")
	val name: String? = null,

	@SerializedName("base_price")
	val basePrice: Int? = null,

	@SerializedName("created_at")
	val createdAt: String? = null,

	@SerializedName("location")
	val location: String? = null,

	@SerializedName("description")
	val description: String? = null,

	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("status")
	val status: String? = null,

	@SerializedName("Categories")
	val categories: List<GetCategoryResponse>? = null
) : Parcelable
