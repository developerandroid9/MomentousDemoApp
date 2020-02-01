package com.momentous.webservice


import com.momentous.BuildConfig
import com.momentous.ListController
import com.momentous.model.DataModelsResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

lateinit var retroFit: Retrofit
//put here your base url
const val BASE_URL = "http://192.168.43.39:8080/"

var itemsData: DataModelsResponse? = null


fun getClient(): Retrofit {
    val client: OkHttpClient = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG)
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        readTimeout(1, TimeUnit.MINUTES)
        connectTimeout(2, TimeUnit.MINUTES)
        writeTimeout(1, TimeUnit.MINUTES)
    }.build()
    retroFit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    return retroFit
}


const val SUCCESS = 200

//internal const val BAD_REQUEST = 400

//const val AUTHORIZATION = "Authorization"

interface ApiClient {
    @GET(ApiConstants.LIST_ITEMS)
    fun getListItems(@Query("limit") limit: Int, @Query("pageCount") pageCount: Int = 0): Call<DataModelsResponse>


}


fun getRequestedItemList(
    pageNumber: Int,
    pageSize: Int,
    callback: OnListItemsGetListener,
    from: Int
) {

    if (from == ListController.HOST_DATA.ordinal) {/*Gettting the data from host*/
        val webservice = getClient().create(ApiClient::class.java)
        webservice.getListItems(pageSize, pageNumber)
            .enqueue(object : Callback<DataModelsResponse> {
                override fun onFailure(call: Call<DataModelsResponse>, t: Throwable) {
                    callback.oError(t.message)
                }

                override fun onResponse(
                    call: Call<DataModelsResponse>,
                    response: Response<DataModelsResponse>
                ) {
                    if (response.code() == SUCCESS) {
                        val dataModelsResponse = response.body() as DataModelsResponse
                        callback.onGetListSuccess(dataModelsResponse)
                    } else {

                        callback.oError(response.errorBody()?.string())
                    }

                }

            })
    } else {


        val currentItems = DataModelsResponse()
        currentItems.responseCode = "1"
        currentItems.responseMessage = "Success"
        currentItems.totalElements = 40
        currentItems.pageSize = pageSize
        currentItems.pageNumber = pageNumber + 1
        currentItems.dataItems =
            itemsData?.dataItems?.subList(pageNumber * pageSize, ((pageNumber + 1) * pageSize))
                ?: mutableListOf()

        callback.onGetListSuccess(currentItems)
    }

}



