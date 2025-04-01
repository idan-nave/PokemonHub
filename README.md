# Kotlin & Spring & APIs: Pokémon API

This project is a Spring Boot application built with Kotlin and Gradle. It stores a list of the original 151 Pokémon, providing endpoints to retrieve information about each Pokémon. The application also includes tests written in JUnit and uses AssertJ for assertions.

## Features
- **Pokémon List**: A list of the original 151 Pokémon.
- **Endpoints**:
    - **GET /pokemons**: Lists all available Pokémon.
    - **GET /pokemons/{id}**: Retrieve detailed information about a specific Pokémon by ID.
- **Data Stored**:
    - Pokémon Name
    - Types (e.g., Fire, Water, etc.)
    - Pokédex Number
    - Link to Pokémon Image

## Prerequisites

Before running this application, make sure you have the following installed:

- **Java 11+**: Required to run Kotlin and Spring Boot applications.
- **Gradle**: Used for building and running the project. You can install Gradle via [Gradle's website](https://gradle.org/install/), or use the Gradle wrapper (`gradlew`).
- **IntelliJ IDEA or any preferred IDE**: Recommended for Kotlin development.

## Running the Application

### 1. Clone the repository:
```bash
git clone https://github.com/your-pokemonname/kotlin-spring-pokemon-api.git
cd kotlin-spring-pokemon-api
