package com.idan.pokemon_hub.init

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.model.PokemonImage
import com.idan.pokemon_hub.repository.PokemonRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ActiveProfiles
import java.net.URI

@ActiveProfiles("test")
@SpringBootTest
class PokemonInitializerTest {

    @Test
    fun `should deserialize test pokedex correctly`() {
        val mapper = jacksonObjectMapper().apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
        val file = ClassPathResource("TestPokedex.json").file
        val pokedex: List<PokemonInitializer.PokemonDTO> = mapper.readValue(file)

        assertThat(pokedex).hasSize(2)
        assertThat(pokedex[0].name.english).isEqualTo("Bulbasaur")
    }

    @Test
    fun `should save pokemon with image via repository`() {
        val mockRepo = mock<PokemonRepository>()
        val pokemon = Pokemon(pokedex = 1L, 0,image = PokemonImage(imageUrl = URI("https://example.com/test.png").toURL()))
        mockRepo.save(pokemon)
        verify(mockRepo).save(pokemon)
    }
}