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

New complaints receive a server-generated `severityScore` and `severityLabel` before they are saved. The client should send the complaint details only; the backend computes the severity from `complaintType` and the incident flags below:

*   `complaintType`
*   `hasInjury`
*   `needsImmediateResponse`
*   `hasPropertyDamage`
*   `affectedPeopleCount`

Severity points are calculated using these rules:

| Factor | Points |
|---|---:|
| `Fire Hazard / Open Burning`, `Electrical Hazard`, `Animal Bite / Attack`, `Missing Person (local report)`, `Harassment / Threats` | +50 |
| Injury reported | +35 |
| `Domestic Conflict`, `Theft / Petty Crime`, `Suspicious Activity`, `Public Health Concern`, `Infrastructure Damage`, `Tree Obstruction / Fallen Tree` | +30 |
| Needs immediate response | +25 |
| Multiple people affected (`affectedPeopleCount > 1`) | +20 |
| Property damage | +15 |
| `Vandalism / Property Damage`, `Trespassing`, `Water Leakage / Pipe Issue`, `Drainage / Flooding`, `Clogged Canal / Sewer`, `Road Damage / Potholes`, `Broken Streetlight`, `Illegal Construction`, `Building Code Violation`, `Pollution (air water noise)`, `Abandoned Vehicle`, `Illegal Vendor / Sidewalk Obstruction` | +15 |
| `Neighborhood Dispute`, `Noise Complaint`, `Public Disturbance`, `Illegal Parking / Obstruction`, `Waste / Garbage Issue`, `Sanitation Problem`, `Animal Concern`, `Stray Animals`, `Noise from Business`, `Curfew Violation`, `Ordinance Violation`, `Parking Dispute`, `Lost and Found`, `Other` | +5 |
| Has photo/video evidence | +5 |
| Older unresolved report by incident date: 1+ days / 3+ days / 7+ days | +5 / +10 / +15 |

Severity labels are assigned by score range:

| Score Range | Label |
|---:|---|
| 0 - 24 | LOW |
| 25 - 49 | MODERATE |
| 50 - 79 | HIGH |
| 80+ | CRITICAL |

`GET /complaints` returns all reports sorted by highest `severityScore` first. The prioritization is handled in `ComplaintPriorityService` with an explicit brute-force O(n^2) nested-loop comparison. You can also request `GET /complaints?sort=CREATED` to sort by `dateOfIncident` instead.

### Severity behavior notes

* The backend does not expect the client to send `severityScore` or `severityLabel`.
* `complaintType` should be one of the supported complaint categories listed above.
* If a type is not recognized exactly, it falls back to no type-based points, but the boolean flags and media/date rules still apply.

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
