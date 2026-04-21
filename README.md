# Smart Campus REST API

S.M.N.Shihara Sasangi
w2120008 / 20232354
This project is a RESTful API for a "Smart Campus" Sensor & Room Management system built using Java and JAX-RS (Jersey) with an embedded Grizzly server. It strictly adheres to coursework requirements by using in-memory data structures (HashMaps and ArrayLists) and avoids external frameworks like Spring Boot or SQL databases.


## Conceptual Report

### Part 1: Service Architecture & Setup
**Q: Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.**
From what I learned, JAX-RS creates a brand new instance of the Resource class every time a client makes an HTTP request. This is called being 'request-scoped'. Because of this, if multiple users hit the API at the exact same time, multiple instances of my classes will try to change the backend data all at once. To stop them from overriding each other and causing data corruption (race conditions), I had to make sure my in-memory data storage was thread-safe. I did this by wrapping modifications to my ArrayLists inside `synchronized` blocks inside my Database singleton class.

**Q: Why is the provision of "Hypermedia" (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?**
HATEOAS basically means including links inside the JSON responses so that the client knows what other actions they can take next, without having to guess or read a manual. I think this is a huge benefit for developers because it makes the API self-documenting. If I ever decide to change the URL structure on the backend, the frontend won't break because it just follows the links I provide in the response, instead of having hardcoded URLs.

### Part 2: Room Management
**Q: When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.**
If I only return IDs, the JSON payload is very small and fast to send. But the downside is the client has to make a bunch of extra API calls to actually get the details for each room (which is called the N+1 problem). On the other hand, returning the full room objects uses more bandwidth per request, but I think it's better because the client gets everything they need to build the UI in just one single HTTP call, saving time and extra network requests.

**Q: Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.**
Yes, my DELETE operation is idempotent. If a client accidentally sends the same DELETE request twice for the same room, the first request will delete it and return a success code. The second request will look for the room, see it's already gone, and just return a 404 Not Found error. In both cases, the final result on the server is exactly the same: the room doesn't exist anymore. That's what makes it idempotent.

### Part 3: Sensor Operations & Linking
**Q: We explicitly use the @Consumes(MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?**
Because I used `@Consumes(MediaType.APPLICATION_JSON)`, JAX-RS checks the `Content-Type` header of every incoming request before my code even runs. If a client tries to send something like plain text or XML, JAX-RS will immediately block the request and send back an HTTP `415 Unsupported Media Type` error. This is really helpful because it protects my methods from crashing trying to parse bad data.

**Q: You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/v1/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?**
From my understanding, path parameters are meant to point to one specific resource (like an ID), while query parameters are meant for filtering a list. Using `@QueryParam` is better for searching because it makes the filter optional. It also lets you easily combine multiple filters later on (like `?type=CO2&status=ACTIVE`). If I hardcoded 'type' into the path itself, the URLs would become really rigid and messy to manage if I wanted to add more filters in the future.

### Part 4: Deep Nesting with Sub - Resources
**Q: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?**
The Sub-Resource Locator pattern basically lets me hand off the work for nested URLs to a completely separate class. So instead of putting all the sensor reading logic inside my main `SensorResource` class, I pass it to a new `SensorReadingResource` class. This is great for keeping my code organized and following the Single Responsibility Principle. If I put everything in one massive class, it would be a nightmare to read and test, but this way, each class handles its own specific job.

### Part 5: Advanced Error Handling, Exception Mapping & Logging
**Q: Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?**
A `404 Not Found` usually means the actual URL you typed in doesn't exist. But in this case, the client is POSTing to the correct URL, and the JSON syntax is perfectly fine. The issue is that the data inside the JSON (like a bad `roomId`) doesn't make logical sense to the system. That's why returning a `422 Unprocessable Entity` is much more accurate—it tells the client 'I understood your request and the format was right, but the actual data values are invalid'.

**Q: From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?**
If I let the API return a full Java stack trace when it crashes, it's a huge security risk called Information Disclosure. A hacker could read the trace and see exactly what server I'm using, what library versions are installed, my internal package names, and even the exact line of code that broke. Giving away all those internal technical details makes it way too easy for someone to figure out how my system works and find targeted ways to attack it.

**Q: Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?**
Using JAX-RS filters lets me write my logging code just once in a central place. If I had to type `Logger.info()` inside every single method I wrote, I would definitely forget to do it sometimes, and it would clutter up my actual business logic. The filter acts like a checkpoint that automatically intercepts every request and response, guaranteeing that everything gets logged consistently without me having to manually remember it.
