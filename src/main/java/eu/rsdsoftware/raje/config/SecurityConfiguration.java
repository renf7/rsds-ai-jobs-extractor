package eu.rsdsoftware.raje.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Value("${raje.frontend-url:http://localhost:5173}") String frontendUrl) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health", "/api/health", "/api/auth/me").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth -> oauth.defaultSuccessUrl(frontendUrl, true))
                .oauth2Client(Customizer.withDefaults())
                .build();
    }
}
