package luke.friendbook.security;


import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final JTokenUtility tokenUtility;
    private final UserDetailsService detailsService;

    public AuthorizationFilter(AuthenticationManager authenticationManager,
                               JTokenUtility tokenUtility,
                               UserDetailsService detailsService) {
        super(authenticationManager);
        this.tokenUtility = tokenUtility;
        this.detailsService = detailsService;
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
        if (!tokenUtility.validateToken(token)){
            response.sendError(401, "Nie ważny token autoryzacyjny. Zaloguj się ponownie.");
            return;
        }

        UserDetails userDetails = detailsService.loadUserByUsername(tokenUtility.extractSubject(token));
        if (userDetails == null) {
            response.sendError(403, "Nie znaleziono użytkownika dla wysłanej autoryzacji. Odmowa dostępu.");
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthenticationObject(userDetails ,token);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthenticationObject(UserDetails userDetails,String token) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                tokenUtility.extractCredentials(token),
                tokenUtility.extractAuthorities(token));
    }
}
