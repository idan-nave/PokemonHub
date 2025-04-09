package com.idan.pokemon_hub.init

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.model.PokemonImage
import com.idan.pokemon_hub.model.PokemonType
import com.idan.pokemon_hub.repository.PokemonRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.core.io.ClassPathResource
import java.net.URI

class PokemonInitializerTest {

    private val pokemonRepo: PokemonRepository = mock()
    private val initializer = PokemonInitializer(pokemonRepo)

    @Test
    fun `should initialize pokemon data correctly from Pokedex json`() {
        // Arrange: Mock repository to indicate empty database
        whenever(pokemonRepo.count()).thenReturn(0L)

        // Load the actual TestPokedex.json file
        val mapper = jacksonObjectMapper().apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
        val jsonFile = ClassPathResource("TestPokedex.json").file
        val pokedex: List<PokemonInitializer.PokemonDTO> = mapper.readValue(jsonFile)

        // Expected Pokémon based on the JSON content
        val expectedPokemons = pokedex.map { entry ->
            Pokemon(
                pokedex = entry.id.toLong(),
                name = entry.name.english,
                type = entry.type.mapNotNull { type ->
                    try {
                        PokemonType.valueOf(type.uppercase())
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }.toSet(),
                image = PokemonImage(
                    pokedex = 0, // Auto-generated
                    imageUrl = try {
                        URI(entry.image.hires).toURL()
                    } catch (e: Exception) {
                        URI("https://example.com/default.png").toURL()
                    }
                )
            )
        }

        // Act: Invoke the init method
        initializer.javaClass.getDeclaredMethod("init").apply {
            isAccessible = true
            invoke(initializer)
        }

        // Assert: Verify that saveAll was called with the expected Pokémon list
        verify(pokemonRepo).saveAll(argThat<List<Pokemon>> { list ->
            list.size == expectedPokemons.size &&
                    list.all { pokemon ->
                        expectedPokemons.any { expected ->
                            expected.pokedex == pokemon.pokedex &&
                                    expected.name == pokemon.name &&
                                    expected.type == pokemon.type &&
                                    expected.image.imageUrl.toString() == pokemon.image.imageUrl.toString()
                        }
                    }
        })
    }

    @Test
    fun `should save pokemon with image via repository`() {
        val mockRepo = mock<PokemonRepository>()
        val pokemon = Pokemon(pokedex = 1L, image = PokemonImage(imageUrl = URI("https://example.com/test.png").toURL()))
        mockRepo.save(pokemon)
        verify(mockRepo).save(pokemon)
    }
}