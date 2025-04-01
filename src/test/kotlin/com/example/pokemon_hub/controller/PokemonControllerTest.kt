package com.idan.pokemon_hub

import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.service.PokemonService
import io.mockk.every
import io.mockk.Runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.net.URL

@WebMvcTest(PokemonController::class)
class PokemonControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val pokemonService: PokemonService = mock()  // Mocking the service using Mockito

    private val objectMapper = jacksonObjectMapper()

    private val baseUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"
    private val testPokemon = Pokemon(1, "Bulbasaur", "grass/poison", URL("$baseUrl/1.png"))

    @BeforeEach
    fun setUp() {
        // Use mock behavior
        every { pokemonService.getAllPokemons() } returns listOf(testPokemon)
        every { pokemonService.getPokemonByPokedex(1) } returns testPokemon
        every { pokemonService.getPokemonByPokedex(2) } returns null
        every { pokemonService.updatePokemon(1, any()) } returns testPokemon.copy(name = "Updated Name")
        every { pokemonService.updatePokemon(2, any()) } returns null
        every { pokemonService.deletePokemon(any<Long>()) } returns Unit
    }

    @Test
    fun testGetAllPokemons() {
        mockMvc.perform(get("/pokemons"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].pokedex").value(1))
            .andExpect(jsonPath("$[0].name").value("Bulbasaur"))
            .andExpect(jsonPath("$[0].type").value("grass/poison"))
    }

    @Test
    fun testGetPokemonByPokedex() {
        mockMvc.perform(get("/pokemons/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.pokedex").value(1))
            .andExpect(jsonPath("$.name").value("Bulbasaur"))
            .andExpect(jsonPath("$.type").value("grass/poison"))

        mockMvc.perform(get("/pokemons/2"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun testUpdatePokemon() {
        val updatedPokemon = testPokemon.copy(name = "Updated Name")
        val requestBody = objectMapper.writeValueAsString(updatedPokemon)

        mockMvc.perform(put("/pokemons/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Updated Name"))

        mockMvc.perform(put("/pokemons/2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
            .andExpect(status().isNotFound)
    }

    @Test
    fun testDeletePokemon() {
        mockMvc.perform(delete("/pokemons/1"))
            .andExpect(status().isNoContent)
    }
}
