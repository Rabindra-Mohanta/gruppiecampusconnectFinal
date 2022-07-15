package com.vivid.gruppie.model

import com.google.gson.annotations.SerializedName

data class ClassItem(
    val type: String? = null,

    val classTypeId: String? = null,

    @SerializedName("class")
    val _class: ArrayList<String>? = null,
)
