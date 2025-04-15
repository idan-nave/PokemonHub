package com.idan.pokemon_hub.service

import com.idan.pokemon_hub.exception.InvalidFieldException
import com.idan.pokemon_hub.exception.PokemonNotFoundException
import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.repository.PokemonRepository
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
class PokemonService(private val pokemonRepository: PokemonRepository) {

    fun getAll(): List<Pokemon> {
        return pokemonRepository.findAll()
    }

    fun getByPokedex(pokedex: Long): Pokemon {
        return pokemonRepository.findById(pokedex).getOrElse { throw PokemonNotFoundException(pokedex) }
    }

    fun updateByPokedex(pokedex: Long, pokemon: Pokemon): Pokemon {
        if (pokemon.name.isBlank()) {
            throw InvalidFieldException("Name")
        }
        if (pokemon.type.isEmpty()) {
            throw InvalidFieldException("Type")
        }

        val existingPokemon = pokemonRepository.findById(pokedex).getOrElse { throw PokemonNotFoundException(pokedex) }

        existingPokemon.name = pokemon.name
        existingPokemon.type = pokemon.type
        existingPokemon.image = pokemon.image
        return pokemonRepository.save(existingPokemon)
    }

    fun deleteByPokedex(pokedex: Long) {
        val existingPokemon = pokemonRepository.findById(pokedex).getOrElse { throw PokemonNotFoundException(pokedex) }
        pokemonRepository.delete(existingPokemon)
    }
}