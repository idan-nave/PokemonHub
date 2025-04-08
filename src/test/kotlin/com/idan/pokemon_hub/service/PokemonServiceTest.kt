package com.idan.pokemon_hub.service

import com.idan.pokemon_hub.exception.InvalidFieldException
import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.model.PokemonType
import com.idan.pokemon_hub.repository.PokemonRepository
import com.idan.pokemon_hub.exception.PokemonNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.mockito.kotlin.*
import java.net.URI

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class PokemonServiceTest {

    private val pokemonRepository: PokemonRepository = mock()
    private lateinit var pokemonService: PokemonService

    private val baseUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"

    @BeforeEach
    fun setUp() {
        pokemonService = PokemonService(pokemonRepository)
    }

    @Test
    fun `should return all pokemons when repository has data`() {
        // given
        val pokemon1 = Pokemon(1, "Bulbasaur", setOf(PokemonType.GRASS), URI("${baseUrl}1.png").toURL())
        val pokemon2 = Pokemon(2, "Charmander", setOf(PokemonType.FIRE), URI("${baseUrl}2.png").toURL())
        val pokemonsList = listOf(pokemon1, pokemon2)
        whenever(pokemonRepository.findAll()).thenReturn(pokemonsList)

        // when
        val result = pokemonService.getAll()

        // then
        assertThat(result).isNotNull
        assertThat(result).hasSize(2)
        assertThat(result).contains(pokemon1, pokemon2)
        assertThat(result[0].name).isEqualTo("Bulbasaur")
        assertThat(result[1].name).isEqualTo("Charmander")
    }

    @Test
    fun `should return pokemon when pokedex exists`() {
        // given
        val pokemon = Pokemon(1, "Bulbasaur", setOf(PokemonType.GRASS), URI("${baseUrl}1.png").toURL())
        whenever(pokemonRepository.findByPokedex(1)).thenReturn(pokemon)

        // when
        val result = pokemonService.getByPokedex(1) // Fix to getByPokedex if service is updated

        // then
        assertThat(result).isNotNull
        assertThat(result.name).isEqualTo("Bulbasaur")
        assertThat(result.type).isEqualTo(setOf(PokemonType.GRASS))
        assertThat(result.pokedex).isEqualTo(1)
    }

    @Test
    fun `should throw PokemonNotFoundException when pokedex does not exist`() {
        // given
        whenever(pokemonRepository.findByPokedex(2)).thenReturn(null)

        // when
        val exception = assertThrows<PokemonNotFoundException> {
            pokemonService.getByPokedex(2) // Fix to getByPokedex if service is updated
        }

        // then
        assertThat(exception.message).isEqualTo("Pokemon with Pokedex 2 not found")
        assertThat(exception).isInstanceOf(PokemonNotFoundException::class.java)
    }

    @Test
    fun `should update pokemon when input is valid`() {
        // given
        val existing = Pokemon(1, "Bulbasaur", setOf(PokemonType.GRASS), URI("${baseUrl}1.png").toURL())
        val updated = Pokemon(1, "Updated Name", setOf(PokemonType.FIRE), URI("${baseUrl}1.png").toURL())

        whenever(pokemonRepository.findByPokedex(1)).thenReturn(existing)
        whenever(pokemonRepository.save(existing)).thenReturn(updated)

        // when
        val result = pokemonService.updateByPokedex(1, updated)

        // then
        assertThat(result).isNotNull
        assertThat(result.name).isEqualTo("Updated Name")
        assertThat(result.type).isEqualTo(setOf(PokemonType.FIRE))
        assertThat(result.pokedex).isEqualTo(1)
        verify(pokemonRepository, times(1)).save(existing)
    }

    @Test
    fun `should throw InvalidFieldException when type is empty`() {
        // given
        val existing = Pokemon(1, "Bulbasaur", setOf(PokemonType.GRASS), URI("${baseUrl}1.png").toURL())
        val invalid = Pokemon(1, "Bulbasaur", emptySet())

        whenever(pokemonRepository.findByPokedex(1)).thenReturn(existing)

        // when
        val exception = assertThrows<InvalidFieldException> {
            pokemonService.updateByPokedex(1, invalid)
        }

        // then
        assertThat(exception.message).isEqualTo("Type cannot be empty")
        assertThat(exception).isInstanceOf(InvalidFieldException::class.java)
    }

    @Test
    fun `should throw InvalidFieldException when name is empty`() {
        // given
        val existing = Pokemon(1, "Bulbasaur", setOf(PokemonType.GRASS), URI("${baseUrl}1.png").toURL())
        val invalid = Pokemon(1, "", setOf(PokemonType.GRASS))

        whenever(pokemonRepository.findByPokedex(1)).thenReturn(existing)

        // when
        val exception = assertThrows<InvalidFieldException> {
            pokemonService.updateByPokedex(1, invalid)
        }

        // then
        assertThat(exception.message).isEqualTo("Name cannot be empty")
        assertThat(exception).isInstanceOf(InvalidFieldException::class.java)
    }

    @Test
    fun `should delete pokemon when pokedex is valid`() {
        // given
        val pokedex = 1L
        val existing = Pokemon(1, "Bulbasaur", setOf(PokemonType.GRASS), URI("${baseUrl}1.png").toURL())
        whenever(pokemonRepository.findByPokedex(pokedex)).thenReturn(existing)

        // when
        pokemonService.deleteByPokedex(pokedex)

        // then
        verify(pokemonRepository, times(1)).delete(existing)
    }

    @Test
    fun `should throw NOT_FOUND when attempting to delete a non-existent pokemon`() {
        // given
        val pokedex = 999L
        whenever(pokemonRepository.findByPokedex(pokedex)).thenReturn(null)

        // when
        val exception = assertThrows<PokemonNotFoundException> {
            pokemonService.deleteByPokedex(pokedex)
        }

        // then
        assertThat(exception.message).isEqualTo("Pokemon with Pokedex 999 not found")
        assertThat(exception).isInstanceOf(PokemonNotFoundException::class.java)
    }
}