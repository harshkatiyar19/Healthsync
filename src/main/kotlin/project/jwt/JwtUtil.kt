package project.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function
import javax.crypto.SecretKey

@Component
class JwtUtil() {

     fun getSigningKey(): SecretKey {
        val secretKey = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
        return Keys.hmacShaKeyFor(secretKey.toByteArray())
    }

    fun extractUsername(token: String?): String {
        return extractClaim(token) { obj: Claims -> obj.subject }
    }

    fun extractUserId(token: String?): Long {
        return extractClaim(
            token
        ) { claims: Claims -> claims.get("userId", Long::class.java) }
    }

    fun extractExpiration(token: String?): Date {
        return extractClaim(token) { obj: Claims -> obj.expiration }
    }

    fun <T> extractClaim(token: String?, resolver: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return resolver.apply(claims)
    }

     fun extractAllClaims(token: String?): Claims {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

     fun isTokenExpired(token: String?): Boolean {
        return !extractExpiration(token).before(Date())
    }

    fun generateToken(userId: String): String {
        val claim: MutableMap<String, Any?> = HashMap()
//        claim["userId"] = userRepository.findUserIdByUsername(username)
        return Jwts.builder()
//            .claims(claim)
            .subject(userId)
            .header().empty().add("typ", "JWT")
            .and()
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 60 minutes expiration time
            .signWith(getSigningKey())
            .compact()
    }

    fun isTokenValid(token: String?, username: String): Boolean {
        return (username == extractUsername(token) && isTokenExpired(token))
    }

    fun validateToken(token: String?): Boolean {
        return isTokenExpired(token)
    }

}