package com.auth0.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

import java.net.URI;

/**
 * Configures the application's security settings
 */
@EnableWebFluxSecurity
public class SecurityConfig {

    private final String issuer;
    private final String clientId;

    public SecurityConfig(@Value("${spring.security.oauth2.client.provider.auth0.issuer-uri}") String issuer,
                          @Value("${spring.security.oauth2.client.registration.auth0.client-id}") String clientId) {
        this.issuer = issuer;
        this.clientId = clientId;
    }

    @Bean
    public SecurityWebFilterChain configure(ServerHttpSecurity http) throws Exception {
        return http.authorizeExchange()
            .pathMatchers("/", "/images/**").permitAll()
            .anyExchange().authenticated()
            .and().oauth2Login()
            .and().logout().logoutSuccessHandler(logoutSuccessHandler())
            .and().build();
    }

    /**
     * Configures the logout handling to log users out of Auth0 after successful logout from the application.
     * @return a {@linkplain ServerLogoutSuccessHandler} that will be called on successful logout.
     */
    @Bean
    public ServerLogoutSuccessHandler logoutSuccessHandler() {
        // Change this as needed to URI where users should be redirected to after logout
        String returnTo = "http://localhost:3000/";
        String logoutUrl = String.format(
                "%sv2/logout?client_id=%s&returnTo=%s",
                this.issuer,
                this.clientId,
                returnTo
        );

        RedirectServerLogoutSuccessHandler handler = new RedirectServerLogoutSuccessHandler();
        handler.setLogoutSuccessUrl(URI.create(logoutUrl));
        return handler;
    }
}