package com.example.qlcc

import java.io.Serializable

data class Apartment(
    val roomId: String,
    val roomStatus: String,
    val roomArea: Double,
    val roomDesc: String?,
    val userFullname: String? = null
) : Serializable