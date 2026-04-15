package com.example.qlcc

import java.io.Serializable

data class Admin(
    val adminId: Int,
    val adminUsername: String,
    val adminFullname: String?,
    val adminPhonenumber: String,
    val adminRole: String?
) : Serializable