package luke.friendbook.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JTokenUtility {

    @Value("${jwt.secret.key}")
    private String SECRET_TOKEN;
    private final Logger logger = LoggerFactory.getLogger(JTokenUtility.class);

    public String generateToken(Authentication authentication) {
        String roles = pullAuthorities(authentication);
        Claims claims = setClaims(authentication, roles);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(authentication.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)))
                .signWith(SignatureAlgorithm.HS512, SECRET_TOKEN)
                .compact();
    }

    private Claims setClaims(Authentication authentication, String roles) {
        Claims claims = Jwts.claims();
        claims.put("credentials", authentication.getCredentials().toString());
        claims.put("roles", roles);
        return claims;
    }

    public String extractSubject(String token) {
        return extractClaim(token)
                .getSubject();
    }

    public String extractCredentials(String token) {
        return extractClaim(token)
                .get("credentials", String.class);
    }

    public Collection<GrantedAuthority> extractAuthorities(String token) {
        String[] rolesInString = extractClaim(token)
                .get("roles", String.class)
                .split(",");

        return Arrays.stream(rolesInString)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    public boolean isJwtTokenExpired(String token) {
        try {
            return getTokenExpirationDate(token).before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Date getTokenExpirationDate(String token) {
        return extractClaim(token)
                .getExpiration();
    }

    private Claims extractClaim(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET_TOKEN)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.warn("Login attempt with invalid JSON Web Token");
        }

        return claims;
    }

    protected String pullAuthorities(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining());
    }
}
