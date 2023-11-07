package id.co.binar.secondhand.data.remote

import id.co.binar.secondhand.model.notification.NotificationUsers
import id.co.binar.secondhand.util.Constant
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationFCMApi {

    @Headers("Authorization: key=${Constant.TOKEN_NOTIF}", "Content-Type:application/json")
    @POST("fcm/send")
    suspend fun sendNotif(
        @Body field: NotificationUsers
    ) : Response<ResponseBody>
}