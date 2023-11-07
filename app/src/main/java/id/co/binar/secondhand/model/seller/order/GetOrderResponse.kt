package id.co.binar.secondhand.model.seller.order

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetOrderResponse(

	@SerializedName("transaction_date")
	val transactionDate: String? = null,

	@SerializedName("User")
	val user: User? = null,

	@SerializedName("Product")
	val product: Product? = null,

	@SerializedName("buyer_id")
	val buyerId: Int? = null,

	@SerializedName("product_name")
	val productName: String? = null,

	@SerializedName("createdAt")
	val createdAt: String? = null,

	@SerializedName("price")
	val price: Long? = null,

	@SerializedName("product_id")
	val productId: Int? = null,

	@SerializedName("base_price")
	val basePrice: Long? = null,

	@SerializedName("image_product")
	val imageProduct: String? = null,

	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("status")
	val status: String? = null,

	@SerializedName("updatedAt")
	val updatedAt: String? = null
) : Parcelable

@Parcelize
data class User(

	@SerializedName("full_name")
	val fullName: String? = null,

	@SerializedName("address")
	val address: String? = null,

	@SerializedName("city")
	val city: String? = null,

	@SerializedName("phone_number")
	val phoneNumber: String? = null,

	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("email")
	val email: String? = null,

	@SerializedName("image_url")
	val imageUrl: String? = null
) : Parcelable

@Parcelize
data class Product(

	@SerializedName("image_name")
	val imageName: String? = null,

	@SerializedName("User")
	val user: User? = null,

	@SerializedName("user_id")
	val userId: Int? = null,

	@SerializedName("image_url")
	val imageUrl: String? = null,

	@SerializedName("name")
	val name: String? = null,

	@SerializedName("base_price")
	val basePrice: Long? = null,

	@SerializedName("description")
	val description: String? = null,

	@SerializedName("location")
	val location: String? = null,

	@SerializedName("status")
	val status: String? = null
) : Parcelable
