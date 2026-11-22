# Candidate Management System

A RESTful Candidate Management application built using **Java 17 and Spring Boot 4.1.2**. The application provides APIs to create, retrieve, update, delete, search, and filter candidate records.

## Technology Stack

- **Java:** 17
- **Spring Boot:** 4.1.2
- **Build Tool:** Maven
- **Spring Web:** REST API development
- **Spring Data JPA:** Database operations
- **Hibernate:** ORM
- **Database:** H2 (default), MySQL (optional)
- **Validation:** Jakarta Bean Validation
- **Testing:** JUnit 5, Mockito, Spring Boot Test, MockMvc
- **Logging:** SLF4J with Logback

## Project Features

- Create a new candidate.
- Retrieve all candidates.
- Retrieve a candidate by ID.
- Update candidate details.
- Delete a candidate.
- Search candidates using keywords.
- Filter candidates by position, location, and experience.
- Pagination and sorting.
- Request validation.
- Centralized exception handling.
- Application logging.
- Persistent database storage.

## Prerequisites

Make sure the following software is installed:

- Java JDK 17 or later
- Apache Maven 3.6 or later
- IDE such as IntelliJ IDEA, Eclipse, or Spring Tool Suite
- Postman or any REST API client

## How to Run the Application

### 1. Clone the repository

```bash
git clone <repository-url>
```

### 2. Navigate to the project directory

```bash
cd candidate-management
```

### 3. Build the application

```bash
mvn clean install
```

### 4. Run the application

```bash
mvn spring-boot:run
```

Alternatively, run the generated JAR:

```bash
java -jar target/candidate-management-1.0.0.jar
```

The application starts at:

```text
http://localhost:8080
```

## Database Configuration

### H2 Database (Default)

The application uses an H2 file-based database by default.

```properties
spring.datasource.url=jdbc:h2:file:./data/candidates;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.format_sql=false

spring.h2.console.enabled=false
```

Candidate records are stored locally in the `data` directory.

### MySQL Configuration (Optional)

To use MySQL, create the database:

```sql
CREATE DATABASE candidate_management;
```

Update the datasource configuration in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/candidate_management
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

Ensure the MySQL connector dependency is present in `pom.xml`.

## API Endpoints

Base URL:

```text
http://localhost:8080/api/candidates
```

All request and response payloads use JSON.

### 1. Create Candidate

**POST** `/api/candidates`

Creates a new candidate.

Request body:

```json
{
  "name": "Alex Johnson",
  "email": "alex.johnson@example.com",
  "phone": "9876543210",
  "position": "Java Developer",
  "skills": "Java, Spring Boot, Hibernate, SQL",
  "experienceYears": 4,
  "location": "Bangalore"
}
```

### 2. Get All Candidates

**GET** `/api/candidates`

Retrieves candidates with pagination and sorting.

Example:

```text
GET /api/candidates?page=0&size=10&sortBy=name&direction=asc
```

### 3. Get Candidate By ID

**GET** `/api/candidates/{id}`

Example:

```text
GET /api/candidates/1
```

### 4. Update Candidate

**PUT** `/api/candidates/{id}`

Updates an existing candidate.

Request body:

```json
{
  "name": "Alex Johnson",
  "email": "alex.johnson@example.com",
  "phone": "9876543211",
  "position": "Senior Java Developer",
  "skills": "Java, Spring Boot, Microservices, AWS",
  "experienceYears": 5,
  "location": "Hyderabad"
}
```

### 5. Delete Candidate

**DELETE** `/api/candidates/{id}`

Example:

```text
DELETE /api/candidates/1
```

### 6. Search and Filter Candidates

**GET** `/api/candidates/search`

Supports keyword-based searching and multiple filters.

| Query Parameter | Description | Example |
|---|---|---|
| `keyword` | Searches candidate information using a keyword | `keyword=Java` |
| `position` | Filters by job position | `position=Java Developer` |
| `location` | Filters by candidate location | `location=Bangalore` |
| `minExperience` | Minimum years of experience | `minExperience=2` |
| `maxExperience` | Maximum years of experience | `maxExperience=6` |
| `page` | Page number (starts from 0) | `page=0` |
| `size` | Number of records per page | `size=10` |
| `sortBy` | Field used for sorting | `sortBy=name` |
| `direction` | Sorting direction (`asc` or `desc`) | `direction=asc` |

#### Search Examples

Search by keyword:

```text
GET /api/candidates/search?keyword=Java
```

Filter by position:

```text
GET /api/candidates/search?position=Java Developer
```

Filter by location:

```text
GET /api/candidates/search?location=Bangalore
```

Filter by experience:

```text
GET /api/candidates/search?minExperience=2&maxExperience=6
```

Combine multiple filters:

```text
GET /api/candidates/search?keyword=Java&location=Bangalore&minExperience=2&maxExperience=6
```

Search with pagination and sorting:

```text
GET /api/candidates/search?keyword=Java&page=0&size=5&sortBy=experienceYears&direction=desc
```

Multiple filters can be combined in a single request.

## Pagination and Sorting

Pagination is implemented using Spring Data JPA.

- `PageRequest.of()` creates the pageable request.
- `Pageable` passes pagination and sorting information to the repository.
- `Page<T>` returns the requested records along with pagination metadata.
- `JpaRepository` provides the built-in paginated `findAll()` method.

Example:

```text
GET /api/candidates?page=0&size=5&sortBy=name&direction=asc
```

Page numbering starts from `0`.

The response includes:

- `content`: Candidate records for the current page.
- `totalElements`: Total number of matching records.
- `totalPages`: Total number of pages.
- `size`: Requested page size.
- `number`: Current page number.
- `first`: Indicates whether this is the first page.
- `last`: Indicates whether this is the last page.
- `empty`: Indicates whether the page has records.

## Exception Handling

Centralized exception handling is implemented using Spring's `@RestControllerAdvice`.

The `GlobalExceptionHandler` handles application exceptions and returns structured error responses.

| Exception | HTTP Status | Description |
|---|---|---|
| MethodArgumentNotValidException | 400 Bad Request | Request validation failure |
| CandidateNotFoundException | 404 Not Found | Candidate does not exist |
| DuplicateEmailException | 409 Conflict | Email already exists |
| Exception | 500 Internal Server Error | Unexpected server-side error |

### Validation

Candidate request fields are validated using Jakarta Bean Validation annotations.

Invalid requests are handled centrally, and meaningful validation messages are returned to the client.

### Example Error Scenarios

- Creating a candidate with an invalid email.
- Submitting missing or blank mandatory fields.
- Providing negative experience.
- Retrieving a candidate that does not exist.
- Creating another candidate with an already registered email.

## Logging

Application logging is implemented using SLF4J.

Logging is configured in `application.properties`:

```properties
logging.level.root=INFO
logging.level.com.candidate.management=INFO
```

The application uses appropriate logging levels:

| Log Level | Usage |
|---|---|
| INFO | Candidate creation, successful updates, and deletion operations |
| WARN | Duplicate email attempts and candidate not found scenarios |
| DEBUG | Candidate retrieval and search operations |
| ERROR | Unexpected exceptions and server-side failures |

Logs are kept meaningful and avoid unnecessary output or sensitive candidate information.

## Running Tests

Execute all unit and integration tests using:

```bash
mvn clean test
```

The test suite covers:

- Candidate creation.
- Email normalization.
- Duplicate email handling.
- Candidate not found scenarios.
- Candidate CRUD API operations.
- Search functionality.
- Invalid request validation.

## Project Structure

```text
candidate-management
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.candidate.management
│   │   │       ├── controller
│   │   │       ├── dto
│   │   │       ├── exception
│   │   │       ├── model
│   │   │       ├── repository
│   │   │       ├── service
│   │   │       └── CandidateManagementApplication.java
│   │   │
│   │   └── resources
│   │       └── application.properties
│   │
│   └── test
│       └── java
│           └── com.candidate.management
│               ├── service
│               └── controller
│
├── pom.xml
├── README.md
```

## Architecture

The application follows a layered architecture:

```text
Client (Postman)
      |
      v
CandidateController
      |
      v
CandidateService
      |
      v
CandidateRepository
      |
      v
Database (H2 / MySQL)
```

- **Controller:** Exposes REST endpoints and handles HTTP requests.
- **Service:** Contains business logic, validation-related checks, and candidate operations.
- **Repository:** Handles database interactions using Spring Data JPA.
- **Model:** Represents the candidate database entity.
- **DTO:** Defines and validates incoming candidate request data.
- **Exception:** Contains custom exceptions and centralized exception handling.
