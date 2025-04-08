package com.idan.pokemon_hub.init

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.model.PokemonImage
import com.idan.pokemon_hub.model.PokemonType
import com.idan.pokemon_hub.repository.PokemonImageRepository
import com.idan.pokemon_hub.repository.PokemonRepository
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.URI
import kotlin.system.exitProcess

@Component
class PokemonInitializer(
    private val pokemonRepository: PokemonRepository,
    private val pokemonImageRepository: PokemonImageRepository
) {
    private val logger = LoggerFactory.getLogger(PokemonInitializer::class.java)

    //  temp DTO to match JSON
    data class RawPokemon(
        val id: Int,
        val name: Name,
        val type: List<String>,
        val image: Image
    )

    data class Name(val english: String)
    data class Image(val hires: String)

    @PostConstruct
    fun init() {
        try {
            if (pokemonRepository.count() > 0) {
                logger.info("Pokémon data already initialized.")
                return
            }

            val mapper = jacksonObjectMapper() // to parse JSON
            val file = ClassPathResource("Pokedex.json").file
            val pokedex: List<RawPokemon> = mapper.readValue(file)

            val pokemons = pokedex.map {
                Pokemon(
                    pokedex = it.id.toLong(),
                    name = it.name.english,
                    type = it.type.mapNotNull { typeName ->
                        try {
                            PokemonType.valueOf(typeName.uppercase())
                        } catch (e: IllegalArgumentException) {
                            logger.warn("Unknown Pokémon type '${typeName}' for Pokémon ${it.id}")
                            null
                        }
                    }.toSet()
                )
            }

            val images = pokedex.map {
                PokemonImage(
                    pokedex = it.id.toLong(),
                    imageUrl = try {
                        URI(it.image.hires).toURL()
                    } catch (e: Exception) {
                        logger.warn("Invalid URL for Pokémon ${it.id}: ${it.image.hires}")
                        null
                    }
                )
            }

            pokemonRepository.saveAll(pokemons)
            pokemonImageRepository.saveAll(images)

            logger.info("Successfully initialized ${pokemons.size} Pokémon.")
        } catch (e: IOException) {
            logger.error("Error initializing Pokémon data: ${e.message}, \nclosing the app!", e)
            exitProcess(1) // Terminate the app with exit code 1
        } catch (e: Exception) {
            logger.error("Unexpected error: ${e.message}", e)
            exitProcess(1) // Terminate the app with exit code 1
        }
    }
}
