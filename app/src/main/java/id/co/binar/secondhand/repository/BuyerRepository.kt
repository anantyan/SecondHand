package id.co.binar.secondhand.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.withTransaction
import com.google.gson.Gson
import id.co.binar.secondhand.data.local.SellerDao
import id.co.binar.secondhand.data.remote.BuyerApi
import id.co.binar.secondhand.data.remote.SellerApi
import id.co.binar.secondhand.database.RoomDatabase
import id.co.binar.secondhand.model.ErrorResponse
import id.co.binar.secondhand.model.buyer.order.AddOrderRequest
import id.co.binar.secondhand.model.buyer.order.GetOrderResponse
import id.co.binar.secondhand.model.buyer.product.GetProductResponse
import id.co.binar.secondhand.util.DataStoreManager
import id.co.binar.secondhand.util.Resource
import id.co.binar.secondhand.util.castFromRemoteToLocal
import id.co.binar.secondhand.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BuyerRepository @Inject constructor(
    private val sellerApi: SellerApi,
    private val buyerApi: BuyerApi,
    private val sellerDao: SellerDao,
    private val store: DataStoreManager,
    private val db: RoomDatabase
) {
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

    fun getProduct(): Flow<Resource<List<GetProductResponse>>> = flow {
        emit(Resource.Loading())
        try {
            val response = buyerApi.getProduct(page = 1, per_page = 20)
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

    fun getProduct(
        category: Int? = null,
        search: String? = null
    ) = Pager(
        config = PagingConfig(pageSize = 20, maxSize = 100, enablePlaceholders = false),
        pagingSourceFactory = { ProductPagingSource(buyerApi, search, category) }
    ).flow

    fun getProductById(
        product_id: Int
    ): Flow<Resource<GetProductResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = buyerApi.getProductById(id = product_id)
            if (response.isSuccessful) {
                response.body()?.let { emit(Resource.Success(it)) } ?: throw Exception("Product tidak ditemukan!")
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

    fun newOrder(field: AddOrderRequest): Flow<Resource<GetOrderResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = buyerApi.newOrder(store.getTokenId(), field)
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
}