import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.User
import java.util.Date

object JwtConfig {
    private const val SECRET = "G7x3b9D5qT8wZ2kL1sN6fA0pVjR4uE8s" // Your secret key
     const val ISSUER = "my-blog-app"
     const val VALIDITY_IN_MS = 36_000_00 * 24 // 24 hours

    private val algorithm = Algorithm.HMAC256(SECRET)

    // JWT Verifier
    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER)
        .build()

    // Token validation method
    fun validateToken(token: String): String? {
        return try {
            val decodedJWT = verifier.verify(token)
            decodedJWT.subject
        } catch (e: Exception) {
            null
        }
    }

    // Token generation
    fun generateToken(user: User): String {
        return JWT.create()
            .withSubject(user.email)
            .withClaim("userId", user.userId)
            .withClaim("username", user.username)
            .withClaim("email", user.email)
            .withIssuer(ISSUER)
            .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_IN_MS))
            .sign(algorithm)
    }
}
