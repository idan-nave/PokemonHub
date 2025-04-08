package com.idan.pokemon_hub.repository

import com.idan.pokemon_hub.model.PokemonImage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PokemonImageRepository : JpaRepository<PokemonImage, Long> {
}
