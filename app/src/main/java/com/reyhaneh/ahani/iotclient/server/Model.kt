package com.reyhaneh.ahani.iotclient.server

import android.content.Context
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


data class Response(
    val result: String,
    val reason: String?
    )

data class Row(
    val id: Int,
    val device_id: Int,
    val created: String,
    val ir: Int,
    val light: Int,
    val moisture: Int,
    val temperature: Int
)

data class DataResponse(
    val result: String = "",
    val data: List<Row> = listOf()
)

interface ServerInterface {
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(@Field("username") username: String,
                      @Field("password") password: String) : Response

    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(@Field("username") username: String,
                         @Field("password") password: String) : Response

    @GET("api/fetch_recent_record")
    suspend fun fetchRecentRecords(@Query("limit") limit: String): DataResponse

    @POST("auth/logout")
    fun logout()


    companion object {
        var serverInterface: ServerInterface? = null
        fun getInstance(context: Context, serverAddress: String, serverPort: String) : ServerInterface {
            if (serverInterface == null) {
                val cookieJar: ClearableCookieJar =
                    PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
                val okHttpClient: OkHttpClient = OkHttpClient.Builder().cookieJar(cookieJar).build()
                serverInterface = Retrofit.Builder()
                    .baseUrl("http://${serverAddress}:${serverPort}")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build().create(ServerInterface::class.java)
            }
            return serverInterface!!
        }
    }
}


fun resposneFromErrorBody(responseBody: ResponseBody): Response {
    return Gson().fromJson(responseBody.string(), Response::class.java)
}