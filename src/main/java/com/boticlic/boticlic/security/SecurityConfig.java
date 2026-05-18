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

    // ✅ Bean que faltaba - encriptación de contraseñas
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

                        // ✅ Rutas públicas
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/usuarios/registrar").permitAll() // ← AGREGAR ESTA LÍNEA
                        .requestMatchers(
                                "/", "/index.html", "/admin.html",
                                "/farmaceutico.html", "/delivery.html", "/login.html",
                                "/*.css", "/*.js", "/images/**"
                        ).permitAll()

                        // ✅ Ver productos es público
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()

                        // 🔒 Solo ADMIN gestiona usuarios
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                        // 🔒 Solo FARMACEUTICO crea/edita/elimina productos
                        .requestMatchers(HttpMethod.POST,   "/api/productos/**").hasRole("FARMACEUTICO")
                        .requestMatchers(HttpMethod.PUT,    "/api/productos/**").hasRole("FARMACEUTICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("FARMACEUTICO")

                        // 🔒 Solo DELIVERY ve sus pedidos
                        .requestMatchers("/api/delivery/**").hasRole("DELIVERY")

                        // 🔒 ADMIN y FARMACEUTICO cambian estado de pedidos
                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/*/estado")
                        .hasAnyRole("ADMIN", "FARMACEUTICO")

                        // 🔒 Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}