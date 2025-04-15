package com.idan.pokemon_hub.init

import com.idan.pokemon_hub.model.Pokemon
import com.idan.pokemon_hub.model.PokemonImage
import com.idan.pokemon_hub.model.PokemonType
import com.idan.pokemon_hub.repository.PokemonRepository
import com.idan.pokemon_hub.repository.PokemonImageRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.net.URI
import org.assertj.core.api.Assertions.assertThat
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@ActiveProfiles("dataTest")
@SpringBootTest
@Transactional
class PokemonPersistenceTest {

    @Autowired
    private lateinit var pokemonRepository: PokemonRepository

    @Autowired
    private lateinit var pokemonImageRepository: PokemonImageRepository

    @Test
    fun `should save pokemon and cascade to pokemon image`() {
        // given
        val pokemon = Pokemon(
            name = "Pikachu",
            type = setOf(PokemonType.ELECTRIC),
            image = PokemonImage(
                imageUrl = URI("https://example.com/pikachu.png").toURL()
            )
        )

        // when
        pokemonRepository.save(pokemon)

        // then
        val savedPokemon = pokemonRepository.findById(1L).get()
        assertThat(savedPokemon.image).isNotNull
        assertThat(savedPokemon.image.imageUrl.toString()).isEqualTo("https://example.com/pikachu.png")

        val savedImage = pokemonImageRepository.findById(savedPokemon.image.pokedex).get()
        assertThat(savedImage.imageUrl.toString()).isEqualTo("https://example.com/pikachu.png")
    }
}
