package com.idan.pokemon_hub.model

import jakarta.persistence.*

@Entity
@Table(name = "pokemon")
data class Pokemon(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var pokedex: Long = 0,

    var name: String = "",

    @ElementCollection(targetClass = PokemonType::class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "pokemon_types",
        joinColumns = [JoinColumn(name = "pokedex")]
    )
    var type: Set<PokemonType> = emptySet(),

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "image_pokedex", referencedColumnName = "pokedex")
    var image: PokemonImage = PokemonImage() // Use no-arg constructor for default
)
