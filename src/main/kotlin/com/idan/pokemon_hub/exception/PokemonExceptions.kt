package com.idan.pokemon_hub.exception

// Exception for when a Pokemon is not found
class PokemonNotFoundException(message: String) : RuntimeException(message)

// Exception for when fields (like name or type) are invalid
class InvalidFieldException(message: String) : RuntimeException(message)

// Exception for when the type of a Pokemon is invalid
class InvalidTypeException(message: String) : RuntimeException(message)
