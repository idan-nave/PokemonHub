package com.idan.pokemon_hub.service


import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.repository.PokemonRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.jupiter.api.assertThrows
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus
import org.mockito.kotlin.verify
import org.mockito.kotlin.times
import java.net.URL

class PokemonServiceTest {

    private val pokemonRepository: PokemonRepository = mock()
    private val pokemonService = PokemonService(pokemonRepository)

    @Test
    fun testGetAllPokemons() {
        val baseUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"

        // Arrange
        val pokemon = Pokemon(1, "Bulbasaur", "grass/poison", URL("$baseUrl}1.png"))
        val pokemonsList = listOf(pokemon)
        whenever(pokemonRepository.findAll()).thenReturn(pokemonsList)

        // Act
        val result = pokemonService.getAllPokemons()

        // Assert
        assertEquals(pokemonsList, result)
    }

    @Test
    fun testGetPokemonByPokedex() {
        val baseUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"

        // Arrange
        val pokemon = Pokemon(1, "Bulbasaur", "grass/poison", URL("$baseUrl/1.png"))
        whenever(pokemonRepository.findByPokedex(1)).thenReturn(pokemon)
        whenever(pokemonRepository.findByPokedex(2)).thenReturn(null)

        // Act
        val result = pokemonService.getPokemonByPokedex(1)
        val result2 = pokemonService.getPokemonByPokedex(2)

        // Assert
        assertNotNull(result)
        assertEquals("Bulbasaur", result?.name)
        assertNull(result2)
    }


    @Test
    fun testUpdatePokemon() {
        val baseUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"

        // Arrange
        val pokemon = Pokemon(1, "Bulbasaur", "grass/poison", URL("$baseUrl/1.png"))
        val updatedPokemon = Pokemon(1, "Updated Name", "grass/poison", URL("$baseUrl/1.png"))
        whenever(pokemonRepository.findByPokedex(1)).thenReturn(pokemon)
        whenever(pokemonRepository.save(pokemon)).thenReturn(updatedPokemon)

        // Act
        val result = pokemonService.updatePokemon(1, updatedPokemon)

        // Assert
        assertNotNull(result)
        assertEquals("Updated Name", result?.name)
        assertEquals("grass/poison", result?.type)
    }

    @Test
    fun testUpdatePokemon_InvalidInput() {
        val baseUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"

        // Arrange
        val pokemon = Pokemon(1, "Bulbasaur", "grass/poison", URL("$baseUrl}1.png"))
        val invalidPokemon1 = Pokemon(1, "", "grass/poison")
        val invalidPokemon2 = Pokemon(1, "Bulbasaur", "invalid-type")

        whenever(pokemonRepository.findByPokedex(1)).thenReturn(pokemon)

        // Act & Assert
        val exception1 = assertThrows<ResponseStatusException> {
            pokemonService.updatePokemon(1, invalidPokemon1)
        }
        assertEquals(HttpStatus.BAD_REQUEST.value(), exception1.statusCode.value())
        assertEquals("Name and type cannot be empty", exception1.reason)

        val exception2 = assertThrows<ResponseStatusException> {
            pokemonService.updatePokemon(1, invalidPokemon2)
        }
        assertEquals(HttpStatus.BAD_REQUEST.value(), exception2.statusCode.value())
        assertEquals("Invalid type format", exception2.reason)
    }

    @Test
    fun testDeletePokemon() {
        // Arrange
        val pokedex = 1L

        // Act
        pokemonService.deletePokemon(pokedex)

        // Assert
        verify(pokemonRepository, times(1)).deleteByPokedex(pokedex)
    }
}
