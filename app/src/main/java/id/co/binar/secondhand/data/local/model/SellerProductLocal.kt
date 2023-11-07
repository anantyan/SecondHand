package id.co.binar.secondhand.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import id.co.binar.secondhand.model.seller.category.GetCategoryResponse

@Entity(tableName = "tbl_product")
data class SellerProductLocal(

    @ColumnInfo(name = "image_name")
    val imageName: String? = null,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String? = null,

    @ColumnInfo(name = "user_id")
    val userId: Int? = null,

    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,

    @ColumnInfo(name = "name")
    val name: String? = null,

    @ColumnInfo(name = "base_price")
    val basePrice: Int? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: String? = null,

    @ColumnInfo(name = "location")
    val location: String? = null,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @PrimaryKey(autoGenerate = false)
    val id: Int,

    @ColumnInfo(name = "Categories")
    val categories: List<GetCategoryResponse>? = null
)
