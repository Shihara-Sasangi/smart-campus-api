# Smart Campus REST API

S.M.N.Shihara Sasangi
w2120008 / 20232354
This project is a RESTful API for a "Smart Campus" Sensor & Room Management system built using Java and JAX-RS (Jersey) with an embedded Grizzly server. It strictly adheres to coursework requirements by using in-memory data structures (HashMaps and ArrayLists) and avoids external frameworks like Spring Boot or SQL databases.

## API Overview
The Smart Campus API is designed to manage physical campus spaces and the IoT devices within them. It supports three main resources:
* **Rooms:** Represents physical locations (e.g., lecture halls, labs) with a specific capacity.
* **Sensors:** Represents devices (e.g., Temperature, CO2, Motion sensors) that are deployed within specific rooms.
* **Sensor Readings:** Represents the data points collected by sensors over time. 
The API uses RESTful principles, deep nesting for related resources (e.g., `/sensors/{id}/readings`), and provides robust error handling with appropriate HTTP status codes (like 404, 422, and 415).

## Build and Launch Instructions
Follow these steps to compile and run the API server locally:
1. **Prerequisites:** Ensure you have Java 17 and Maven installed on your system.
2. **Clone the repository:** Open your terminal and clone this project.
3. **Navigate to the directory:** `cd smart-campus-api`
4. **Build the project:** Run the following command to compile the code and download dependencies:
   ```bash
   mvn clean compile
   ```
5. **Start the server:** Launch the embedded Grizzly server by running:
   ```bash
   mvn exec:java
   ```
6. **Access the API:** The server will start and listen for requests at `http://localhost:9090/api/v1`.

## Sample cURL Commands
Here are five sample interactions demonstrating how to use the API:

**1. Create a new Room:**
```bash
curl -X POST http://localhost:9090/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{"name": "Computing Lab 1", "capacity": 50}'
```

**2. Retrieve all Rooms:**
```bash
curl -X GET http://localhost:9090/api/v1/rooms
```

**3. Create a new Sensor in the Room:**
```bash
curl -X POST http://localhost:9090/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{"name": "Lab Temp Sensor", "type": "TEMPERATURE", "roomId": 1}'
```

**4. Retrieve Sensors (filtered by type):**
```bash
curl -X GET "http://localhost:9090/api/v1/sensors?type=TEMPERATURE"
```

**5. Add a Reading to a specific Sensor:**
```bash
curl -X POST http://localhost:9090/api/v1/sensors/1/readings \
-H "Content-Type: application/json" \
-d '{"value": 24.5}'
```


