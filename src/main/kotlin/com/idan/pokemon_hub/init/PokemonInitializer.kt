package com.idan.pokemon_hub.init

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.model.PokemonImage
import com.idan.pokemon_hub.model.PokemonType
import com.idan.pokemon_hub.repository.PokemonRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URI

@Service
@Profile("!dataTest")
class PokemonInitializer(private val pokemonRepository: PokemonRepository) {
    private val logger = LoggerFactory.getLogger(PokemonInitializer::class.java)

    @Transactional
    fun initializePokemonData() {
        try {
            if (pokemonRepository.count() > 0) {
                logger.info("Pokémon data already initialized.")
                return
            }
            val pokedex = loadPokemonData()
            val pokemons = processPokemonData(pokedex)
            savePokemons(pokemons)
        } catch (e: Exception) {
            logger.error("Failed to initialize Pokémon data: ${e.message}", e)
            throw RuntimeException("Initialization failed", e)
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class PokemonDTO(
        val id: Long, val name: NameDTO, val type: List<String>, val image: ImageDTO
    )

    data class NameDTO(
        val english: String = "", val japanese: String? = null, val chinese: String? = null, val french: String? = null
    )

    data class ImageDTO(
        val hires: String = "", val sprite: String? = null, val thumbnail: String? = null
    )

    private fun loadPokemonData(): List<PokemonDTO> {
        return try {
            val inputStream = ClassPathResource("Pokedex.json").inputStream
            val pokemons = jacksonObjectMapper().readValue(inputStream, jacksonTypeRef<List<PokemonDTO>>())
            val duplicates = pokemons.groupBy { it.id }.filter { it.value.size > 1 }
            if (duplicates.isNotEmpty()) {
                duplicates.forEach { (pokedex, entries) ->
                    logger.error("Duplicate pokedex ID: $pokedex, found in entries: $entries")
                }
                throw IllegalStateException("Duplicate pokedex entries in Pokedex.json: $duplicates")
            }
            pokemons
        } catch (e: IOException) {
            logger.error("Failed to load Pokémon data file: ${e.message}", e)
            throw RuntimeException("Failed to load Pokémon data file", e)
        }
    }

    private fun processPokemonData(pokedex: List<PokemonDTO>): List<Pokemon> {
        return pokedex.take(151).map { entry ->
            val image = createPokemonImage(entry)
            createPokemon(entry, image)
        }
    }

    private fun createPokemonImage(entry: PokemonDTO): PokemonImage {
        return try {
            PokemonImage(
                pokedex = entry.id,
                imageUrl = URI(entry.image.hires).toURL()
            )
        } catch (e: Exception) {
            logger.warn("Invalid URL for Pokémon image: ${entry.image.hires}")
            PokemonImage(
                pokedex = entry.id,
                imageUrl = URI("https://example.com/default.png").toURL()
            )
        }
    }

    private fun mapTypes(typeNames: List<String>): Set<PokemonType> {
        return typeNames.mapNotNull { typeName ->
            try {
                PokemonType.valueOf(typeName.uppercase())
            } catch (e: IllegalArgumentException) {
                logger.warn("Unknown type '$typeName'")
                null
            }
        }.toSet()
    }

    private fun createPokemon(entry: PokemonDTO, image: PokemonImage): Pokemon {
        return Pokemon(
            pokedex = entry.id,
            name = entry.name.english,
            type = mapTypes(entry.type),
            image = image
        )
    }

    private fun savePokemons(pokemons: List<Pokemon>) {
        try {
            pokemonRepository.saveAll(pokemons)
            logger.info("Successfully initialized ${pokemons.size} Pokémon.")
        } catch (e: Exception) {
            logger.error("Failed to save Pokémon data: ${e.message}", e)
            throw RuntimeException("Failed to save Pokémon data", e)
        }
    }
}