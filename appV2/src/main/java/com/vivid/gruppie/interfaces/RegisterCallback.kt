package com.vivid.gruppie.interfaces

interface RegisterCallback {
    fun onUniversityClicked(universityName: String)
    fun onCheckBoxChanged(typeID: String, isSelected: Boolean)
    fun onCountChanged(key: String, value: Int)
}