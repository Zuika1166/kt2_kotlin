package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val login: String,
    val password: String
)

@Serializable
data class LoginRequest(
    val login: String,
    val password: String
)

@Serializable
data class UserResponse(
    val id: Long,
    val login: String
)

@Serializable
data class TokenResponse(
    val token: String,
    val expiresInSeconds: Long
)

@Serializable
data class ErrorResponse(
    val message: String
)
