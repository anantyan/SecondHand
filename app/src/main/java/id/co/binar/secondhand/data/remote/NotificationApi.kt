package id.co.binar.secondhand.data.remote

import id.co.binar.secondhand.model.notification.GetNotifResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface NotificationApi {

    @GET("notification")
    suspend fun getNotif(
        @Header("access_token") token: String
    ) : Response<List<GetNotifResponse>>

    @PATCH("notification/{id}")
    suspend fun updateNotif(
        @Header("access_token") token: String,
        @Path("id") id: Int
    ) : Response<GetNotifResponse>
}