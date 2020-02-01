package com.momentous.viewmodel


import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.momentous.ListController
import com.momentous.PAGE_SIZE
import com.momentous.model.DataModelsResponse
import com.momentous.webservice.OnListItemsGetListener
import com.momentous.webservice.getRequestedItemList
import com.momentous.webservice.itemsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class MainViewModel : ViewModel(), OnListItemsGetListener {
    var isLoading = MutableLiveData<Boolean>().apply { value = false }
    var message = MutableLiveData<String>().apply { value = null }
    val responseData = MutableLiveData<DataModelsResponse>()
    val isLoadMore = MutableLiveData<Boolean>().apply { value = true }
    var fromHost: Boolean = false
    var isAscending: Boolean? = false
    var currentPageNumber = 0
    var isReferesh = false

    fun getListFromServer() {
        isLoading.value = true
        getRequestedItemList(
            currentPageNumber,
            PAGE_SIZE,
            this@MainViewModel,
            if (fromHost) ListController.HOST_DATA.ordinal else ListController.LOCAL_DATA.ordinal
        )
    }


    override fun oError(errMessage: String?) {
        isLoading.value = false
        message.value = errMessage
    }

    fun getLiveItems() = responseData

    override fun onGetListSuccess(dataModelsResponse: DataModelsResponse) {
        isLoading.value = false
        if (isReferesh) {
            responseData.value?.dataItems?.clear()
            isReferesh = false
        }
        if (dataModelsResponse.dataItems.isNotEmpty()) {
            currentPageNumber = dataModelsResponse.pageNumber
            if (responseData.value == null) {
                responseData.value = dataModelsResponse
            } else
                responseData.value?.dataItems?.addAll(dataModelsResponse.dataItems)
            sortItems()
            //responseData.value?.dataItems?.sortBy { it.name }

            if (responseData.value?.dataItems?.size ?: 0 >= dataModelsResponse.totalElements)
                isLoadMore.value = false
        } else {
            currentPageNumber--
            message.value = dataModelsResponse.responseMessage
        }
        //isCalled=false

    }

    private fun sortItems() {
        if (isAscending == true) {
            responseData.value?.dataItems?.sortByDescending { it.name }

        } else {
            responseData.value?.dataItems?.sortBy { it.name }

        }
    }

    fun getListItems(context: Context?) {
        isLoading.value = true
        GlobalScope.launch {
            var data: DataModelsResponse? = null
            var reader: BufferedReader? = null
            val sb = StringBuffer()
            try {
                reader = BufferedReader(InputStreamReader(context?.assets?.open("list_items.json")))
                var s: String?
                s = reader.readLine()
                while (s != null) {
                    sb.append(s)
                    s = reader.readLine()
                }
                val json = sb.toString()
                val gson = Gson()
                val type = object : TypeToken<DataModelsResponse>() {}.type
                data = gson.fromJson<DataModelsResponse>(json, type)


            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                reader?.close()
                GlobalScope.launch(Dispatchers.Main) {

                    itemsData = data
                    getListFromServer()
                }
            }

        }

    }
}