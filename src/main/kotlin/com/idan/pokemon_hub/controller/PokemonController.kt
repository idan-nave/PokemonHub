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
    fun getAll(): List<Pokemon> {
        return pokemonService.getAll()
    }

    @GetMapping("/{pokedex}")
    fun geByPokedex(@PathVariable pokedex: Long): Pokemon? {
        return pokemonService.geByPokedex(pokedex) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Pokemon not found"
        )
    }

    @PutMapping("/{pokedex}")
    fun updateByPokedex(@PathVariable pokedex: Long, @RequestBody pokemon: Pokemon): Pokemon? {
        return pokemonService.updateByPokedex(pokedex, pokemon) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "Pokemon not found"
        )
    }

    @DeleteMapping("/{pokedex}")
    fun deleteByPokedex(@PathVariable pokedex: Long): ResponseEntity<Void> {
        pokemonService.deleteByPokedex(pokedex)
        return ResponseEntity.noContent().build()
    }
}
