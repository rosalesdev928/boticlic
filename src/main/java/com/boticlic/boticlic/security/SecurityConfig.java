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

                        // ✅ PÚBLICAS
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/usuarios/registrar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                        .requestMatchers(
                                "/", "/index.html", "/admin.html", "/farmaceutico.html",
                                "/delivery.html", "/login.html", "/Login.html",
                                "/*.css", "/*.js", "/Auth guard.js",
                                "/images/**", "/img/**", "/favicon.png"
                        ).permitAll()

                        // ✅ RECETAS
                        .requestMatchers(HttpMethod.POST, "/api/recetas").hasAnyRole("CLIENTE", "ADMIN", "FARMACEUTICO")
                        .requestMatchers(HttpMethod.GET, "/api/recetas/usuario/**").hasAnyRole("CLIENTE", "ADMIN", "FARMACEUTICO")
                        .requestMatchers(HttpMethod.GET, "/api/recetas/pendientes").hasAnyRole("ADMIN", "FARMACEUTICO")
                        .requestMatchers(HttpMethod.GET, "/api/recetas").hasAnyRole("ADMIN", "FARMACEUTICO")
                        .requestMatchers(HttpMethod.GET, "/api/recetas/*/archivo").hasAnyRole("ADMIN", "FARMACEUTICO")
                        .requestMatchers(HttpMethod.PUT, "/api/recetas/*/aprobar").hasAnyRole("ADMIN", "FARMACEUTICO")
                        .requestMatchers(HttpMethod.PUT, "/api/recetas/*/rechazar").hasAnyRole("ADMIN", "FARMACEUTICO")

                        // ✅ PEDIDOS
                        .requestMatchers(HttpMethod.POST, "/api/pedidos").hasAnyRole("CLIENTE", "ADMIN", "FARMACEUTICO")
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/usuario/**").hasAnyRole("CLIENTE", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/estado/**").hasAnyRole("ADMIN", "FARMACEUTICO")
                        .requestMatchers(HttpMethod.GET, "/api/pedidos/**").hasAnyRole("ADMIN", "FARMACEUTICO")
                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/**").hasAnyRole("ADMIN", "FARMACEUTICO")

                        // ✅ USUARIOS
                        .requestMatchers(HttpMethod.GET, "/api/usuarios").hasAnyRole("ADMIN", "FARMACEUTICO")
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                        // ✅ PRODUCTOS
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasAnyRole("ADMIN", "FARMACEUTICO")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasAnyRole("ADMIN", "FARMACEUTICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasAnyRole("ADMIN", "FARMACEUTICO")

                        // ✅ DELIVERY
                        .requestMatchers("/api/delivery/**").hasAnyRole("DELIVERY", "ADMIN", "FARMACEUTICO")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}