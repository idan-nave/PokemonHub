package com.idan.pokemon_hub.service

import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.repository.PokemonRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.mockito.kotlin.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
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
    @DisplayName("should return all pokemons when repository has data")
    fun shouldReturnAllPokemonsWhenRepositoryHasData() {
        // given
        val pokemon = Pokemon(1, "Bulbasaur", "grass/poison", URI("${baseUrl}1.png").toURL())
        val pokemonsList = listOf(pokemon)
        whenever(pokemonRepository.findAll()).thenReturn(pokemonsList)

        // when
        val result = pokemonService.getAll()

        // then
        assertThat(result).isEqualTo(pokemonsList)
    }

    @Test
    @DisplayName("should return pokemon when pokedex exists")
    fun shouldReturnPokemonWhenPokedexExists() {
        // given
        val pokemon = Pokemon(1, "Bulbasaur", "grass/poison", URI("${baseUrl}1.png").toURL())
        whenever(pokemonRepository.findByPokedex(1)).thenReturn(pokemon)

        // when
        val result = pokemonService.geByPokedex(1)

        // then
        assertThat(result).isNotNull
        assertThat(result?.name).isEqualTo("Bulbasaur")
    }

    @Test
    @DisplayName("should throw NOT_FOUND when pokedex does not exist")
    fun shouldThrowNotFoundWhenPokedexDoesNotExist() {
        // given
        whenever(pokemonRepository.findByPokedex(2)).thenReturn(null)

        // when
        val exception = assertThrows<ResponseStatusException> {
            pokemonService.geByPokedex(2)
        }

        // then
        assertThat(exception.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(exception.reason).isEqualTo("Pokemon not found")
    }

    @Test
    @DisplayName("should update pokemon when input is valid")
    fun shouldupdateByPokedexWhenInputIsValid() {
        // given
        val existing = Pokemon(1, "Bulbasaur", "grass/poison", URI("${baseUrl}1.png").toURL())
        val updated = Pokemon(1, "Updated Name", "grass/poison", URI("${baseUrl}1.png").toURL())

        whenever(pokemonRepository.findByPokedex(1)).thenReturn(existing)
        whenever(pokemonRepository.save(updated)).thenReturn(updated)

        // when
        val result = pokemonService.updateByPokedex(1, updated)

        // then
        assertThat(result).isNotNull
        assertThat(result?.name).isEqualTo("Updated Name")
        assertThat(result?.type).isEqualTo("grass/poison")
    }

    @Test
    @DisplayName("should throw BAD_REQUEST when name is empty")
    fun shouldThrowBadRequestWhenNameIsEmpty() {
        // given
        val existing = Pokemon(1, "Bulbasaur", "grass/poison", URI("${baseUrl}1.png").toURL())
        val invalid = Pokemon(1, "", "grass/poison")

        whenever(pokemonRepository.findByPokedex(1)).thenReturn(existing)

        // when
        val exception = assertThrows<ResponseStatusException> {
            pokemonService.updateByPokedex(1, invalid)
        }

        // then
        assertThat(exception.statusCode.value()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(exception.reason).isEqualTo("Name and type cannot be empty")
    }

    @Test
    @DisplayName("should throw BAD_REQUEST when type is invalid")
    fun shouldThrowBadRequestWhenTypeIsInvalid() {
        // given
        val existing = Pokemon(1, "Bulbasaur", "grass/poison", URI("${baseUrl}1.png").toURL())
        val invalid = Pokemon(1, "Bulbasaur", "invalid-type")

        whenever(pokemonRepository.findByPokedex(1)).thenReturn(existing)

        // when
        val exception = assertThrows<ResponseStatusException> {
            pokemonService.updateByPokedex(1, invalid)
        }

        // then
        assertThat(exception.statusCode.value()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        assertThat(exception.reason).isEqualTo("Invalid type format")
    }

    @Test
    @DisplayName("should delete pokemon when pokedex is valid")
    fun shoulddeleteByPokedexWhenPokedexIsValid() {
        // given
        val pokedex = 1L

        // when
        pokemonService.deleteByPokedex(pokedex)

        // then
        verify(pokemonRepository, times(1)).deleteByPokedex(pokedex)
    }
}
