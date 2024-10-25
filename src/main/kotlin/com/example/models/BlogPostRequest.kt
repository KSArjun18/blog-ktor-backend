package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class BlogPostRequest(
    val title: String,
    val content: String?
)
