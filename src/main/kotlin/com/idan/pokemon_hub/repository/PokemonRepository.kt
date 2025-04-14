package com.idan.pokemon_hub.repository

import com.idan.pokemon_hub.model.Pokemon
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PokemonRepository : JpaRepository<Pokemon, Long> {
    fun getByPokedex(pokedex: Long): Pokemon?

    @Lock(LockModeType.OPTIMISTIC)
    fun findByPokedex(pokedex: Long): Pokemon?
}