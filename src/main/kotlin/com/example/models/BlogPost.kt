package com.example.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

import org.bson.types.ObjectId
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName


@Serializable
data class BlogPost(
    @Contextual
    @SerialName("_id") @Serializable(with = ObjectIdSerializer::class) val _id: ObjectId = ObjectId(),
    val title: String,
    val content: String?,
    val createdAt: LocalDateTime? = null,
    val userId: String?=null, // Add the userId to track which user created the blog post
    val username: String? = null, // Optional field for username
    val email: String? = null // Optional field for email
)


