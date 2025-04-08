package com.idan.pokemon_hub.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.idan.pokemon_hub.exception.PokemonNotFoundException
import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.model.PokemonType
import com.idan.pokemon_hub.service.PokemonService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import java.net.URI

@WebMvcTest(PokemonController::class)
class PokemonControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var pokemonService: PokemonService

    private val objectMapper = jacksonObjectMapper()
    private val baseUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"

    private val testPokemon1 = Pokemon(1, "Bulbasaur", setOf(PokemonType.GRASS), URI("$baseUrl/1.png").toURL())
    private val testPokemon2 = Pokemon(2, "Charmander", setOf(PokemonType.FIRE), URI("$baseUrl/2.png").toURL())

    @BeforeEach
    fun setUp() {
        whenever(pokemonService.getAll()).thenReturn(listOf(testPokemon1, testPokemon2))

        whenever(pokemonService.getByPokedex(any())).thenAnswer { invocation ->
            val id = invocation.arguments[0] as Long
            when (id) {
                1L -> testPokemon1
                2L -> testPokemon2
                else -> throw PokemonNotFoundException("Pokemon with Pokedex $id not found")
            }
        }

        whenever(pokemonService.updateByPokedex(any(), any())).thenAnswer { invocation ->
            val id = invocation.arguments[0] as Long
            val pokemon = invocation.arguments[1] as Pokemon
            when (id) {
                1L -> testPokemon1.copy(name = pokemon.name)
                2L -> testPokemon2.copy(name = pokemon.name)
                else -> throw PokemonNotFoundException("Pokemon with Pokedex $id not found")
            }
        }

        doNothing().whenever(pokemonService).deleteByPokedex(any())
        whenever(pokemonService.deleteByPokedex(999)).thenThrow(PokemonNotFoundException("Pokemon with Pokedex 999 not found"))
    }

    @Test
    fun `should return all pokemons when GET pokemons is called`() {
        mockMvc.get("/pokemons") {
            accept = MediaType.APPLICATION_JSON
        }.andExpectAll {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$[0].pokedex") { value(1) }
            jsonPath("$[0].name") { value("Bulbasaur") }
            jsonPath("$[0].type[0]") { value(PokemonType.GRASS.name) }
            jsonPath("$[1].pokedex") { value(2) }
            jsonPath("$[1].name") { value("Charmander") }
            jsonPath("$[1].type[0]") { value(PokemonType.FIRE.name) }
        }
    }

    @Test
    fun `should return pokemon when GET pokemons by pokedex is called with valid id`() {
        mockMvc.get("/pokemons/1") {
            accept = MediaType.APPLICATION_JSON
        }.andExpectAll {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.pokedex") { value(1) }
            jsonPath("$.name") { value("Bulbasaur") }
            jsonPath("$.type[0]") { value(PokemonType.GRASS.name) }
        }
    }

    @Test
    fun `should return pokemon when GET pokemons by pokedex is called with second valid id`() {
        mockMvc.get("/pokemons/2") {
            accept = MediaType.APPLICATION_JSON
        }.andExpectAll {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.pokedex") { value(2) }
            jsonPath("$.name") { value("Charmander") }
            jsonPath("$.type[0]") { value(PokemonType.FIRE.name) }
        }
    }

    @Test
    fun `should return 404 when GET pokemons by pokedex is called with invalid id`() {
        mockMvc.get("/pokemons/999") {
            accept = MediaType.APPLICATION_JSON
        }.andExpectAll {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.message") { value("Pokemon with Pokedex 999 not found") }
            jsonPath("$.status") { value(404) }
        }
    }

    @Test
    fun `should update pokemon when PUT pokemons by pokedex is called with valid data`() {
        val updatedPokemon = testPokemon1.copy(name = "Updated Name")
        val requestBody = objectMapper.writeValueAsString(updatedPokemon)

        mockMvc.put("/pokemons/1") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpectAll {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.name") { value("Updated Name") }
            jsonPath("$.pokedex") { value(1) }
            jsonPath("$.type[0]") { value(PokemonType.GRASS.name) }
        }
    }

    @Test
    fun `should return 404 when PUT pokemons by pokedex is called with invalid id`() {
        val updatedPokemon = testPokemon1.copy(name = "Updated Name")
        val requestBody = objectMapper.writeValueAsString(updatedPokemon)

        mockMvc.put("/pokemons/999") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpectAll {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.message") { value("Pokemon with Pokedex 999 not found") }
            jsonPath("$.status") { value(404) }
        }
    }

    @Test
    fun `should delete pokemon when DELETE pokemons by pokedex is called`() {
        mockMvc.delete("/pokemons/1") {
            accept = MediaType.APPLICATION_JSON
        }.andExpectAll {
            status { isNoContent() }
            content { string("") }
        }
    }

    @Test
    fun `should return 404 when DELETE pokemons by pokedex is called with invalid id`() {
        mockMvc.delete("/pokemons/999") {
            accept = MediaType.APPLICATION_JSON
        }.andExpectAll {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.message") { value("Pokemon with Pokedex 999 not found") }
            jsonPath("$.status") { value(404) }
        }
    }
}