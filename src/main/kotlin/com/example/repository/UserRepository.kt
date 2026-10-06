package com.example.repository

import com.example.model.User
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

interface UserRepository {
    fun findById(id: Long): User?
    fun findByLogin(login: String): User?
    fun create(login: String, passwordHash: String): User?
}

class InMemoryUserRepository : UserRepository {
    private val usersById = ConcurrentHashMap<Long, User>()
    private val userIdsByLogin = ConcurrentHashMap<String, Long>()
    private val sequence = AtomicLong(0)

    override fun findById(id: Long): User? = usersById[id]

    override fun findByLogin(login: String): User? {
        val id = userIdsByLogin[login] ?: return null
        return usersById[id]
    }

    override fun create(login: String, passwordHash: String): User? {
        val id = sequence.incrementAndGet()
        val previous = userIdsByLogin.putIfAbsent(login, id)
        if (previous != null) return null
        val user = User(id, login, passwordHash)
        usersById[id] = user
        return user
    }
}
