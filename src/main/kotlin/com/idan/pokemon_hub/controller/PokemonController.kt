package com.idan.pokemon_hub

import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.service.PokemonService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/pokemons")
class PokemonController(private val pokemonService: PokemonService) {

    @GetMapping
    fun getAllPokemons(): List<Pokemon> {
        return pokemonService.getAllPokemons()
    }

    @GetMapping("/{id}")
    fun getPokemonByPokedex(@PathVariable pokedex: Long): Pokemon? {
        return pokemonService.getPokemonByPokedex(pokedex) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Pokemon not found"
        )
    }

    @PutMapping("/{id}")
    fun updatePokemon(@PathVariable pokedex: Long, @RequestBody pokemon: Pokemon): Pokemon? {
        return pokemonService.updatePokemon(pokedex, pokemon) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Pokemon not found"
        )
    }

    @DeleteMapping("/{id}")
    fun deletePokemon(@PathVariable pokedex: Long): ResponseEntity<Void> {
        pokemonService.deletePokemon(pokedex)
        return ResponseEntity.noContent().build()
    }
}
