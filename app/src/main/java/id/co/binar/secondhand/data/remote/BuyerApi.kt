package id.co.binar.secondhand.data.remote

import id.co.binar.secondhand.model.buyer.order.AddOrderRequest
import id.co.binar.secondhand.model.buyer.order.GetOrderResponse
import id.co.binar.secondhand.model.buyer.product.GetProductResponse
import retrofit2.Response
import retrofit2.http.*

interface BuyerApi {

    @GET("buyer/product")
    suspend fun getProduct(
        @Query("status") status: String? = null,
        @Query("category_id") category: Int? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int? = null,
        @Query("per_page") per_page: Int? = 10
    ) : Response<List<GetProductResponse>>

    @GET("buyer/product/{id}")
    suspend fun getProductById(
        @Path("id") id: Int
    ) : Response<GetProductResponse>

    @POST("buyer/order")
    suspend fun newOrder(
        @Header("access_token") token: String,
        @Body field: AddOrderRequest
    ) : Response<GetOrderResponse>
}