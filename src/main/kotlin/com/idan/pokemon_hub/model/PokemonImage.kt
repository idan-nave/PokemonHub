package com.idan.pokemon_hub.model

import jakarta.persistence.*
import java.net.URL

@Entity
@Table(name = "pokemon_image")
data class PokemonImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var pokedex: Long = 0,
    var imageUrl: URL? = null
)
