package com.idan.pokemon_hub.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.idan.pokemon_hub.PokemonController
import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.service.PokemonService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.net.URI

@WebMvcTest(PokemonController::class)
class PokemonControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var pokemonService: PokemonService

    private val objectMapper = jacksonObjectMapper()
    private val baseUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"
    private val testPokemon = Pokemon(1, "Bulbasaur", "grass/poison", URI("$baseUrl/1.png").toURL())

    @BeforeEach
    fun setUp() {
        whenever(pokemonService.getAll()).thenReturn(listOf(testPokemon))

        whenever(pokemonService.geByPokedex(any())).thenAnswer { invocation ->
            val id = invocation.getArgument<Long>(0)
            if (id == 1L) testPokemon else null
        }

        whenever(pokemonService.updateByPokedex(any(), any())).thenAnswer { invocation ->
            val id = invocation.getArgument<Long>(0)
            if (id == 1L) testPokemon.copy(name = "Updated Name") else null
        }

        doNothing().whenever(pokemonService).deleteByPokedex(any())
    }


    @Test
    @DisplayName("should return all pokemons when GET /pokemons is called")
    fun shouldReturnAllPokemonsWhenGetAllIsCalled() {
        // when + then
        mockMvc.perform(get("/pokemons"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].pokedex").value(1))
            .andExpect(jsonPath("$[0].name").value("Bulbasaur"))
            .andExpect(jsonPath("$[0].type").value("grass/poison"))
    }

    @Test
    @DisplayName("should return pokemon when GET /pokemons/{pokedex} is called with valid id")
    fun shouldReturnPokemonWhenValidPokedexIsProvided() {
        // when + then
        mockMvc.perform(get("/pokemons/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.pokedex").value(1))
            .andExpect(jsonPath("$.name").value("Bulbasaur"))
            .andExpect(jsonPath("$.type").value("grass/poison"))
    }

    @Test
    @DisplayName("should return 404 when GET /pokemons/{pokedex} is called with invalid id")
    fun shouldReturnNotFoundWhenInvalidPokedexIsProvided() {
        // when + then
        mockMvc.perform(get("/pokemons/2"))
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("should update pokemon when PUT /pokemons/{pokedex} is called with valid data")
    fun shouldupdateByPokedexWhenValidInputProvided() {
        // given
        val updatedPokemon = testPokemon.copy(name = "Updated Name")
        val requestBody = objectMapper.writeValueAsString(updatedPokemon)

        // when + then
        mockMvc.perform(put("/pokemons/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Updated Name"))
    }

    @Test
    @DisplayName("should return 404 when PUT /pokemons/{pokedex} is called with invalid id")
    fun shouldReturnNotFoundWhenUpdatingNonexistentPokemon() {
        // given
        val updatedPokemon = testPokemon.copy(name = "Updated Name")
        val requestBody = objectMapper.writeValueAsString(updatedPokemon)

        // when + then
        mockMvc.perform(put("/pokemons/2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("should delete pokemon when DELETE /pokemons/{pokedex} is called")
    fun shoulddeleteByPokedexWhenValidPokedexIsProvided() {
        // when + then
        mockMvc.perform(delete("/pokemons/1"))
            .andExpect(status().isNoContent)
    }
}
