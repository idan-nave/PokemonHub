package com.idan.pokemon_hub.service

import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.repository.PokemonRepository
import org.springframework.stereotype.Service
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@Service
class PokemonService(private val pokemonRepository: PokemonRepository) {

    fun getAll(): List<Pokemon> {
        return pokemonRepository.findAll()
    }
    fun geByPokedex(pokedex: Long): Pokemon? {
        val pokemon = pokemonRepository.findByPokedex(pokedex)
        return pokemon ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Pokemon not found"
        )
    }

    fun updateByPokedex(pokedex: Long, pokemon: Pokemon): Pokemon? {
        if (pokemon.name.isNullOrBlank() || pokemon.type.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Name and type cannot be empty")
        }

        if (!isValidType(pokemon.type)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type format")
        }

        val optionalPokemon = pokemonRepository.findByPokedex(pokedex)

        return optionalPokemon?.apply {
            name = pokemon.name
            type = pokemon.type
            pokemonRepository.save(this)
        }

    }

    fun deleteByPokedex(pokedex: Long) {
        pokemonRepository.deleteByPokedex(pokedex)
    }

    private fun isValidType(type: String): Boolean {
        val validTypes = setOf("grass", "poison", "fire", "water", "bug", "normal", "electric", "ground", "fairy", "fighting", "psychic", "rock", "ghost", "ice", "dragon", "dark", "steel", "flying")
        return type.split("/").all { it in validTypes }
    }
}
