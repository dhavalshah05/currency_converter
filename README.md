# Currency Converter

➡️ It is an Android application which fetch the currencies from [OpenExchangeRates][OPEN_EXCHANGE] and let us convert it as per our needs.

## Tech Stack
1. MVVM Architecture
2. UseCase pattern
3. Flow
4. Unit Testing with [Kotest][KOTEST] and [Mockk][MOCKK]
5. Jetpack Compose
6. [Ktor][KTOR]
7. [SQLDelight][SQL_DELIGHT]
8. WorkManager

## How to run all Unit Tests

➡️ Right click on tests folder and run all unit tests.


[MOCKK]:https://mockk.io
[KOTEST]:https://kotest.io
[SQL_DELIGHT]:https://cashapp.github.io/sqldelight
[KTOR]: https://ktor.io
[OPEN_EXCHANGE]:https://openexchangerates.org

## How to setup project

To run the project, you need to create `credentials.properties` file with valid details.
Perform following actions
1. Create `credentials.properties` file from `credential.properties.sample`
2. Provide valid details for each entry in that file
3. Build and Run the project