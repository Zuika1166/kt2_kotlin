package com.example

import com.example.config.JwtConfig
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.repository.InMemoryBookRepository
import com.example.repository.InMemoryUserRepository
import com.example.routes.authRoutes
import com.example.routes.bookRoutes
import com.example.service.AuthService
import com.example.service.BookService
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(
        factory = Netty,
        host = "0.0.0.0",
        port = 8080,
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    val jwtConfig = JwtConfig()
    val userRepository = InMemoryUserRepository()
    val bookRepository = InMemoryBookRepository()
    val authService = AuthService(userRepository, jwtConfig)
    val bookService = BookService(bookRepository)

    configureSerialization()
    configureSecurity(jwtConfig, userRepository)

    routing {
        authRoutes(authService)
        bookRoutes(bookService)
    }
}
