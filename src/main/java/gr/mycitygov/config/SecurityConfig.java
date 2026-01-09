package gr.mycitygov.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Για να μπορείς να τεστάρεις API εύκολα (και να μη σε κόβει CSRF)
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // REST API (προσωρινά ανοιχτό)
                        .requestMatchers("/api/**").permitAll()

                        // Swagger (αν το έχεις)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // H2 console (αν το χρησιμοποιήσεις ξανά)
                        .requestMatchers("/h2-console/**").permitAll()

                        // Ό,τι άλλο είναι UI -> θέλει login
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults());

        return http.build();
    }

    @PostConstruct
    public void loaded() {
        System.out.println(">>> SecurityConfig LOADED");
    }

}
