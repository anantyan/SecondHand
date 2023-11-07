package id.co.binar.secondhand.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.co.binar.secondhand.data.local.model.SellerCategoryLocal
import id.co.binar.secondhand.data.local.model.SellerProductLocal
import id.co.binar.secondhand.data.local.model.SellerProductPreviewLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface SellerDao {
    /**
     * tbl tbl tbl Takut Banget Loh : Category
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCategoryHome(field: List<SellerCategoryLocal>)

    @Query("SELECT * FROM tbl_category")
    fun getCategoryHome() : Flow<List<SellerCategoryLocal>>

    @Query("DELETE FROM tbl_category")
    suspend fun removeCategoryHome()

    /**
     * tbl tbl tbl Takut Banget Loh : Product
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setProductHome(field: List<SellerProductLocal>)

    @Query("SELECT * FROM tbl_product")
    fun getProductHome() : Flow<List<SellerProductLocal>>

    @Query("DELETE FROM tbl_product")
    suspend fun removeProductHome()

    /**
     * tbl tbl tbl Takut Banget Loh : Product Preview
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setProductPreview(field: SellerProductPreviewLocal)

    @Query("SELECT * FROM tbl_product_preview WHERE user_id=:id")
    fun getProductPreview(id: Int) : Flow<SellerProductPreviewLocal>

    @Query("DELETE FROM tbl_product_preview")
    suspend fun removeProductPreview()
}