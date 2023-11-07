package id.co.binar.secondhand.data.local

import androidx.room.*
import id.co.binar.secondhand.data.local.model.AuthLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthDao {

    /**
     * tbl tbl tbl Takut Banget Loh : Authentication
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setAccount(field: AuthLocal)

    @Query("SELECT * FROM tbl_auth WHERE token=:token AND id=:id")
    fun getAccount(token: String, id: Int) : Flow<AuthLocal>

    @Query("DELETE FROM tbl_auth WHERE token=:token AND id=:id")
    suspend fun removeAccount(token: String, id: Int)

    @Query("DELETE FROM tbl_auth")
    suspend fun logout()
}