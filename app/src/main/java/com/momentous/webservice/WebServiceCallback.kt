package com.momentous.webservice

import com.momentous.model.DataModelsResponse


interface OnListItemsGetListener {
    fun onGetListSuccess(dataModelsResponse: DataModelsResponse)
    fun oError(message: String?)
}