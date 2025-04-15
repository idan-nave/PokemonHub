package com.idan.pokemon_hub.service

import com.idan.pokemon_hub.exception.InvalidFieldException
import com.idan.pokemon_hub.exception.PokemonNotFoundException
import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.model.PokemonImage
import com.idan.pokemon_hub.model.PokemonType
import com.idan.pokemon_hub.repository.PokemonRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.mockito.kotlin.*
import java.net.URI
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class PokemonServiceTest {

    private val pokemonRepository: PokemonRepository = mock()
    private lateinit var pokemonService: PokemonService

    private val baseUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"

    private lateinit var existingPokemon: Pokemon
    private lateinit var updatedPokemon: Pokemon

    @BeforeEach
    fun setUp() {
        pokemonService = PokemonService(pokemonRepository)

        existingPokemon = Pokemon(
            1,
            "Bulbasaur",
            setOf(PokemonType.GRASS),
            PokemonImage(1, URI("${baseUrl}1.png").toURL())
        )
        updatedPokemon = Pokemon(
            1,
            "Updated Name",
            setOf(PokemonType.FIRE),
            PokemonImage(1, URI("${baseUrl}1.png").toURL())
        )
    }

    @Test
    fun `should return all pokemons when repository has data`() {
        val pokemon1 = existingPokemon
        val pokemon2 = Pokemon(
            2,
            "Charmander",
            setOf(PokemonType.FIRE),
            PokemonImage(2, URI("${baseUrl}2.png").toURL())
        )
        val pokemonsList = listOf(pokemon1, pokemon2)
        whenever(pokemonRepository.findAll()).thenReturn(pokemonsList)

        val result = pokemonService.getAll()

        assertThat(result).isNotNull
        assertThat(result).hasSize(2)
        assertThat(result).contains(pokemon1, pokemon2)
    }

    @Test
    fun `should return pokemon when pokedex exists`() {
        whenever(pokemonRepository.findById(1)).thenReturn(Optional.of(existingPokemon))

        val result = pokemonService.getByPokedex(1)

        assertThat(result).isNotNull
        assertThat(result.name).isEqualTo("Bulbasaur")
        assertThat(result.pokedex).isEqualTo(1)
    }

    @Test
    fun `should throw PokemonNotFoundException when pokedex does not exist`() {
        whenever(pokemonRepository.findById(2)).thenReturn(Optional.empty())

        val exception = assertThrows<PokemonNotFoundException> {
            pokemonService.getByPokedex(2)
        }

        assertThat(exception.message).isEqualTo("Pokemon with Pokedex 2 not found")
    }

    @Test
    fun `should update pokemon when input is valid`() {
        whenever(pokemonRepository.findById(1)).thenReturn(Optional.of(existingPokemon))
        whenever(pokemonRepository.save(existingPokemon)).thenReturn(updatedPokemon)

        val result = pokemonService.updateByPokedex(1, updatedPokemon)

        assertThat(result).isNotNull
        assertThat(result.name).isEqualTo("Updated Name")
        assertThat(result.type).isEqualTo(setOf(PokemonType.FIRE))
        assertThat(result.pokedex).isEqualTo(1)
        verify(pokemonRepository, times(1)).save(existingPokemon)
    }

    @Test
    fun `should throw InvalidFieldException when type is empty`() {
        val invalid = existingPokemon.copy(type = emptySet())

        whenever(pokemonRepository.findById(1)).thenReturn(Optional.of(existingPokemon))

        val exception = assertThrows<InvalidFieldException> {
            pokemonService.updateByPokedex(1, invalid)
        }

        assertThat(exception.message).isEqualTo("Type cannot be empty")
    }

    @Test
    fun `should throw InvalidFieldException when name is empty`() {
        val invalid = existingPokemon.copy(name = "")

        whenever(pokemonRepository.findById(1)).thenReturn(Optional.of(existingPokemon))

        val exception = assertThrows<InvalidFieldException> {
            pokemonService.updateByPokedex(1, invalid)
        }

        assertThat(exception.message).isEqualTo("Name cannot be empty")
    }

    @Test
    fun `should delete pokemon when pokedex is valid`() {
        whenever(pokemonRepository.findById(1)).thenReturn(Optional.of(existingPokemon))

        pokemonService.deleteByPokedex(1)

        verify(pokemonRepository, times(1)).delete(existingPokemon)
    }

    @Test
    fun `should throw PokemonNotFoundException when attempting to delete a non-existent pokemon`() {
        whenever(pokemonRepository.findById(999)).thenReturn(Optional.empty())

        val exception = assertThrows<PokemonNotFoundException> {
            pokemonService.deleteByPokedex(999)
        }

        assertThat(exception.message).isEqualTo("Pokemon with Pokedex 999 not found")
    }
}
