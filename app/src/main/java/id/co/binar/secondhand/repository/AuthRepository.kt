package id.co.binar.secondhand.repository

import androidx.room.withTransaction
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import id.co.binar.secondhand.data.local.AuthDao
import id.co.binar.secondhand.data.local.SellerDao
import id.co.binar.secondhand.data.local.model.AuthLocal
import id.co.binar.secondhand.data.remote.AuthApi
import id.co.binar.secondhand.database.RoomDatabase
import id.co.binar.secondhand.model.ErrorResponse
import id.co.binar.secondhand.model.auth.*
import id.co.binar.secondhand.util.DataStoreManager
import id.co.binar.secondhand.util.Resource
import id.co.binar.secondhand.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val authDao: AuthDao,
    private val sellerDao: SellerDao,
    val store: DataStoreManager,
    private val db: RoomDatabase
) {
    suspend fun logout() {
        authDao.logout()
        sellerDao.removeProductHome()
        FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/${store.getUsrId()}")
            .addOnSuccessListener {
                store.clear()
            }
    }

    fun login(field: GetAuthRequest): Flow<Resource<GetAuthResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = authApi.login(field)
            if (response.isSuccessful) {
                response.body()?.let {
                    authDao.setAccount(
                        AuthLocal(
                            id = it.id!!,
                            fullName = it.name,
                            email = it.email,
                            token = it.accessToken
                        )
                    )
                    store.setTokenId(it.accessToken.toString())
                    store.setUsrId(it.id)
                    FirebaseMessaging.getInstance().subscribeToTopic("/topics/${it.id}").await()
                    emit(Resource.Success(it))
                }
            } else {
                response.errorBody()?.let {
                    val error = Gson().fromJson(it.string(), ErrorResponse::class.java)
                    throw Exception("${error.name} : ${error.message} - ${response.code()}")
                }
            }
        } catch (ex: Exception) {
            emit(Resource.Error(ex))
        }
    }

    fun register(field: AddAuthRequest): Flow<Resource<AddAuthResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = authApi.register(
                hashMapOf(
                    "full_name" to field.fullName.toString().toRequestBody(MultipartBody.FORM),
                    "password" to field.password.toString().toRequestBody(MultipartBody.FORM),
                    "email" to field.email.toString().toRequestBody(MultipartBody.FORM)
                )
            )
            if (response.isSuccessful) {
                response.body()?.let { emit(Resource.Success(it)) }
            } else {
                response.errorBody()?.let {
                    val error = Gson().fromJson(it.string(), ErrorResponse::class.java)
                    throw Exception("${error.name} : ${error.message} - ${response.code()}")
                }
            }
        } catch (ex: Exception) {
            emit(Resource.Error(ex))
        }
    }

    fun updateAccount(field: UpdateAuthByTokenRequest, image: MultipartBody.Part): Flow<Resource<UpdateAuthByTokenResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = authApi.updateAccount(
                store.getTokenId(),
                hashMapOf(
                    "password" to field.password.toString().toRequestBody(MultipartBody.FORM),
                    "full_name" to field.fullName.toString().toRequestBody(MultipartBody.FORM),
                    "phone_number" to field.phoneNumber.toString().toRequestBody(MultipartBody.FORM),
                    "email" to field.email.toString().toRequestBody(MultipartBody.FORM),
                    "address" to field.address.toString().toRequestBody(MultipartBody.FORM),
                    "city" to field.city.toString().toRequestBody(MultipartBody.FORM)
                ),
                image = image
            )
            if (response.isSuccessful) {
                response.body()?.let { emit(Resource.Success(it)) }
            } else {
                response.errorBody()?.let {
                    val error = Gson().fromJson(it.string(), ErrorResponse::class.java)
                    throw Exception("${error.name} : ${error.message} - ${response.code()}")
                }
            }
        } catch (ex: Exception) {
            emit(Resource.Error(ex))
        }
    }

    fun getAccount() = networkBoundResource(
        query = {
            authDao.getAccount(store.getTokenId(), store.getUsrId())
        },
        fetch = {
            authApi.getAccount(store.getTokenId())
        },
        saveFetchResult = {
            if (it.isSuccessful) {
                val response = it.body()
                db.withTransaction {
                    authDao.removeAccount(store.getTokenId(), store.getUsrId())
                    authDao.setAccount(
                        AuthLocal(
                            id = store.getUsrId(),
                            fullName = response?.fullName,
                            address = response?.address,
                            city = response?.city,
                            imageUrl = response?.imageUrl,
                            token = store.getTokenId(),
                            email = response?.email,
                            phoneNumber = response?.phoneNumber ?: 0
                        )
                    )
                }
            }
        }
    )
}