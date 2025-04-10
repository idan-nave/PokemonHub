package com.idan.pokemon_hub.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

data class ErrorResponse(val message: String, val status: Int)

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(PokemonNotFoundException::class)
    fun handlePokemonNotFoundException(ex: PokemonNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(ex.message ?: "Pokemon not found", HttpStatus.NOT_FOUND.value()))
    }

    @ExceptionHandler(InvalidFieldException::class)
    fun handleInvalidFieldException(ex: InvalidFieldException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(ex.message ?: "Invalid field", HttpStatus.BAD_REQUEST.value()))
    }

    @ExceptionHandler(InvalidTypeException::class)
    fun handleInvalidTypeException(ex: InvalidTypeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(ex.message ?: "Invalid type", HttpStatus.BAD_REQUEST.value()))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("An unexpected error occurred: ${ex.message}", HttpStatus.INTERNAL_SERVER_ERROR.value()))
    }
}