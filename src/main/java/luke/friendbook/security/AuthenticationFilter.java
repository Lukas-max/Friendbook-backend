package luke.friendbook.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import luke.friendbook.account.model.LoginRequestModel;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Collections;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JTokenUtility tokenUtility;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public AuthenticationFilter(JTokenUtility tokenUtility,
                                PasswordEncoder passwordEncoder,
                                UserDetailsService userDetailsService) {
        this.tokenUtility = tokenUtility;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        LoginRequestModel loginRequestModel = new ObjectMapper()
                .readValue(request.getInputStream(), LoginRequestModel.class);

        String decodedPassword = new String(Base64.getDecoder().decode(loginRequestModel.getPassword().getBytes()));
        loginRequestModel.setPassword(decodedPassword);

        // dodać tutaj exception handlera dla tego Errora, i dla tego niżej. zrób też dla ShopBackend.
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequestModel.getEmail());
        if (!passwordEncoder.matches(loginRequestModel.getPassword(), userDetails.getPassword()))
            throw new BadCredentialsException("Hasło albo nazwa użytkownika nie prawidłowe.");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
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





