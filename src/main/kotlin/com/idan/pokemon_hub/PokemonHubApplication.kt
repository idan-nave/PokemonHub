package com.idan.pokemon_hub

import com.idan.pokemon_hub.init.PokemonInitializer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext

@SpringBootApplication
class PokemonHubApplication

fun main(args: Array<String>) {
	val appContext: ApplicationContext = runApplication<PokemonHubApplication>(*args)
	val pokemonInitializer = appContext.getBean(PokemonInitializer::class.java)
	pokemonInitializer.initializePokemonData()
}
