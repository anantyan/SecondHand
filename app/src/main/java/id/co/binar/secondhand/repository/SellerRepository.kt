package id.co.binar.secondhand.repository

import android.graphics.Bitmap
import androidx.room.withTransaction
import com.google.gson.Gson
import id.co.binar.secondhand.data.local.SellerDao
import id.co.binar.secondhand.data.local.model.SellerProductPreviewLocal
import id.co.binar.secondhand.data.remote.SellerApi
import id.co.binar.secondhand.database.RoomDatabase
import id.co.binar.secondhand.model.ErrorResponse
import id.co.binar.secondhand.model.seller.banner.GetBannerResponse
import id.co.binar.secondhand.model.seller.order.GetOrderResponse
import id.co.binar.secondhand.model.seller.order.UpdateOrderRequest
import id.co.binar.secondhand.model.seller.product.*
import id.co.binar.secondhand.util.DataStoreManager
import id.co.binar.secondhand.util.Resource
import id.co.binar.secondhand.util.castFromRemoteToLocal
import id.co.binar.secondhand.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class SellerRepository @Inject constructor(
    private val sellerApi: SellerApi,
    private val sellerDao: SellerDao,
    val store: DataStoreManager,
    private val db: RoomDatabase
) {
    fun getBanner() : Flow<Resource<List<GetBannerResponse>>> = flow {
        emit(Resource.Loading())
        try {
            val response = sellerApi.getBanner()
            if (response.isSuccessful) {
                emit(Resource.Success(response.body() ?: listOf()))
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

    fun getCategory() = networkBoundResource(
        query = {
            sellerDao.getCategoryHome()
        },
        fetch = {
            sellerApi.getCategory()
        },
        saveFetchResult = {
            if (it.isSuccessful) {
                val response = it.body().castFromRemoteToLocal()
                db.withTransaction {
                    sellerDao.removeCategoryHome()
                    sellerDao.setCategoryHome(response)
                }
            }
        }
    )

    fun getOrder(status: String?): Flow<Resource<List<GetOrderResponse>>> = flow {
        emit(Resource.Loading())
        try {
            val response = sellerApi.getOrder(store.getTokenId(), status)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Resource.Success(it.sortedByDescending { it.id }))
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

    fun getOrderById(id: Int): Flow<Resource<GetOrderResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = sellerApi.getOrderById(store.getTokenId(), id)
            if (response.isSuccessful) {
                response.body()?.let {
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

    fun updateOrder(id: Int, field: UpdateOrderRequest): Flow<Resource<GetOrderResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = sellerApi.updateOrder(store.getTokenId(), id, field)
            if (response.isSuccessful) {
                response.body()?.let {
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

    fun editProduct(id: Int, field: UpdateOrderRequest): Flow<Resource<GetProductResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = sellerApi.editStatusProduct(store.getTokenId(), id, field)
            if (response.isSuccessful) {
                response.body()?.let {
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

    fun getProduct() = networkBoundResource(
        query = {
            sellerDao.getProductHome()
        },
        fetch = {
            sellerApi.getProduct(store.getTokenId())
        },
        saveFetchResult = {
            if (it.isSuccessful) {
                val response = it.body().castFromRemoteToLocal()
                db.withTransaction {
                    sellerDao.removeProductHome()
                    sellerDao.setProductHome(response)
                }
            }
        }
    )

    fun addProduct(field: AddProductRequest, image: MultipartBody.Part): Flow<Resource<AddProductResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = sellerApi.addProduct(
                store.getTokenId(),
                hashMapOf(
                    "name" to field.name.toString().toRequestBody(MultipartBody.FORM),
                    "description" to field.description.toString().toRequestBody(MultipartBody.FORM),
                    "base_price" to field.basePrice.toString().toRequestBody(MultipartBody.FORM),
                    "category_ids" to field.categoryIds.toString().toRequestBody(MultipartBody.FORM),
                    "location" to field.location.toString().toRequestBody(MultipartBody.FORM)
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

    fun editProduct(id_product: Int, field: UpdateProductByIdRequest, image: MultipartBody.Part): Flow<Resource<UpdateProductByIdResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = sellerApi.editProduct(
                store.getTokenId(),
                id_product,
                hashMapOf(
                    "name" to field.name.toString().toRequestBody(MultipartBody.FORM),
                    "description" to field.description.toString().toRequestBody(MultipartBody.FORM),
                    "base_price" to field.basePrice.toString().toRequestBody(MultipartBody.FORM),
                    "category_ids" to field.categoryIds.toString().toRequestBody(MultipartBody.FORM),
                    "location" to field.location.toString().toRequestBody(MultipartBody.FORM)
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

    fun getProductById(id_product: Int): Flow<Resource<GetProductResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = sellerApi.getProductById(store.getTokenId(), id_product)
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

    fun deleteProduct(id_product: Int): Flow<Resource<ErrorResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = sellerApi.deleteProduct(store.getTokenId(), id_product)
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

    fun getProductPreview() = sellerDao.getProductPreview(store.getUsrId())

    suspend fun deleteProductPreview() = sellerDao.removeProductPreview()

    suspend fun setProductPreview(field: SellerProductPreviewLocal) = sellerDao.setProductPreview(
        SellerProductPreviewLocal(userId = store.getUsrId(), imageUrl = field.imageUrl, id = field.id)
    )
}