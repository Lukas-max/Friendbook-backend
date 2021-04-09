package luke.friendbook.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import luke.friendbook.account.model.LoginRequestModel;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JTokenUtility tokenUtility;

    public AuthenticationFilter(JTokenUtility tokenUtility) {
        this.tokenUtility = tokenUtility;
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        LoginRequestModel loginRequestModel = new ObjectMapper()
                .readValue(request.getInputStream(), LoginRequestModel.class);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginRequestModel.getEmail(),
                loginRequestModel.getPassword(),
                Collections.emptyList()
        );

        return this.getAuthenticationManager().authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authentication) {
        if (!authentication.isAuthenticated())
            throw new InternalAuthenticationServiceException("Błąd przy uwierzytelnieniu użytkownika");

        String auths = tokenUtility.pullAuthorities(authentication);
        String token = tokenUtility.generateToken(authentication);
        long timestamp = tokenUtility.getTokenExpirationDate(token)
                .getTime();


        response.setHeader("user", authentication.getName());
        response.setHeader("jwt-token", token);
        response.setHeader("roles", auths);
        response.setHeader("jwt-expiration", Long.toString(timestamp));
    }
}





