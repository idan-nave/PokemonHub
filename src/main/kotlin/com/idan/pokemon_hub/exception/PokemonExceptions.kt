package com.idan.pokemon_hub.exception

class PokemonNotFoundException(pokedex: Long) : RuntimeException("Pokemon with Pokedex $pokedex not found")

class InvalidFieldException(field: String) : RuntimeException("$field cannot be empty")

class InvalidTypeException(type: String) : RuntimeException("Invalid Pokemon type: $type")
