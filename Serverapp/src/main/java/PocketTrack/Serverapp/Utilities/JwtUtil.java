package PocketTrack.Serverapp.Utilities;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    private static final String SECRET = "JjO4Ggiyhoq59vKPLTuEhpOtKkafamMxU9o8GU6gF8ICJxt1Pf+U5DBLQcCKIMmsRdqKet1enuCGbSa29WV9NQ==";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String generateToken(String accessToken, String username) {
        return createToken(accessToken, username);
    }

    public String createToken(String oldToken, String subject) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJwt(oldToken).getBody();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1800000))
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public String createRefreshToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1800000))
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public String generateToken(Map<String, Object> claims, String username) {
        return createRefreshToken(claims, username);
    }

    public Boolean validateToken(String token) {
        try {
            String cleanToken = token.replace("Bearer", "");
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(cleanToken);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    private Date expirationDate(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getExpiration();
    }

    public Boolean isTokenExpired(String token) {
        return expirationDate(token).before(new Date());
    }

}
