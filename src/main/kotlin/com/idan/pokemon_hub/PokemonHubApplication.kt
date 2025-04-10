package com.idan.pokemon_hub

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PokemonHubApplication

fun main(args: Array<String>) {
	runApplication<PokemonHubApplication>(*args)
}
