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

    fun getByPokedex(pokedex: Long): Pokemon {
        val pokemon = pokemonRepository.findByPokedex(pokedex)
        return pokemon ?: throw PokemonNotFoundException("Pokemon with Pokedex $pokedex not found")
    }

    fun updateByPokedex(pokedex: Long, pokemon: Pokemon): Pokemon {
        if (pokemon.name.isBlank()) {
            throw InvalidFieldException("Name cannot be empty")
        }
        if (pokemon.type.isEmpty()) {
            throw InvalidFieldException("Type cannot be empty")
        }

        val existingPokemon = pokemonRepository.findByPokedex(pokedex)
            ?: throw PokemonNotFoundException("Pokemon with Pokedex $pokedex not found")

        existingPokemon.name = pokemon.name
        existingPokemon.type = pokemon.type
        existingPokemon.image = pokemon.image
        return pokemonRepository.save(existingPokemon)
    }

    fun deleteByPokedex(pokedex: Long) {
        val existingPokemon = pokemonRepository.findByPokedex(pokedex)
            ?: throw PokemonNotFoundException("Pokemon with Pokedex $pokedex not found")
        pokemonRepository.delete(existingPokemon)
    }
}