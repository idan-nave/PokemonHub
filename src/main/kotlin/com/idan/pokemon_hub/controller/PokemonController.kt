package com.idan.pokemon_hub.controller

import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.service.PokemonService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/pokemons")
class PokemonController(private val pokemonService: PokemonService) {

    // BE ADVISED- a Global Exception Handler is handling the custom exceptions globally via @ControllerAdvice
    @GetMapping
    fun getAll(): List<Pokemon> {
        return pokemonService.getAll()
    }

    @GetMapping("/{pokedex}")
    fun getByPokedex(@PathVariable pokedex: Long): Pokemon {
        // Let the service throw PokemonNotFoundException if not found, which will be caught by @ControllerAdvice
        return pokemonService.getByPokedex(pokedex)
    }

    @PutMapping("/{pokedex}")
    fun updateByPokedex(@PathVariable pokedex: Long, @RequestBody pokemon: Pokemon): Pokemon {
        // Let the service throw PokemonNotFoundException if not found, which will be caught by @ControllerAdvice
        return pokemonService.updateByPokedex(pokedex, pokemon)
    }

    @DeleteMapping("/{pokedex}")
    fun deleteByPokedex(@PathVariable pokedex: Long): ResponseEntity<Void> {
        pokemonService.deleteByPokedex(pokedex)
        return ResponseEntity.noContent().build()
    }
}
