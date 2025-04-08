package com.idan.pokemon_hub.service

import com.idan.pokemon_hub.exception.InvalidFieldException
import com.idan.pokemon_hub.exception.PokemonNotFoundException
import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.repository.PokemonRepository
import org.springframework.stereotype.Service

@Service
class PokemonService(private val pokemonRepository: PokemonRepository) {

    fun getAll(): List<Pokemon> {
        return pokemonRepository.findAll()
    }

    fun getByPokedex(pokedex: Long): Pokemon { // Note: Still has typo "geByPokedex", should be "getByPokedex"
        val pokemon = pokemonRepository.findByPokedex(pokedex)
        return pokemon ?: throw PokemonNotFoundException("Pokemon with Pokedex $pokedex not found")
    }

    fun updateByPokedex(pokedex: Long, pokemon: Pokemon): Pokemon {
        // Check for invalid fields
        if (pokemon.name.isBlank()) {
            throw InvalidFieldException("Name cannot be empty")
        }
        if (pokemon.type.isEmpty()) {
            throw InvalidFieldException("Type cannot be empty")
        }

        // No need for additional type validation since type is a Set<PokemonType>
        // Invalid types can't be passed due to type safety

        val existingPokemon = pokemonRepository.findByPokedex(pokedex)
            ?: throw PokemonNotFoundException("Pokemon with Pokedex $pokedex not found")

        // Update the Pokemon fields
        existingPokemon.name = pokemon.name
        existingPokemon.type = pokemon.type
        return pokemonRepository.save(existingPokemon)
    }

    fun deleteByPokedex(pokedex: Long) {
        val existingPokemon = pokemonRepository.findByPokedex(pokedex)
            ?: throw PokemonNotFoundException("Pokemon with Pokedex $pokedex not found")
        pokemonRepository.delete(existingPokemon)
    }
}