package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.auth.*
import com.example.routes.authRoutes
import com.example.routes.blogRoutes
import com.example.services.AuthService
import com.example.services.BlogService
import com.mongodb.ConnectionString
import io.ktor.http.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Application")

fun main() {
    val mongoUri = "mongodb+srv://arjunkishore18:arjun18@cluster0.dfacy.mongodb.net/"
    val client = KMongo.createClient(ConnectionString(mongoUri)).coroutine
    val db: CoroutineDatabase = client.getDatabase("blog-app")

    val authService = AuthService(db)
    val blogService = BlogService(db)

    embeddedServer(Netty, port = 8081) {
        install(CORS) {
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Get)
            allowMethod(HttpMethod.Post)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
            allowCredentials = true
            anyHost() // Enable CORS for all hosts
        }

        install(ContentNegotiation) {
            json(Json { prettyPrint = true; isLenient = true })
        }

        install(Authentication) {
            jwt("default") {
                verifier(JwtConfig.verifier)
                validate { credential ->
                    val userId = credential.payload.getClaim("userId").asString()
                    logger.info("Validating token for userId: $userId")
                    if (userId.isNotEmpty()) {
                        UserIdPrincipal(userId)
                    } else {
                        null
                    }
                }
            }
        }

        routing {
            authRoutes(authService)
            blogRoutes(blogService, authService)
        }

        logger.info("Server started on port 8081")
    }.start(wait = true)
}
