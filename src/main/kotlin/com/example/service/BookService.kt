package com.example.service

import com.example.dto.CreateBookRequest
import com.example.dto.UpdateBookRequest
import com.example.model.Book
import com.example.repository.BookRepository

class BookService(private val repository: BookRepository) {
    fun getAll(): List<Book> = repository.findAll()

    fun getById(id: Long): Book? = repository.findById(id)

    fun create(request: CreateBookRequest): Book = repository.create(request)

    fun update(id: Long, request: UpdateBookRequest): Book? = repository.update(id, request)

    fun delete(id: Long): Boolean = repository.delete(id)
}
