package luke.friendbook.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JTokenUtility {

    @Value("${jwt.secret.key}")
    private String SECRET_TOKEN;

    public String generateToken(Authentication authentication) {
        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)))
                .signWith(SignatureAlgorithm.HS512, SECRET_TOKEN)
                .compact();
    }

    public Date getTokenExpirationDate(String token) {
        return extractClaim(token)
                .getExpiration();
    }

    private Claims extractClaim(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_TOKEN)
                .parseClaimsJws(token)
                .getBody();
    }
}
