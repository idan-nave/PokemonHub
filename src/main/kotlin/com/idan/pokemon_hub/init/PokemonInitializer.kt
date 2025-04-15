package com.idan.pokemon_hub.init

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.model.PokemonImage
import com.idan.pokemon_hub.model.PokemonType
import com.idan.pokemon_hub.repository.PokemonRepository
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URI

@Service
@Profile("!test") // Don't run in the "test" profile
class PokemonInitializer(private val pokemonRepository: PokemonRepository) {
    private val logger = LoggerFactory.getLogger(PokemonInitializer::class.java)

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class PokemonDTO(
        val pokedex: Int, val name: Name, val type: List<String>, val image: Image
    )

    data class Name(
        val english: String = "", val japanese: String? = null, val chinese: String? = null, val french: String? = null
    )

    data class Image(
        val hires: String = "", val sprite: String? = null, val thumbnail: String? = null
    )

    fun initializePokemonData() {
        try {
            if (pokemonRepository.count() > 0) {
                logger.info("Pokémon data already initialized.")
                return
            }

            val inputStream = ClassPathResource("Pokedex.json").inputStream
            val pokedex: List<PokemonDTO> =
                jacksonObjectMapper().readValue(inputStream, jacksonTypeRef<List<PokemonDTO>>())

            val pokemons = pokedex.take(151).map { entry ->
                val image = PokemonImage(
                    imageUrl = try {
                        URI(entry.image.hires).toURL()
                    } catch (e: Exception) {
                        logger.warn("Invalid URL for Pokémon ${entry.pokedex}: ${entry.image.hires}")
                        URI("https://example.com/default.png").toURL()
                    }
                )

                Pokemon(
                    name = entry.name.english, type = entry.type.mapNotNull { typeName ->
                        try {
                            PokemonType.valueOf(typeName.uppercase())
                        } catch (e: IllegalArgumentException) {
                            logger.warn("Unknown type '${typeName}' for Pokémon ${entry.pokedex}")
                            null
                        }
                    }.toSet(), image = image
                )
            }

            pokemonRepository.saveAll(pokemons)
            logger.info("Successfully initialized ${pokemons.size} Pokémon.")
        } catch (e: IOException) {
            logger.error("Failed to initialize Pokémon data: ${e.message}", e)
            throw RuntimeException("Initialization failed", e)
        }
    }
}