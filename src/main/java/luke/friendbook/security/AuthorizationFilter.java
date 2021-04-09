package luke.friendbook.security;


import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final JTokenUtility tokenUtility;

    public AuthorizationFilter(AuthenticationManager authenticationManager, JTokenUtility tokenUtility) {
        super(authenticationManager);
        this.tokenUtility = tokenUtility;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String headerValue = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (headerValue == null || !headerValue.startsWith("Bearer ")){
            chain.doFilter(request, response);
            return;
        }

        String token = headerValue.replace("Bearer ", "");
        if (!validateToken(token)){
            response.sendError(401, "Nie ważny token autoryzacyjny. Zaloguj się ponownie.");
            return;
        }

        Authentication authentication = getAuthenticationObject(token);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        chain.doFilter(request, response);
    }

    private boolean validateToken(String token) {
        String subject = null;
        String credentials = null;
        Collection<GrantedAuthority> authoritySet = null;

        try {
            subject = tokenUtility.extractSubject(token);
            credentials = tokenUtility.extractCredentials(token);
            authoritySet = tokenUtility.extractAuthorities(token);
        }catch (Exception e){
            return false;
        }

        if (subject == null || subject.isEmpty() || credentials == null || credentials.isEmpty() || authoritySet == null)
            return false;

        return !tokenUtility.isJwtTokenExpired(token);
    }

    private Authentication getAuthenticationObject(String token) {
        return new UsernamePasswordAuthenticationToken(
                tokenUtility.extractSubject(token),
                tokenUtility.extractCredentials(token),
                tokenUtility.extractAuthorities(token));
    }
}
