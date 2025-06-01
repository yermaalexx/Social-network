package com.yermaalexx.socialnetwork.security;

import com.yermaalexx.socialnetwork.model.UserLogin;
import com.yermaalexx.socialnetwork.repository.UserLoginRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserLoginRepository userLoginRepository) {
        return login -> {
            UserLogin userLogin = userLoginRepository.findByLogin(login);
            if (userLogin != null)
                return userLogin;
            throw new UsernameNotFoundException("Login '" + login + "' not found");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        configureAuthorization(http);
        configureFormLogin(http);
        configureCsrf(http);
        configureHeaders(http);
        configureLogout(http);

        return http.build();
    }

    private void configureAuthorization(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/main/**", "/chat/**", "/profile/**").authenticated()
                .anyRequest().permitAll());
    }

    private void configureFormLogin(HttpSecurity http) throws Exception {
        http.formLogin(formLoginConfigurer -> formLoginConfigurer
                .loginPage("/login")
                .defaultSuccessUrl("/main", true));
    }

    private void configureCsrf(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
    }

    private void configureHeaders(HttpSecurity http) throws Exception {
        http.headers(headersConfigurer -> headersConfigurer
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
    }

    private void configureLogout(HttpSecurity http) throws Exception {
        http.logout(logoutConfigurer -> logoutConfigurer.logoutSuccessUrl("/login"));
    }

}
