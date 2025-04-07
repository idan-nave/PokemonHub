package com.idan.pokemon_hub.model
import java.net.URL
import jakarta.persistence.*


@Entity
@Table(name="pokemon")
data class Pokemon(

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var pokedex: Long = 0,
    var name: String = "",
    var type: String = "",
    var imageUrl: URL? = null

)
