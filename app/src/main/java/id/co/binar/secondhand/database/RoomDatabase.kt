package id.co.binar.secondhand.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.co.binar.secondhand.data.local.AuthDao
import id.co.binar.secondhand.data.local.SellerDao
import id.co.binar.secondhand.data.local.model.AuthLocal
import id.co.binar.secondhand.data.local.model.SellerCategoryLocal
import id.co.binar.secondhand.data.local.model.SellerProductLocal
import id.co.binar.secondhand.data.local.model.SellerProductPreviewLocal
import id.co.binar.secondhand.util.TypeConverter

@Database(entities = [
    AuthLocal::class,
    SellerCategoryLocal::class,
    SellerProductLocal::class,
    SellerProductPreviewLocal::class
], version = 1, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class RoomDatabase : RoomDatabase() {
    abstract fun authDao(): AuthDao
    abstract fun sellerDao(): SellerDao
}