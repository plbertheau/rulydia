# Random User Lydia : rulydia

Test project for Lydia :

- Fetches the list of user data in pages from a public random user API
- Caches the fetched data in local database using Room
- Uses the latest Paging library components to handle pagination

The modules are as follow:
It uses clean architecture (app → domain ← data)

* app: Presentation Layer
* domain: Business Logic Layer
* data: Data Access Layer

## Tech Stack

* Multi-Module-Architecture
* Solid Principles
* Kotlin

## Libraries
* Jetpack Compose for ui
* Material Design
* Hilt for dependency injection
* Coroutines for async
* Flow for data stream
* Paging 3 for pagination
* Jetpack Navigation
* Retrofit for network
* Coil for image loading
