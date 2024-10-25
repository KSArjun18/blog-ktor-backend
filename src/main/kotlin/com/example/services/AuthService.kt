package com.example.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

import com.example.models.User
import org.litote.kmongo.eq
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.mindrot.jbcrypt.BCrypt
import java.util.*
import kotlin.random.Random
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("AuthService")

class AuthService(private val db: CoroutineDatabase) {
    private val SECRET_KEY = "G7x3b9D5qT8wZ2kL1sN6fA0pVjR4uE8s"
    private val ISSUER = "my-blog-app"
    private val algorithm = Algorithm.HMAC256(SECRET_KEY)

    suspend fun findUserByEmail(email: String): User? {
        return try {
            db.getCollection<User>().findOne(User::email eq email)
        } catch (e: Exception) {
            logger.error("Error finding user by email: ${e.message}", e)
            null
        }
    }

    suspend fun login(email: String, password: String): User? {
        return try {
            val user = findUserByEmail(email)
            if (user != null && validatePassword(user, password)) {
                logger.info("User logged in: ${user.username}")
                user
            } else {
                null
            }
        } catch (e: Exception) {
            logger.error("Error during login: ${e.message}", e)
            null
        }
    }

    fun generateToken(user: User): String {
        return JWT.create()
            .withSubject(user.email)
            .withClaim("userId", user.userId)
            .withClaim("username", user.username)
            .withClaim("email", user.email)
            .withIssuer(ISSUER)
            .withExpiresAt(Date(System.currentTimeMillis() + JwtConfig.VALIDITY_IN_MS))
            .sign(algorithm)
    }





    suspend fun registerUser(user: User): Boolean {
        return try {
            val existingUser = findUserByEmail(user.email)
            if (existingUser != null) return false

            val uniqueUserId = generateUniqueUserId()
            val hashedPassword = hashPassword(user.password)
            val newUser = user.copy(password = hashedPassword, userId = uniqueUserId)
            db.getCollection<User>().insertOne(newUser).wasAcknowledged()
        } catch (e: Exception) {
            logger.error("Error during registration: ${e.message}", e)
            false
        }
    }

    private fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    private fun validatePassword(user: User, password: String): Boolean {
        return BCrypt.checkpw(password, user.password)
    }

    private fun generateUniqueUserId(): String {
        return Random.nextInt(100000, 999999).toString() // Generate a random user ID
    }
}
