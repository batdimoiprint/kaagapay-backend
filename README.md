# Kaagapay Backend: Technical Guide and Local Setup

This guide provides a technical overview of the Kaagapay Backend tech stack and instructions to run the application on your local machine.

---

## Tech Stack and Libraries

The Kaagapay backend is built with the following core technologies:

### Core Frameworks
*   Language: Java 21
*   Framework: Spring Boot 3.2.5
*   Database: PostgreSQL
*   Security: Spring Security with JWT (JSON Web Tokens)
*   API Documentation: SpringDoc OpenAPI (Swagger UI)

### Key Dependencies
*   Spring Data JPA: Handles database interactions and ORM.
*   io.jsonwebtoken (JJWT) 0.12.5: Manages secure authentication and token generation.
*   Cloudinary (cloudinary-http5): Manages media uploads and storage.
*   Pushy: Handles push notification services.
*   Spring Boot DevTools: Provides fast application restarts during development.

---

## Local Setup Instructions

Follow these steps to run the backend.

### 1. Prerequisites
Ensure you have the following installed:
*   Java 21 JDK
*   Maven (or use the included ./mvnw wrapper)

### 2. Build the Application
Open a terminal in the kaagapay-backend/ directory and run:

```bash
./mvnw clean install
```

### 3. Run the Backend
Start the server using the Spring Boot Maven plugin:

```bash
./mvnw spring-boot:run
```

The backend will be accessible at http://localhost:8081.

---

## API Documentation
You can view the interactive API documentation and test endpoints via Swagger UI:

URL: http://localhost:8081/swagger-ui.html

---

## Complaint Severity Scoring

New complaints receive a `severityScore` and `severityLabel` before they are saved. The mobile app can send these optional scoring inputs with the complaint form:

*   `incidentType`
*   `hasInjury`
*   `needsImmediateResponse`
*   `hasPropertyDamage`
*   `affectedPeopleCount`

Severity points are calculated using these rules:

| Factor | Points |
|---|---:|
| Life-threatening emergency keywords | +50 |
| Injury reported | +35 |
| Fire / violence / medical emergency keywords | +30 |
| Needs immediate response | +25 |
| Multiple people affected (`affectedPeopleCount > 1`) | +20 |
| Property damage | +15 |
| Public safety hazard keywords | +10 |
| Has photo/video evidence | +5 |
| Older unresolved report by incident date: 1+ days / 3+ days / 7+ days | +5 / +10 / +15 |

Severity labels are assigned by score range:

| Score Range | Label |
|---:|---|
| 0 - 24 | LOW |
| 25 - 49 | MODERATE |
| 50 - 79 | HIGH |
| 80+ | CRITICAL |

`GET /complaints` returns all reports sorted by highest `severityScore` first. The prioritization is handled in `ComplaintPriorityService` with an explicit brute-force O(n^2) nested-loop comparison.

---

## Project Structure
*   src/main/java/backend/controller: API Request handlers.
*   src/main/java/backend/service: Business logic and external service integration.
*   src/main/java/backend/repository: Database access layer.
*   src/main/java/backend/security: Authentication and authorization logic.
*   src/main/resources/application.properties: Centralized application settings.

---

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
