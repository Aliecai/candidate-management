
# Candidate Management System

A RESTful Candidate Management application built using **Java 17** and **Spring Boot 4.1.2**. The application allows users to create, retrieve, update, delete, and search candidate information through REST APIs.

The project demonstrates a layered architecture, input validation, exception handling, dynamic searching, pagination, sorting, and unit and integration testing.

## Tech Stack

- Java 17
- Spring Boot 4.1.2
- Spring Web
- Spring Data JPA
- Hibernate
- H2 Database
- MySQL (optional)
- Jakarta Bean Validation
- Maven
- JUnit 5
- Mockito
- MockMvc
- SLF4J Logging

## Features

- Create candidate records.
- Retrieve candidate details by ID.
- Update existing candidate information.
- Delete candidate records.
- Search candidates using optional filters.
- Support pagination and sorting.
- Validate incoming request data.
- Prevent duplicate email addresses.
- Handle exceptions centrally.
- Log important application operations.

## Project Structure

```text
src
 ├── main
 │   ├── java/com/candidate/management
 │   │   ├── controller
 │   │   │   └── CandidateController.java
 │   │   ├── dto
 │   │   │   ├── CandidateRequest.java
 │   │   │   └── CandidateResponse.java
 │   │   ├── exception
 │   │   │   ├── CandidateNotFoundException.java
 │   │   │   ├── DuplicateEmailException.java
 │   │   │   └── GlobalExceptionHandler.java
 │   │   ├── model
 │   │   │   └── Candidate.java
 │   │   ├── repository
 │   │   │   └── CandidateRepository.java
 │   │   ├── service
 │   │   │   └── CandidateService.java
 │   │   └── CandidateManagementApplication.java
 │   └── resources
 │       └── application.properties
 └── test
     └── java/com/candidate/management
         ├── CandidateControllerIntegrationTest.java
         └── service
             └── CandidateServiceTest.java
```

## API Endpoints

Base URL:

```text
http://localhost:8080/api/candidates
```

| Method | Endpoint | Description |
|---|---|---|
| POST | `/create` | Create a candidate |
| GET | `/{id}` | Retrieve candidate by ID |
| GET | `/search` | Search candidates |
| PUT | `/update/{id}` | Update candidate |
| DELETE | `/delete/{id}` | Delete candidate |

## API Examples

### 1. Create Candidate

**POST** `/api/candidates/create`

Request body:

```json
{
  "name": "Rahul",
  "email": "rahul@gmail.com",
  "phone": "9876543210",
  "position": "Java Backend Developer",
  "skills": "Java, Spring Boot, Microservices",
  "experienceYears": 4,
  "location": "Bangalore"
}
```

### 2. Get Candidate by ID

**GET** `/api/candidates/1`

### 3. Search Candidates

**GET** `/api/candidates/search`

Supported query parameters:

| Parameter | Description |
|---|---|
| keyword | Searches name, email, skills, and position |
| position | Filters by position |
| location | Filters by location |
| minExperience | Minimum experience |
| maxExperience | Maximum experience |
| page | Page number (starts from 0) |
| size | Number of records per page |
| sortBy | Field used for sorting |
| direction | Sort direction (`asc` or `desc`) |

Example:

```text
GET /api/candidates/search?keyword=java&location=Bangalore&minExperience=2&maxExperience=5&page=0&size=10&sortBy=name&direction=asc
```

### Sample Search Response

The API returns a customized pagination response using `CandidateResponse`.

```json
{
  "content": [
    {
      "id": 1,
      "name": "Rahul",
      "email": "rahul@gmail.com",
      "phone": "9876543210",
      "position": "Java Backend Developer",
      "skills": "Java, Spring Boot, Microservices",
      "experienceYears": 4,
      "location": "Bangalore"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

The response contains only the required pagination fields instead of exposing the complete Spring Data `Page` structure.

### 4. Update Candidate

**PUT** `/api/candidates/update/1`

Request body follows the same structure as the create API.

### 5. Delete Candidate

**DELETE** `/api/candidates/delete/1`

Returns HTTP `204 No Content` when successful.

## Architecture

The application follows a layered architecture:

- **Controller:** Handles HTTP requests and responses.
- **Service:** Contains business logic and application operations.
- **Repository:** Handles database interactions using Spring Data JPA.
- **Entity:** Represents the candidate database table.
- **DTO:** Defines request and response data structures.
- **Exception:** Handles application-specific exceptions and global error responses.

## Dynamic Search

The search API uses Spring Data JPA `Specification` and the JPA Criteria API.

This approach allows optional search filters to be added dynamically without maintaining multiple repository methods or writing separate JPQL queries for every filter combination.

The implementation supports:

- Case-insensitive keyword search.
- Partial matching using SQL `LIKE`.
- Multiple optional filters.
- Minimum and maximum experience filtering.
- Pagination and sorting.

## Pagination and Sorting

The application uses Spring Data JPA pagination:

- `PageRequest.of()` creates the pagination and sorting request.
- `Pageable` passes pagination information to the repository.
- `Page<Candidate>` holds the query results and pagination metadata internally.
- `CandidateResponse` maps the results into a customized API response containing:
  - `content`
  - `totalElements`
  - `totalPages`
  - `size`
  - `number`

This keeps the API response clean and avoids exposing unnecessary pagination properties.

## Exception Handling

The application uses a centralized `GlobalExceptionHandler` to handle:

- Candidate not found.
- Duplicate email address.
- Invalid request data.
- Validation errors.
- Other application exceptions.

Appropriate HTTP status codes are returned for different error scenarios.

## Logging

SLF4J logging is used to track important application operations, including:

- Candidate creation.
- Candidate retrieval.
- Candidate updates.
- Candidate deletion.
- Search operations.
- Business validation failures.

## Database Configuration

The application uses an H2 in-memory database by default, making it easy to run without installing an external database.

MySQL can also be configured by updating `application.properties`.

## Running the Application

### Prerequisites

- Java 17 or later
- Maven

### Run using Maven

```bash
mvn clean install
mvn spring-boot:run
```

The application starts at:

```text
http://localhost:8080
```

## Running Tests

Execute the following command:

```bash
mvn test
```
