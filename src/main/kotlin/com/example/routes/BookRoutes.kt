package com.example.routes

import com.example.dto.CreateBookRequest
import com.example.dto.ErrorResponse
import com.example.dto.UpdateBookRequest
import com.example.service.BookService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.bookRoutes(bookService: BookService) {
    route("/books") {
        get {
            call.respond(HttpStatusCode.OK, bookService.getAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
            if (id == null || id <= 0) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid book id"))
                return@get
            }

            val book = bookService.getById(id)
            if (book == null) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("Book not found"))
                return@get
            }

            call.respond(HttpStatusCode.OK, book)
        }

        authenticate("auth-jwt") {
            post {
                val request = runCatching { call.receive<CreateBookRequest>() }.getOrElse {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid request body"))
                    return@post
                }

                if (!request.isValid()) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid book data"))
                    return@post
                }

                call.respond(HttpStatusCode.Created, bookService.create(request))
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                if (id == null || id <= 0) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid book id"))
                    return@put
                }

                val request = runCatching { call.receive<UpdateBookRequest>() }.getOrElse {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid request body"))
                    return@put
                }

                if (!request.isValid()) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid book data"))
                    return@put
                }

                val book = bookService.update(id, request)
                if (book == null) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Book not found"))
                    return@put
                }

                call.respond(HttpStatusCode.OK, book)
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                if (id == null || id <= 0) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid book id"))
                    return@delete
                }

                if (!bookService.delete(id)) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Book not found"))
                    return@delete
                }

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

private fun CreateBookRequest.isValid(): Boolean =
    title.isNotBlank() && author.isNotBlank() && year > 0

private fun UpdateBookRequest.isValid(): Boolean =
    title.isNotBlank() && author.isNotBlank() && year > 0
