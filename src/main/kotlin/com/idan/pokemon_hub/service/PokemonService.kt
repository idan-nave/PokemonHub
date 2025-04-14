package com.idan.pokemon_hub.service

import com.idan.pokemon_hub.exception.InvalidFieldException
import com.idan.pokemon_hub.exception.PokemonNotFoundException
import com.idan.pokemon_hub.exception.RaceConditionDetectedException
import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.repository.PokemonRepository
import jakarta.persistence.OptimisticLockException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class PokemonService(private val pokemonRepository: PokemonRepository) {

    fun getAll(): List<Pokemon> {
        return pokemonRepository.findAll()
    }

    fun getByPokedex(pokedex: Long): Pokemon {
        return pokemonRepository.getByPokedex(pokedex)
            ?: throw PokemonNotFoundException(pokedex)
    }

    @Transactional
    fun updateByPokedex(pokedex: Long, pokemon: Pokemon): Pokemon {
        if (pokemon.name.isBlank()) {
            throw InvalidFieldException("Name")
        }
        if (pokemon.type.isEmpty()) {
            throw InvalidFieldException("Type")
        }

        val existingPokemon = pokemonRepository.findByPokedex(pokedex)
            ?: throw PokemonNotFoundException(pokedex)
        try {
            existingPokemon.name = pokemon.name
            existingPokemon.type = pokemon.type
            existingPokemon.image = pokemon.image
            return pokemonRepository.save(existingPokemon)
        }catch (e: OptimisticLockException) {
            throw RaceConditionDetectedException("Optimistic locking failed: concurrent modification detected.")
        }
}

    fun deleteByPokedex(pokedex: Long) {
        val existingPokemon = pokemonRepository.getByPokedex(pokedex)
            ?: throw PokemonNotFoundException(pokedex)
        pokemonRepository.delete(existingPokemon)
    }
}