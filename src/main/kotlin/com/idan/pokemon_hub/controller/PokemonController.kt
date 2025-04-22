package com.idan.pokemon_hub.controller

import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.service.PokemonService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/pokemons")
class PokemonController(private val pokemonService: PokemonService) {

    @GetMapping
    fun getAll(): List<Pokemon> {
        return pokemonService.getAll()
    }

    @GetMapping("/{pokedex}")
    fun getByPokedex(@PathVariable pokedex: Long): Pokemon {
        return pokemonService.getByPokedex(pokedex)
    }

    @PutMapping("/{pokedex}")
    fun updateByPokedex(@PathVariable pokedex: Long, @RequestBody pokemon: Pokemon): Pokemon {
        return pokemonService.updateByPokedex(pokedex, pokemon)
    }

    @DeleteMapping("/{pokedex}")
    fun deleteByPokedex(@PathVariable pokedex: Long): ResponseEntity<Void> {
        pokemonService.deleteByPokedex(pokedex)
        return ResponseEntity.noContent().build()
    }
}
