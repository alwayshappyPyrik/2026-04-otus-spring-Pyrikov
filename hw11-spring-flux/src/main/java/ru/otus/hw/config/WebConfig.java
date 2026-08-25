package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class WebConfig {

    @Bean
    public RouterFunction<ServerResponse> redirectRouter() {
        return route()
                .GET("/", request ->
                        ServerResponse.permanentRedirect(URI.create("/books")).build()
                )
                .build();
    }
}
