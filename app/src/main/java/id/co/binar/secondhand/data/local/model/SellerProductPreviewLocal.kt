package id.co.binar.secondhand.data.local.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_product_preview")
data class SellerProductPreviewLocal(

    @PrimaryKey(autoGenerate = false)
    val id: Int? = -1,

    @ColumnInfo(name = "image_url")
    val imageUrl: Bitmap? = null,

    @ColumnInfo(name = "user_id")
    val userId: Int? = null
)
