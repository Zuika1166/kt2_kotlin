package com.example.repository

import com.example.dto.CreateBookRequest
import com.example.dto.UpdateBookRequest
import com.example.model.Book
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

interface BookRepository {
    fun findAll(): List<Book>
    fun findById(id: Long): Book?
    fun create(request: CreateBookRequest): Book
    fun update(id: Long, request: UpdateBookRequest): Book?
    fun delete(id: Long): Boolean
}

class InMemoryBookRepository : BookRepository {
    private val books = ConcurrentHashMap<Long, Book>()
    private val sequence = AtomicLong(0)

    override fun findAll(): List<Book> = books.values.sortedBy { it.id }

    override fun findById(id: Long): Book? = books[id]

    override fun create(request: CreateBookRequest): Book {
        val id = sequence.incrementAndGet()
        val book = Book(id, request.title, request.author, request.year)
        books[id] = book
        return book
    }

    override fun update(id: Long, request: UpdateBookRequest): Book? {
        if (!books.containsKey(id)) return null
        val book = Book(id, request.title, request.author, request.year)
        books[id] = book
        return book
    }

    override fun delete(id: Long): Boolean = books.remove(id) != null
}
