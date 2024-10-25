package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val _id: String? = null,
    val userId: String? = null,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val password: String
)
