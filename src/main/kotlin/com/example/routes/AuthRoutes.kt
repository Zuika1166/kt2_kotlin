package com.example.routes

import com.example.dto.ErrorResponse
import com.example.dto.LoginRequest
import com.example.dto.RegisterRequest
import com.example.dto.UserResponse
import com.example.service.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            val request = runCatching { call.receive<RegisterRequest>() }.getOrElse {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid request body"))
                return@post
            }

            if (request.login.isBlank() || request.password.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Login and password are required"))
                return@post
            }

            val user = authService.register(request)
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Login already exists"))
                return@post
            }

            call.respond(HttpStatusCode.Created, UserResponse(user.id, user.login))
        }

        post("/login") {
            val request = runCatching { call.receive<LoginRequest>() }.getOrElse {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid request body"))
                return@post
            }

            if (request.login.isBlank() || request.password.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Login and password are required"))
                return@post
            }

            val token = authService.login(request)
            if (token == null) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid login or password"))
                return@post
            }

            call.respond(HttpStatusCode.OK, token)
        }
    }
}
