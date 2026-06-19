package com.bolao.brasileirao.security;

import com.bolao.brasileirao.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioService usuarioService;

    @Value("${api.security.token.secret}")
    private String secret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login",  "/esqueci-senha/**", "/css/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated()
                )

                .userDetailsService(usuarioService)

                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")  // ← POST automático do Spring Security
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                .rememberMe(remember -> remember
                        .key(secret)
                        .tokenValiditySeconds(60 * 60 * 24 * 30) // 30 dias logado
                        .userDetailsService(usuarioService)
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
