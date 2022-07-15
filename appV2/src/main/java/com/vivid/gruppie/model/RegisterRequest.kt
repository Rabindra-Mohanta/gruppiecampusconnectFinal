package com.vivid.gruppie.model

data class RegisterRequestData(
        val appName : String,
    val name: String,
    val subCategory: String,
    val board: String,
    val university: String,
    val medium: String,
    val classTypeId: ArrayList<String>,
    val classSection: ArrayList<ClassInputData>,
    val academicEndYear: String,
    val academicStartYear: String
)