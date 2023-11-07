package id.co.binar.secondhand.util

object Constant {
    const val BASE_URL = "https://market-final-project.herokuapp.com/"
    const val BASE_URL_NOTIF = "https://fcm.googleapis.com"
    const val TOKEN_NOTIF = "AAAALhXDLL4:APA91bHkG-sbmP0DxtJkHybFBNNrvJ-TPVrtmQaH-GlIOzKRnmTJ970YlbqO2EGZejjvqWdfmigKJHtucZ9sEt0JDsJvXdLfTDnL_jY6N1x4G0FYxn9rVf-YAJEMqG-buhbUcY8txDUn"
    val ARRAY_PERMISSION = arrayOf(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA
    )
    val ARRAY_STATUS = arrayOf(

        /** status product */
        "available", // 0
        "seller", // 1

        /** status order */
        "pending", // 2
        "accepted", // 3
        "declined", // 4

        /** status product */
        "sold" // 5
    )
}