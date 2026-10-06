package com.example.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.model.User
import java.util.Date

data class JwtConfig(
    val secret: String = System.getenv("JWT_SECRET") ?: "local-development-secret-change-me",
    val issuer: String = System.getenv("JWT_ISSUER") ?: "ktor-books-api",
    val audience: String = System.getenv("JWT_AUDIENCE") ?: "ktor-books-users",
    val realm: String = System.getenv("JWT_REALM") ?: "Books API",
    val expiresInSeconds: Long = 3600
) {
    val algorithm: Algorithm = Algorithm.HMAC256(secret)

    fun createToken(user: User): String {
        val expiresAt = Date(System.currentTimeMillis() + expiresInSeconds * 1000)
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", user.id)
            .withClaim("login", user.login)
            .withExpiresAt(expiresAt)
            .sign(algorithm)
    }
}
