package com.boticlic.boticlic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

// @EnableAsync permite que los métodos con @Async
// se ejecuten en un hilo separado (en segundo plano).
// Necesario para que los emails no bloqueen la respuesta al usuario.
@SpringBootApplication
@EnableAsync
public class BoticlicApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoticlicApplication.class, args);
    }
}