package com.example

import com.example.dto.TokenResponse
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.contentType
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun crudAndAuthenticationFlow() = testApplication {
        application { module() }

        val registerResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"login":"john","password":"secret123"}""")
        }
        assertEquals(HttpStatusCode.Created, registerResponse.status)

        val duplicateResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"login":"john","password":"secret123"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, duplicateResponse.status)

        val wrongPasswordResponse = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"login":"john","password":"wrong"}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, wrongPasswordResponse.status)

        val loginResponse = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"login":"john","password":"secret123"}""")
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)
        val token = Json.decodeFromString<TokenResponse>(loginResponse.bodyAsText()).token

        val invalidTokenCreate = client.post("/books") {
            bearerAuth("invalid-token")
            contentType(ContentType.Application.Json)
            setBody("""{"title":"Clean Code","author":"Robert C. Martin","year":2008}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, invalidTokenCreate.status)

        val unauthorizedCreate = client.post("/books") {
            contentType(ContentType.Application.Json)
            setBody("""{"title":"Clean Code","author":"Robert C. Martin","year":2008}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, unauthorizedCreate.status)

        val createResponse = client.post("/books") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody("""{"title":"Clean Code","author":"Robert C. Martin","year":2008}""")
        }
        assertEquals(HttpStatusCode.Created, createResponse.status)

        assertEquals(HttpStatusCode.OK, client.get("/books").status)
        assertEquals(HttpStatusCode.OK, client.get("/books/1").status)
        assertEquals(HttpStatusCode.NotFound, client.get("/books/999").status)
        assertEquals(HttpStatusCode.BadRequest, client.get("/books/abc").status)

        val updateResponse = client.put("/books/1") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody("""{"title":"Clean Code","author":"Robert C. Martin","year":2009}""")
        }
        assertEquals(HttpStatusCode.OK, updateResponse.status)

        val deleteResponse = client.delete("/books/1") {
            bearerAuth(token)
        }
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)
        assertEquals(HttpStatusCode.NotFound, client.get("/books/1").status)
    }
}
