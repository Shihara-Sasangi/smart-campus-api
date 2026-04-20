# Smart Campus REST API

This project is a RESTful API for a "Smart Campus" Sensor & Room Management system built using Java and JAX-RS (Jersey) with an embedded Grizzly server. It strictly adheres to coursework requirements by using in-memory data structures (HashMaps and ArrayLists) and avoids external frameworks like Spring Boot or SQL databases.

## Setup & Running the Server

### Prerequisites
- **Java Development Kit (JDK) 17** or higher.
- **Apache Maven** installed and added to your system PATH.

### Build and Run
1. Open your terminal or command prompt in the root of this project directory (where this `pom.xml` is located).
2. Clean and compile the project using Maven:
   ```bash
   mvn clean package
   ```
3. Run the server using the Maven Exec Plugin:
   ```bash
   mvn exec:java
   ```
4. The server will start and be available at: `http://localhost:8080/api/v1`

## Sample API Interactions (cURL commands)

Below are 5 sample `curl` commands demonstrating successful interactions with different parts of the API.

**1. Discovery Endpoint (GET Metadata)**
```bash
curl -X GET http://localhost:8080/api/v1
```

**2. Create a Room (POST)**
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"id": "LEC-01", "name": "Main Lecture Hall", "capacity": 200}'
```

**3. Fetch All Rooms (GET)**
```bash
curl -X GET http://localhost:8080/api/v1/rooms
```

**4. Register a Sensor in a Room (POST)**
```bash
curl -X POST http://localhost:8080/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"id": "CO2-01", "type": "CO2", "status": "ACTIVE", "currentValue": 400.0, "roomId": "LEC-01"}'
```

**5. Post a Sensor Reading (POST)**
```bash
curl -X POST http://localhost:8080/api/v1/sensors/CO2-01/readings \
-H "Content-Type: application/json" \
-d '{"value": 415.5}'
```

---

## Conceptual Report

### Part 1: Service Architecture & Setup
**Q: Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.**
By default, JAX-RS Resource classes are request-scoped, meaning a new instance of the resource class is instantiated for every single incoming HTTP request. Because multiple requests can occur simultaneously, multiple resource instances will try to access and modify our backend data concurrently. To prevent race conditions and data corruption, our in-memory data structures (like Maps and Lists) must be thread-safe. We achieve this by using thread-safe collections like `ConcurrentHashMap` or by synchronizing blocks of code when modifying shared `ArrayLists` in our singleton data repository.

**Q: Why is the provision of "Hypermedia" (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?**
Hypermedia as the Engine of Application State (HATEOAS) allows clients to dynamically discover available actions and resources directly from the API responses, rather than hardcoding URLs. This benefits client developers because the API becomes self-documenting and resilient to backend URL changes. If the server changes a route, the client automatically adapts because it follows the provided URLs in the JSON response, significantly reducing maintenance overhead compared to relying on static documentation.

### Part 2: Room Management
**Q: When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.**
Returning only IDs minimizes network bandwidth and payload size, making the API faster, but forces the client to make multiple subsequent API calls (N+1 problem) to fetch the details of each room, increasing client-side processing and overall latency. Returning full room objects increases the payload size and bandwidth usage per request but allows the client to render the UI immediately with a single HTTP call, reducing processing complexity and total network round-trips.

**Q: Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.**
Yes, the DELETE operation is idempotent. If a client mistakenly sends the exact same DELETE request for a room multiple times, the first request will successfully remove the room and return a `204 No Content` (or `200 OK`). Subsequent identical requests will search for the already-deleted room, fail to find it, and gracefully return a `404 Not Found`. In all cases, the server's state remains exactly the same after the first request—the room is absent—which fulfills the definition of idempotency.

### Part 3: Sensor Operations & Linking
**Q: We explicitly use the @Consumes(MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?**
If a client sends data in a format other than JSON (e.g., `text/plain` or `application/xml`), JAX-RS intercepts the request before it reaches the resource method and checks the `Content-Type` header. Since the incoming format does not match the `@Consumes` annotation, JAX-RS automatically rejects the request and returns an HTTP `415 Unsupported Media Type` error. This ensures the method only processes valid, parseable JSON payloads, preventing parsing crashes.

**Q: You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/v1/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?**
Path parameters are best used to identify a specific, unique resource (like an ID), whereas query parameters are best for filtering, sorting, or modifying the view of a collection. Using `@QueryParam` is superior for filtering because it makes the filter optional, easily combinable (e.g., `?type=CO2&status=ACTIVE`), and respects the hierarchical structure of REST (`/sensors` is the collection). Embedding filters in the path makes URLs rigid and difficult to scale when multiple optional filters are introduced.

### Part 4: Deep Nesting with Sub - Resources
**Q: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?**
The Sub-Resource Locator pattern delegates the handling of nested endpoints to a separate class (e.g., returning `SensorReadingResource` from `SensorResource`). This adheres to the Single Responsibility Principle, modularizing the codebase and keeping resource classes small and focused. In large APIs, defining all nested paths in one massive controller creates a bottleneck that is difficult to read, maintain, and test. Delegation ensures that reading-specific logic lives entirely within its own context, making the API much easier to extend.

### Part 5: Advanced Error Handling, Exception Mapping & Logging
**Q: Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?**
HTTP `404 Not Found` implies that the target URL endpoint itself does not exist. However, when POSTing a valid JSON payload to a valid URL, but a foreign key inside the payload (like `roomId`) is invalid, the URL is correct and the syntax is correct, but the *contents* (semantics) are unprocessable. HTTP `422 Unprocessable Entity` accurately conveys that the server understands the content type and syntax, but cannot process the contained instructions due to semantic errors (the missing dependency).

**Q: From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?**
Exposing internal stack traces leads to Information Disclosure. Attackers can read the trace to discover the underlying technologies used (e.g., Jersey, Grizzly), specific versions of third-party libraries (which might have known vulnerabilities), internal package and class names, database connection logic, and the exact line of code where the error occurred. This deep architectural knowledge provides attackers with a roadmap to craft highly targeted exploits against the system.

**Q: Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?**
Using JAX-RS filters centralizes the logging logic in one place, preventing code duplication across the application. If you manually insert `Logger.info()` into every method, it tightly couples the business logic with logging mechanics, increasing the likelihood of human error (e.g., forgetting to log a method) and making it harder to maintain or modify the logging format later. Filters automatically intercept all traffic, ensuring 100% consistent, decoupled observability.
