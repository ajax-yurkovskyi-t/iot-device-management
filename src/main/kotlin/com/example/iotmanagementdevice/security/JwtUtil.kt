package com.example.iotmanagementdevice.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil(
    @Value("\${jwt.secret}") secretString: String,
    @Value("\${jwt.expiration}") private val expiration: Long) {
    private val secret: SecretKey = Keys.hmacShaKeyFor(secretString.toByteArray(StandardCharsets.UTF_8))

    fun generateToken(email: String?): String {
        return Jwts.builder()
            .subject(email)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(secret)
            .compact()
    }

    fun isValidToken(token: String): Boolean {
        return try {
            val claimsJws = Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)

            !claimsJws.payload.expiration.before(Date())
        } catch (e: JwtException) {
            throw JwtException(EXPIRED_OR_INVALID_TOKEN, e)
        } catch (e: IllegalArgumentException) {
            throw JwtException(EXPIRED_OR_INVALID_TOKEN, e)
        }
    }

    fun getEmail(token: String): String {
        return getClaimsFromToken(token) { claims -> claims.subject }
    }

    private fun <T> getClaimsFromToken(token: String, claimsResolver: (Claims) -> T): T {

        val claims = Jwts.parser()
            .verifyWith(secret)
            .build()
            .parseSignedClaims(token)
            .payload
        return claimsResolver(claims)
    }

    companion object {
        private const val EXPIRED_OR_INVALID_TOKEN = "Expired or invalid JWT token"
    }
}
