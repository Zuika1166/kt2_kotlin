package com.example.service

import com.example.config.JwtConfig
import com.example.dto.LoginRequest
import com.example.dto.RegisterRequest
import com.example.dto.TokenResponse
import com.example.model.User
import com.example.repository.UserRepository
import org.mindrot.jbcrypt.BCrypt

class AuthService(
    private val userRepository: UserRepository,
    private val jwtConfig: JwtConfig
) {
    fun register(request: RegisterRequest): User? {
        val passwordHash = BCrypt.hashpw(request.password, BCrypt.gensalt())
        return userRepository.create(request.login, passwordHash)
    }

    fun login(request: LoginRequest): TokenResponse? {
        val user = userRepository.findByLogin(request.login) ?: return null
        if (!BCrypt.checkpw(request.password, user.passwordHash)) return null
        return TokenResponse(
            token = jwtConfig.createToken(user),
            expiresInSeconds = jwtConfig.expiresInSeconds
        )
    }
}
