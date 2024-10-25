package com.example.services

import com.example.models.BlogPost
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("BlogService")

class BlogService(private val db: CoroutineDatabase) {
    private val blogCollection = db.getCollection<BlogPost>()

    // Create a new blog post
    suspend fun createBlogPost(blogPost: BlogPost): Boolean {
        return try {
            blogCollection.insertOne(blogPost.copy(createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())))
            true
        } catch (e: Exception) {
            println("Error creating blog post: ${e.message}")
            false
        }
    }




    // Get blog posts for a specific user
    suspend fun getBlogPosts(userId: String): List<BlogPost> {
        return blogCollection.find(BlogPost::userId eq userId).toList() // Filter by user ID
    }




    // Get all blog posts
    suspend fun getAllBlogPosts(): List<BlogPost> {
        return try {
            blogCollection.find().toList()
        } catch (e: Exception) {
            logger.error("Error fetching all blog posts: ${e.message}", e)
            emptyList()
        }
    }

    // Update an existing blog post
    suspend fun updateBlogPost(id: ObjectId, updatedPost: BlogPost): Boolean {
        return try {
            val updateResult = blogCollection.updateOne(BlogPost::_id eq id, updatedPost)
            if (updateResult.matchedCount > 0) {
                logger.info("Blog post updated: $id")
                true
            } else {
                logger.warn("Blog post not found for update: $id")
                false
            }
        } catch (e: Exception) {
            logger.error("Error updating blog post: ${e.message}", e)
            false
        }
    }

    // Delete a blog post
    suspend fun deleteBlogPost(id: ObjectId): Boolean {
        return try {
            val deleteResult = blogCollection.deleteOne(BlogPost::_id eq  id)
            if (deleteResult.deletedCount > 0) {
                logger.info("Blog post deleted: $id")
                true
            } else {
                logger.warn("Blog post not found for deletion: $id")
                false
            }
        } catch (e: Exception) {
            logger.error("Error deleting blog post: ${e.message}", e)
            false
        }
    }
}
