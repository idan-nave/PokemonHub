# Kotlin & Spring & APIs: Pokémon API

This project is a Spring Boot application built with Kotlin and Gradle. It stores a list of the original 151 Pokémon, providing endpoints to retrieve information about each Pokémon.
The application also includes tests written in JUnit and uses AssertJ for assertions.
Data initially stored in memory using H2 database, and is initialized with a hardcoded list of the original 151 Pokémon.

## Features
- **Pokémon List**: A list of the original 151 Pokémon.
- **Endpoints**:
    - **GET /pokemons**: Lists all available Pokémon.
    - **GET /pokemons/{pokedex}**: Retrieve detailed information about a specific Pokémon by pokedex.
- **Data Stored**:
    - Pokédex Number
    - Pokémon Name
    - Types (e.g., Fire, Water, etc.)
    - Link to Pokémon Image
- PokemonExceptions.kt contains the exception classes and DataInit.kt loads Pokedex.json.
## Prerequisites

Before running this application, make sure you have the following installed:

- **Java 21+**: Required to run Kotlin and Spring Boot applications.
- **Gradle**: Used for building and running the project. You can install Gradle via [Gradle's website](https://gradle.org/install/), or use the Gradle wrapper (`gradlew`).
- **IntelliJ IDEA or any preferred IDE**: Recommended for Kotlin development.

## Running the Application

### 1. Clone the repository:
```bash
git clone https://github.com/your-pokemonname/kotlin-spring-pokemon-api.git
cd kotlin-spring-pokemon-api
```

### 2. Build and run the application:
Once you have cloned the repository, you can build and run the application using Gradle.

#### Using Gradle Wrapper
If you have Gradle wrapper (`gradlew`), simply run the following command to build and start the application:

```
./gradlew bootRun
```

This will start the Spring Boot application, and by default, it will run on `http://localhost:8080`.

#### Build without running
Alternatively, you can just build the application without running it:

```
./gradlew build
```

### 3. Accessing the API
Once the application is running, you can use the following endpoints to interact with the API.

#### List all Pokémon
**GET** `/pokemons`

This endpoint will return a list of all Pokémon, including their pokedex, name, types, and image URL.

Example response:
```json
[
  {
    "pokedex": 1,
    "name": "Bulbasaur",
    "types": ["Grass", "Poison"],
    "pokedexNumber": 1,
    "image": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
  },
  {
    "pokedex": 2,
    "name": "Ivysaur",
    "types": ["Grass", "Poison"],
    "pokedexNumber": 2,
    "image": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/2.png"
  }
]
```

#### Get a Pokémon by pokedex
**GET** `/pokemons/{pokedex}`

This endpoint retrieves detailed information about a specific Pokémon by its unique pokedex.

Example request:
```bash
GET /pokemons/1
```

Example response:
```json
{
  "pokedex": 1,
  "name": "Bulbasaur",
  "types": ["Grass", "Poison"],
  "pokedexNumber": 1,
  "image": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
}
```

### 4. Running Tests
To ensure everything works correctly, you can run the unit tests for the application. These tests use JUnit and AssertJ for assertions.

#### Running Tests with Gradle:
```bash
./gradlew test
```

#### Tests:
The tests cover the following functionalities:
- Retrieve the list of Pokémon (`/pokemons`).
- Retrieve details of a specific Pokémon (`/pokemons/{pokedex}`).
- Full CRUD operations for Pokémon are also implemented and tested, examples:
  GET /pokemons - List all Pokémon
  GET /pokemons/1 - Get Pokémon with Pokédex ID 1
  PUT /pokemons/1 - Update Pokémon with Pokédex ID 1
  DELETE /pokemons/1 - Delete Pokémon with Pokédex ID 1
- Validate that the correct HTTP status codes are returned (e.g., `200 OK` for successful requests, `404 Not Found` for nonexistent Pokémon).

### 5. Project Structure
The project follows a simple Spring Boot structure with Kotlin:

```
src
├── main
│   ├── kotlin
│   │   └── com
│   │       └── idan
│   │           └── pokemon_hub
│   │               ├── controller
│   │               │   └── PokemonController.kt
│   │               │       # REST controller handling HTTP requests for Pokémon CRUD operations.
│   │               │       # Defines endpoints like GET /pokemons, PUT /pokemons/{pokedex}, etc.
│   │               ├── exception
│   │               │   ├── GlobalExceptionHandler.kt
│   │               │   │   # Global exception handler using @ControllerAdvice.
│   │               │   │   # Returns JSON error responses (e.g., { "message": "...", "status": 404 }) for exceptions.
│   │               │   └── PokemonExceptions.kt
│   │               │       # Custom exception classes (PokemonNotFoundException, InvalidFieldException, etc.).
│   │               │       # Used to signal specific error conditions in the service layer.
│   │               ├── init
│   │               │   └── DataInit.kt
│   │               │       # Initialization logic for loading initial Pokémon data.
│   │               │       # Likely reads from Pokedex.json and populates the database on startup.
│   │               ├── model
│   │               │   ├── PokemonImage.kt
│   │               │   │   # Entity representing Pokémon image URLs.
│   │               │   │   # Linked to Pokemon via a one-to-one relationship.
│   │               │   ├── Pokemon.kt
│   │               │   │   # Core Pokémon entity with fields like pokedex, name, type, and image.
│   │               │   │   # Annotated with JPA annotations for database mapping.
│   │               │   └── PokemonType.kt
│   │               │       # Enum class defining Pokémon types (e.g., GRASS, FIRE).
│   │               │       # Used in Pokemon entity as a set of types.
│   │               ├── PokemonHubApplication.kt
│   │               │   # Main application entry point.
│   │               │   # Contains the @SpringBootApplication annotation to bootstrap the app.
│   │               ├── repository
│   │               │   ├── PokemonImageRepository.kt
│   │               │   │   # JPA repository interface for PokemonImage entity.
│   │               │   │   # Provides CRUD operations for image data.
│   │               │   └── PokemonRepository.kt
│   │               │       # JPA repository interface for Pokemon entity.
│   │               │       # Includes custom queries like findByPokedex.
│   │               └── service
│   │                   └── PokemonService.kt
│   │                       # Business logic layer for Pokémon operations.
│   │                       # Handles validation, interacts with repositories, and throws exceptions.
│   └── resources
│       ├── application.yml
│       │   # Configuration file for Spring Boot.
│       │   # Defines database settings, server port, etc.
│       └── Pokedex.json
│           # JSON file containing initial Pokémon data.
│           # Used by DataInit.kt to seed the database.
└── test
    └── kotlin
        └── com
            └── idan
                └── pokemon_hub
                    ├── controller
                    │   └── PokemonControllerTest.kt
                    │       # Integration tests for PokemonController using MockMvc.
                    │       # Tests endpoints with mocked PokemonService responses.
                    └── service
                        └── PokemonServiceTest.kt
                            # Unit tests for PokemonService.
                            # Uses Mockito to mock PokemonRepository and test service logic.
```

### 6. Future Roadmap
- Implement a respected database (e.g. MySQL) to store Pokémon data instead of in-memory H2.
V Porting Types into ENUMs for better type safety.
V Normalize the database so URLs will be stored in a separate table, as an industry standard.
- Add authentication/authorization for the API (e.g., using JWT).
V Enhance the error handling (e.g., invalid Pokémon pokedex).
- Expand the Pokémon list to include all generations.

### 7. License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## About the Project

This project was created to explore building APIs with Kotlin and Spring Boot. It covers basic API development, working with controllers, services, and models, as well as writing unit tests with JUnit and AssertJ.

---
