# Tickrackr

## About

Tickrackr is a Java Spring Boot project I developed for the Elanco Software Engineering Placement technical task.

The aim of the project is to retrieve tick sighting data from the Elanco API, process it, store it in a database and allow users to search and view reports based on the data.

## What the application does

### Getting data from the API
- Connects to the Elanco API using Spring `RestClient`.
- Retrieves tick sighting records when the application starts.
- Retries requests if there are temporary server or connection problems, using an increasing delay between attempts.

### Processing the data
Before saving records, the application checks that the data is valid.

It checks for:
- Missing IDs.
- Missing or invalid dates.
- Missing locations.
- Missing species names.
- Missing Latin names.
- Duplicate IDs.

Valid records are saved to the sightings table. Records that cannot be processed are saved separately in a rejected records table, along with the reason they were rejected.

### Searching for sightings

The application allows sightings to be searched using:
- Location.
- A start and end date.
- A combination of location and date range.

For example, you can search for all sightings in Manchester or sightings recorded in Manchester during a particular date range.

### Reports

There are three reporting endpoints:

- **Sightings by location:** Shows how many sightings were recorded at each location.
- **Weekly trends:** Shows the number of sightings recorded each week, grouped by ISO week.
- **Monthly trends:** Shows the number of sightings recorded each month.

The counts are calculated using database queries rather than loading every record into Java and counting them individually.

## Technologies used

- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- H2 Database
- Maven
- JUnit 5
- Mockito

## Project structure

```text
src/
├── main/
│   ├── java/com/cemal/tickrackr/
│   │   ├── client/       - Connects to the Elanco API
│   │   ├── controller/   - Handles API requests
│   │   ├── exception/    - Handles application errors
│   │   ├── model/        - Database entities
│   │   ├── repository/   - Database queries
│   │   └── service/      - Main application logic
│   └── resources/
│       └── application.properties
└── test/
    └── java/             - Automated tests
```

## How to run the project

You need Java 17 or later and an internet connection to access the Elanco API.

First, clone the repository:

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
```

Move into the project directory:

```bash
cd tickrackr
```

Run the application:

```bash
./mvnw spring-boot:run
```

When the application starts, it retrieves the data from Elanco and processes the records.

The application uses an H2 file-based database, so data is stored locally in the `data` directory.

## API endpoints

All endpoints run on `http://localhost:8080`.

### Search sightings

`GET /api/sightings`

You can use the following query parameters:

| Parameter | Description |
|---|---|
| `location` | Search by location |
| `startDate` | Start of the date range |
| `endDate` | End of the date range |

Example:

```text
/api/sightings?location=Manchester
```

Search using a date range:

```text
/api/sightings?startDate=2022-08-01T00:00:00&endDate=2022-08-01T23:59:59
```

Search using both:

```text
/api/sightings?location=Manchester&startDate=2022-08-01T00:00:00&endDate=2022-08-01T23:59:59
```

Both date parameters must be provided when using date filtering.

### Reports

| Endpoint | Description |
|---|---|
| `/api/reports/sightings-by-location` | Counts sightings for each location |
| `/api/reports/weekly-trends` | Counts sightings per week |
| `/api/reports/monthly-trends` | Counts sightings per month |

## Running the tests

To run the tests, use:

```bash
./mvnw clean test
```

The tests currently cover:
- Loading the Spring Boot application context.
- Saving valid records.
- Rejecting records with missing IDs.
- Rejecting records with invalid dates.
- Detecting duplicates already in the database.
- Detecting duplicate IDs within the same batch.

## Current implementation

Data is retrieved and processed when the application starts. Valid sightings and rejected records are stored separately in the database.

The reporting endpoints use the locations provided by the Elanco API. No separate region field is available in the data being processed.
