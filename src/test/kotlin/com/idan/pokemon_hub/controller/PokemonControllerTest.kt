package com.idan.pokemon_hub.controller

import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.service.PokemonService
import org.springframework.test.web.servlet.MockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.idan.pokemon_hub.PokemonController
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
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
        whenever(pokemonService.getAllPokemons()).thenReturn(listOf(testPokemon))

        whenever(pokemonService.getPokemonByPokedex(any())).thenAnswer { invocation ->
            val id = invocation.getArgument<Long>(0)
            if (id == 1L) testPokemon else null
        }

        whenever(pokemonService.updatePokemon(any(), any())).thenReturn(testPokemon.copy(name = "Updated Name"))
        doNothing().whenever(pokemonService).deletePokemon(any())
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