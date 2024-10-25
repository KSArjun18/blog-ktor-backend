package com.example.routes

import com.example.models.BlogPost
import com.example.services.BlogService
import com.example.services.AuthService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("BlogRoutes")

fun Route.blogRoutes(blogService: BlogService, authService: AuthService) {
    get("/all") {
        try {
            val blogPosts = blogService.getAllBlogPosts()
            if (blogPosts.isEmpty()) {
                call.respond(HttpStatusCode.OK, "No blog posts available.")
            } else {
                call.respond(blogPosts)
            }
        } catch (e: Exception) {
            logger.error("Error fetching all blog posts: ${e.message}", e)
            call.respond(HttpStatusCode.InternalServerError, "Failed to fetch blog posts")
        }
    }


    authenticate("default") {
        post("/blogs") {
            val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")

            if (token == null) {
                call.respond(HttpStatusCode.Unauthorized, "Token not provided")
                return@post
            }

            try {
                // Validate and decode the JWT token
                val decodedJWT = JwtConfig.verifier.verify(token)
                val userId = decodedJWT.getClaim("userId").asString()
                val username = decodedJWT.getClaim("username").asString()
                val email = decodedJWT.getClaim("email").asString()

                // Receive the blog post data from the request body
                val blogPost = call.receive<BlogPost>()

                // Create a new blog post with user data added
                val blogPostWithUser = blogPost.copy(
                    userId = userId,
                    username = username,
                    email = email,
                    createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                )

                // Save the blog post to the database
                val result = blogService.createBlogPost(blogPostWithUser)
                if (result) {
                    call.respond(HttpStatusCode.Created, "Blog post created successfully")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to create blog post")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid token: ${e.message}")
            }
        }

        get("/blogs/my") {
            try {
                val userId = call.principal<UserIdPrincipal>()?.name
                if (userId != null) {
                    val posts = blogService.getBlogPosts(userId)
                    call.respond(posts)
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "User not authorized")
                }
            } catch (e: Exception) {
                logger.error("Error fetching user's blog posts: ${e.message}", e)
                call.respond(HttpStatusCode.InternalServerError, "Failed to fetch user's blog posts")
            }
        }

        // Update a specific blog post
        put("/blogs/{id}") {
            val userId = call.principal<UserIdPrincipal>()?.name
            val id = call.parameters["id"]?.let { ObjectId(it) } ?: return@put call.respond(HttpStatusCode.BadRequest, "Invalid ID")

            try {
                val existingPost = blogService.getBlogPosts(userId!!).find { it._id == id }
                if (existingPost != null) {
                    val updatedPost = call.receive<BlogPost>()
                    val result = blogService.updateBlogPost(id, updatedPost.copy(userId = existingPost.userId, username = existingPost.username, email = existingPost.email))
                    if (result) {
                        call.respond(HttpStatusCode.OK, "Blog post updated")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Blog post not found")
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "Blog post not found")
                }
            } catch (e: Exception) {
                logger.error("Error updating blog post: ${e.message}", e)
                call.respond(HttpStatusCode.InternalServerError, "Failed to update blog post")
            }
        }

        // Delete a specific blog post
        delete("/blogs/{id}") {
            val userId = call.principal<UserIdPrincipal>()?.name
            val id = call.parameters["id"]?.let { ObjectId(it) } ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID")

            try {
                val existingPost = blogService.getBlogPosts(userId!!).find { it._id == id }
                if (existingPost != null) {
                    val deleted = blogService.deleteBlogPost(id)
                    if (deleted) {
                        call.respond(HttpStatusCode.NoContent)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Blog post not found")
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "Blog post not found")
                }
            } catch (e: Exception) {
                logger.error("Error deleting blog post: ${e.message}", e)
                call.respond(HttpStatusCode.InternalServerError, "Failed to delete blog post")
            }
        }


    }
}
