package com.momentous.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class DataModelsResponse(
    @SerializedName("content") var dataItems: MutableList<DataModels> = mutableListOf(),
    @SerializedName("responseMessage") var responseMessage: String? = null,
    @SerializedName("responseCode") var responseCode: String? = null,
    @SerializedName("number") var pageNumber: Int = 0,
    @SerializedName("pageSize") var pageSize: Int = 0,
    @SerializedName("totalElements") var totalElements: Int = 0
)

class DataModels : Serializable {
    @SerializedName("id")
    var id: String? = null
    @SerializedName("name")
    var name: String? = null
        get() {
            return field?.trim()
        }
    @SerializedName("desctiption")
    var description: String? = null
        get() {
            return field?.replace("\\s+".toRegex(), " ")?.trim()
        }
    @SerializedName("image_url")
    var imageUrl: String? = null
        get() {
            return field?.replace("\\s+".toRegex(), "")
        }
}