package id.co.binar.secondhand.data.remote

import id.co.binar.secondhand.model.auth.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

    @POST("auth/login")
    suspend fun login(
        @Body field: GetAuthRequest
    ) : Response<GetAuthResponse>

    @Multipart
    @POST("auth/register")
    suspend fun register(
        @PartMap field: HashMap<String, RequestBody>
    ) : Response<AddAuthResponse>

    @GET("auth/user")
    suspend fun getAccount(
        @Header("access_token") token: String
    ) : Response<GetAuthByTokenResponse>

    @Multipart
    @PUT("auth/user")
    suspend fun updateAccount(
        @Header("access_token") token: String,
        @PartMap field: HashMap<String, RequestBody>,
        @Part image: MultipartBody.Part
    ) : Response<UpdateAuthByTokenResponse>
}