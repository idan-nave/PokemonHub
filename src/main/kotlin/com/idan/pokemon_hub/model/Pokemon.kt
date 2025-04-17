package com.idan.pokemon_hub.model

import jakarta.persistence.*

@Entity
@Table(name = "pokemon")
data class Pokemon(
    @Id
    var pokedex: Long = 0,

    var name: String = "",

    @ElementCollection(targetClass = PokemonType::class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "pokemon_types",
        joinColumns = [JoinColumn(name = "pokedex")]
    )
    var type: Set<PokemonType> = mutableSetOf(),

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "image_pokedex")
    var image: PokemonImage = PokemonImage()
)
