package luke.friendbook.security;

import luke.friendbook.utilities.JTokenUtility;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class AppSecurity extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final JTokenUtility tokenUtility;

    public AppSecurity(
            @Qualifier("userRepository") UserDetailsService userDetailsService,
                       JTokenUtility tokenUtility) {
        this.userDetailsService = userDetailsService;
        this.tokenUtility = tokenUtility;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(this.passwordEncoder());
        auth.eraseCredentials(false);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().and()
                .authorizeRequests().anyRequest().permitAll()
                .and()
                .addFilter(getAuthenticationFilter())
                .addFilter(getAuthorizationFilter());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter filter = new AuthenticationFilter(tokenUtility, passwordEncoder(), userDetailsService);
        filter.setAuthenticationManager(this.authenticationManager());
        filter.setFilterProcessesUrl("/app/user/login");
        return filter;
    }

    private AuthorizationFilter getAuthorizationFilter() throws Exception {
        return new AuthorizationFilter(this.authenticationManager(), tokenUtility, userDetailsService);
    }
}
