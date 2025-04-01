package com.idan.pokemon_hub.service

import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.repository.PokemonRepository
import org.springframework.stereotype.Service
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@Service
class PokemonService(private val pokemonRepository: PokemonRepository) {

    fun getAllPokemons(): List<Pokemon> {
        return pokemonRepository.findAll()
    }
    fun getPokemonByPokedex(pokedex: Long): Pokemon? {
        val pokemon = pokemonRepository.findByPokedex(pokedex)
        return pokemon ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Pokemon not found"
        )
    }

    fun updatePokemon(pokedex: Long, pokemon: Pokemon): Pokemon? {
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

    fun deletePokemon(pokedex: Long) {
        pokemonRepository.deleteByPokedex(pokedex)
    }

    private fun isValidType(type: String): Boolean {
        val typeRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        return typeRegex.matches(type)
    }
}
