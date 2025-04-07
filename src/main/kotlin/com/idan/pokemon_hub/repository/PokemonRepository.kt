package com.idan.pokemon_hub.repository

import com.idan.pokemon_hub.model.Pokemon
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PokemonRepository : JpaRepository<Pokemon, Long> {
    fun findByPokedex(pokedex: Long): Pokemon?
    fun deleteByPokedex(pokedex: Long)
}
