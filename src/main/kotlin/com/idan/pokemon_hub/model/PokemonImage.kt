package com.idan.pokemon_hub.model

import jakarta.persistence.*
import java.net.URI
import java.net.URL

@Entity
@Table(name = "pokemon_image")
data class PokemonImage(
    @Id
    var pokedex: Long = 0,

    @Column(name = "image_url", nullable = false)
    var imageUrl: URL = URI("https://example.com/default.png").toURL()
)