package com.boticlic.boticlic.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFiltro jwtFiltro;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ✅ PÚBLICAS — sin token
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/usuarios/registrar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                        .requestMatchers(
                                "/", "/index.html", "/admin.html", "/farmaceutico.html",
                                "/delivery.html", "/login.html", "/Login.html",

                                "/*.css",
                                "/*.js",

                                "/Auth guard.js",

                                "/images/**",
                                "/img/**",
                                "/favicon.png"
                        ).permitAll()

                        // ✅ CLIENTE — ver y crear SUS pedidos (PRIMERO, antes de las reglas de admin)
                        .requestMatchers(HttpMethod.POST, "/api/pedidos").hasAnyRole("CLIENTE", "ADMIN", "FARMACEUTICO")
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/usuario/**").hasAnyRole("CLIENTE", "ADMIN")

                        // ✅ ADMIN y FARMACEUTICO — gestión completa de pedidos
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/estado/**").hasAnyRole("ADMIN", "FARMACEUTICO")
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/**").hasAnyRole("ADMIN", "FARMACEUTICO")
                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/**").hasAnyRole("ADMIN", "FARMACEUTICO")

                        // ✅ ADMIN — gestión de usuarios
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                        // ✅ FARMACEUTICO — gestión de productos
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("FARMACEUTICO")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("FARMACEUTICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("FARMACEUTICO")

                        // ✅ DELIVERY
                        .requestMatchers("/api/delivery/**").hasAnyRole("DELIVERY", "ADMIN")

                        // 🔒 Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}