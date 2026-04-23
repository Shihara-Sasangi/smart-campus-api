# Smart Campus REST API

> **Student Name** – S.M.N. Shihara Sasangi
>
> **UoW Number** – w2120008
>
> **IIT Number** – 20232354

---

## Project Overview

This project is a RESTful API for a Smart Campus Sensor and Room Management system built using Java and JAX-RS (Jersey) with an embedded Grizzly server. It strictly adheres to coursework requirements by using in-memory data structures (HashMaps and ArrayLists) and avoids external frameworks like Spring Boot or SQL databases.

---

## API Overview

The Smart Campus API is designed to manage physical campus spaces and the IoT devices within them. It supports three main resources:

- **Rooms** — Represents physical locations (e.g., lecture halls, labs) with a specific capacity.
- **Sensors** — Represents devices (e.g., Temperature, CO2, Motion sensors) that are deployed within specific rooms.
- **Sensor Readings** — Represents the data points collected by sensors over time.

The API follows RESTful principles, supports deep nesting for related resources (e.g., `/api/v1/sensors/{id}/readings`), and provides robust error handling with appropriate HTTP status codes such as 404, 415, and 422.

---

## Build and Run Instructions

Follow these steps to compile and run the API server locally.

### Prerequisites

- Java 17 or higher installed
- Apache Maven installed and added to system PATH

### Steps

1. **Clone the repository** — Open your terminal and clone this project.

2. **Navigate to the project directory:**

```bash
cd smart-campus-api
```

3. **Build the project** — Compile the code and download all dependencies:

```bash
mvn clean compile
```

4. **Start the server** — Launch the embedded Grizzly server:

```bash
mvn exec:java
```

5. **Access the API** — The server will start and listen for requests at:

```
http://localhost:9090/api/v1
```

---

## Sample curl Commands

Here are five sample interactions demonstrating how to use the API.

### 1. Create a New Room

```bash
curl -X POST http://localhost:9090/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"name": "Computing Lab 1", "capacity": 50}'
```

### 2. Retrieve All Rooms

```bash
curl -X GET http://localhost:9090/api/v1/rooms
```

### 3. Create a New Sensor in a Room

```bash
curl -X POST http://localhost:9090/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"name": "Lab Temp Sensor", "type": "TEMPERATURE", "roomId": 1}'
```

### 4. Retrieve Sensors Filtered by Type

```bash
curl -X GET "http://localhost:9090/api/v1/sensors?type=TEMPERATURE"
```

### 5. Add a Reading to a Specific Sensor

```bash
curl -X POST http://localhost:9090/api/v1/sensors/1/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 24.5}'
```
