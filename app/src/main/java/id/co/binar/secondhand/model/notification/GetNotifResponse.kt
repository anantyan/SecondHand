package id.co.binar.secondhand.model.notification

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import id.co.binar.secondhand.model.seller.product.GetProductResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetNotifResponse(

	@SerializedName("transaction_date")
	val transactionDate: String? = null,

	@SerializedName("read")
	val read: Boolean? = null,

	@SerializedName("image_url")
	val imageUrl: String? = null,

	@SerializedName("receiver_id")
	val receiverId: Int? = null,

	@SerializedName("buyer_name")
	val buyerName: String? = null,

	@SerializedName("Product")
	val product: Product? = null,

	@SerializedName("product_name")
	val productName: String? = null,

	@SerializedName("createdAt")
	val createdAt: String? = null,

	@SerializedName("seller_name")
	val sellerName: String? = null,

	@SerializedName("product_id")
	val productId: Int? = null,

	@SerializedName("base_price")
	val basePrice: Long? = null,

	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("bid_price")
	val bidPrice: Long? = null,

	@SerializedName("status")
	val status: String? = null,

	@SerializedName("updatedAt")
	val updatedAt: String? = null
) : Parcelable

@Parcelize
data class Product(

	@SerializedName("image_name")
	val imageName: String? = null,

	@SerializedName("createdAt")
	val createdAt: String? = null,

	@SerializedName("user_id")
	val userId: Int? = null,

	@SerializedName("image_url")
	val imageUrl: String? = null,

	@SerializedName("name")
	val name: String? = null,

	@SerializedName("base_price")
	val basePrice: Int? = null,

	@SerializedName("description")
	val description: String? = null,

	@SerializedName("location")
	val location: String? = null,

	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("status")
	val status: String? = null,

	@SerializedName("updatedAt")
	val updatedAt: String? = null
) : Parcelable

data class NotificationUsers(
	val to: String? = null,
	val data: NotificationUsersField? = null
)

data class NotificationUsersField(
	val title: String? = null,
	val message: String? = null
)