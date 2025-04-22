package com.idan.pokemon_hub.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/pokemons")
            .allowedOrigins("http://localhost:5173")
            .allowedMethods("GET", "OPTIONS")
            .allowedHeaders("*")

        registry.addMapping("/pokemons/**")
            .allowedOrigins("http://localhost:5173")
            .allowedMethods("GET", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
    }
}