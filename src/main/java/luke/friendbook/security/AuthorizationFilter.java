package luke.friendbook.security;


import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

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
        System.out.println(headerValue);

        if (headerValue == null || !headerValue.startsWith("Bearer ")){
            chain.doFilter(request, response);
            return;
        }

        String token = headerValue.replace("Bearer ", "");

        chain.doFilter(request, response);
    }

    private Authentication getAuthenticationObject() {
        return new UsernamePasswordAuthenticationToken("xx", "xx", Collections.emptyList());
    }
}
