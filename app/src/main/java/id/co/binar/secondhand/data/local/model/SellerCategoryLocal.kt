package id.co.binar.secondhand.data.local.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "tbl_category")
data class SellerCategoryLocal(

    @ColumnInfo(name = "createdAt")
    val createdAt: String? = null,

    @ColumnInfo(name = "name")
    val name: String? = null,

    @PrimaryKey(autoGenerate = false)
    val id: Int,

    @ColumnInfo(name = "updatedAt")
    val updatedAt: String? = null
)