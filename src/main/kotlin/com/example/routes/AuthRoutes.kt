package com.example.routes

import com.example.models.LoginRequest
import com.example.models.RegisterRequest
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import com.example.services.AuthService
import com.example.models.User
import io.ktor.http.*
import io.ktor.server.request.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("AuthRoutes")

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            try {
                val registerRequest = call.receive<RegisterRequest>()
                val user = User(
                    username = registerRequest.username,
                    email = registerRequest.email,
                    phoneNumber = registerRequest.phoneNumber,
                    password = registerRequest.password
                )
                val success = authService.registerUser(user)
                if (success) {
                    call.respond(HttpStatusCode.Created, "User registered successfully")
                } else {
                    call.respond(HttpStatusCode.BadRequest, "User with this email already exists")
                }
            } catch (e: Exception) {
                logger.error("Error during registration: ${e.message}", e)
                call.respond(HttpStatusCode.InternalServerError, "Registration failed")
            }
        }

        post("/login") {
            try {
                val credentials = call.receive<LoginRequest>()
                val user = authService.login(credentials.email, credentials.password)
                if (user != null) {
                    val token = authService.generateToken(user)
                    call.respond(mapOf("token" to token))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                }
            } catch (e: Exception) {
                logger.error("Error during login: ${e.message}", e)
                call.respond(HttpStatusCode.InternalServerError, "Login failed")
            }
        }
        post("/logout") {
            try {
                // Invalidate token on the client side (client removes token)
                call.respond(HttpStatusCode.OK, "User logged out successfully")
            } catch (e: Exception) {
                logger.error("Error during logout: ${e.message}", e)
                call.respond(HttpStatusCode.InternalServerError, "Logout failed")
            }
        }
    }
}
