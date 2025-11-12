# MeetHive – Community Collaboration and Event Management Platform

MeetHive is a full-stack web application designed to help communities connect, collaborate, and organize events seamlessly. It combines event management, group-based discussions, and real-time communication into one unified platform. Built with **Java Spring Boot**, **MySQL**, and a **HTML/CSS/JavaScript frontend**, the project emphasizes both performance and simplicity.

---

## Table of Contents
1. [Overview](#overview)
2. [Features](#features)
3. [System Architecture](#system-architecture)
4. [Tech Stack](#tech-stack)
5. [Backend Design](#backend-design)
6. [Frontend Design](#frontend-design)
7. [Database Design](#database-design)
8. [Installation & Setup](#installation--setup)
9. [Project Modules](#project-modules)
10. [Example API Endpoints](#example-api-endpoints)
11. [Conclusion](#conclusion)

---

## Overview

MeetHive addresses a common challenge faced by communities — fragmented communication and disorganized event coordination. Traditional group chats and social media platforms lack a focused system for managing events and structured group discussions, often leading to missed announcements, scheduling conflicts, and scattered information across multiple channels.

**What MeetHive Does:**

MeetHive provides a **centralized hub** that brings together three core functionalities:

1. **Event Management** - Users can create detailed events with titles, descriptions, dates, times, and locations. The platform handles event creation, updates, and RSVP tracking. Organizers can see who's attending, and participants receive clear information about upcoming activities. Events can be filtered, searched, and viewed in multiple formats.

2. **Group-Based Communities** - The platform allows users to form interest-based groups or project teams. Each group functions as a dedicated space where members can share updates, coordinate activities, and maintain focused discussions. Groups can be public or private, and members can belong to multiple groups simultaneously, making it perfect for managing different aspects of community life.

3. **Real-Time Communication** - Built-in WebSocket-powered chat enables instant messaging within groups and between users. Unlike email or traditional forums, conversations happen in real-time, allowing quick coordination and spontaneous collaboration. All messages are persisted to the database, so users can catch up on conversations they missed.

4. **Integrated Calendar View** - All events are automatically displayed in an interactive calendar interface, giving users a visual overview of upcoming activities. This eliminates the need for separate calendar apps and ensures everyone stays on the same page about when things are happening.

**Who It's For:**

The platform is ideal for colleges, organizations, clubs, community groups, sports teams, or any collective that regularly hosts activities or discussions. Whether you're organizing hackathons, study groups, club meetings, volunteer events, or social gatherings, MeetHive streamlines the entire process from planning to execution.

**The Problem It Solves:**

Instead of juggling between WhatsApp for chat, Google Calendar for scheduling, email for announcements, and social media for updates, MeetHive consolidates everything into one intuitive platform. This reduces confusion, minimizes missed events, and creates a single source of truth for community activities.

---

## Features

- **User Authentication** – Secure signup and login with JWT-based token authentication.
- **Event Management** – Create, update, and manage events with details like time, date, and description.
- **Group System** – Users can create or join groups to discuss shared interests or event topics.
- **Real-Time Chat** – Integrated WebSocket-based chat for seamless live conversations.
- **Calendar Integration** – Displays upcoming and past events in an interactive calendar view.
- **Responsive Frontend** – Clean and simple UI using HTML, CSS, and JavaScript.
- **Database Persistence** – All data (users, messages, events, and groups) stored in MySQL for reliability.

---

## System Architecture

MeetHive follows a **client-server architecture** with clear separation between the presentation and business layers.

- **Frontend (Client):** HTML, CSS, and JavaScript interface for user interaction.
- **Backend (Server):** Spring Boot RESTful API handling authentication, event and group management, and chat processing.
- **Database:** MySQL relational database storing structured data.
- **WebSocket Layer:** Enables real-time message delivery for the chat module.

---

## Tech Stack

**Frontend:**  
- HTML  
- CSS  
- JavaScript  

**Backend:**  
- Java Spring Boot  
- Spring Web, Spring Security, Spring Data JPA  
- WebSocket (for real-time chat)

**Database:**  
- MySQL  

**Build Tool:**  
- Maven  

---

## Backend Design

The backend follows a **layered MVC (Model-View-Controller)** structure for modularity and maintainability:

- **Controller Layer:** Handles incoming HTTP requests and defines REST API endpoints (e.g., `EventController`, `ChatController`, `GroupController`).
- **Service Layer:** Contains business logic (e.g., `EventService`, `MessageService`, `UserService`).
- **Repository Layer:** Interacts directly with the MySQL database using Spring Data JPA.
- **Model Layer:** Defines entity and DTO classes that represent database tables and API payloads.

**Example Flow:**
```
User → Controller → Service → Repository → Database → Response to User
```

Security and authentication are managed using **JWT (JSON Web Token)** for secure API access.

---

## Frontend Design

The frontend consists of static pages served by Spring Boot:
- **Login and Signup Pages:** For user authentication.
- **Dashboard:** Displays user info and navigation.
- **Event Page:** Allows event creation and management.
- **Group Page:** Lists joined groups and group creation options.
- **Chat Page:** Real-time messaging via WebSocket.
- **Calendar Page:** Displays upcoming events in a structured calendar format.

Each page interacts with backend REST APIs to fetch or post data asynchronously using JavaScript `fetch()` calls.

---

## Database Design

The MySQL database contains the following key tables:

- **User** – Stores user credentials and basic info.
- **Event** – Contains event metadata (title, description, date, etc.).
- **Group** – Defines discussion groups and their members.
- **Message** – Stores chat messages with sender, receiver, and timestamp.
- **GroupMember** – Manages relationships between users and groups.
- **EventResponse** – Stores user responses or RSVPs for events.

---

## Installation & Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL Server
- IDE (IntelliJ / Eclipse / VS Code)

### Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/meethive.git
   cd meethive/backend
   ```

2. **Configure Database**
   
   Open `src/main/resources/application.properties`
   
   Update MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/meethive
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   ```

3. **Build the Project**
   ```bash
   mvn clean install
   ```

4. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```
   
   The server will start at `http://localhost:8080`.

5. **Access the Frontend**
   
   Open `http://localhost:8080/login.html` in your browser.

---

## Project Modules

### 1. Authentication Module
- Handles user signup, login, and token-based session management.
- Uses JWT for secure authorization.

### 2. Event Management Module
- Users can create and view events.
- Events are stored in MySQL and linked to user accounts.
- Includes date validation and conflict checking.

### 3. Group Management Module
- Allows users to create and join interest-based groups.
- Supports adding members and group chat functionality.

### 4. Real-Time Chat Module
- Implements WebSocket communication for low-latency messaging.
- Displays user messages instantly without page reloads.
- Stores messages persistently in MySQL.

### 5. Calendar Module
- Integrates all events visually using a calendar interface.
- Fetches events dynamically via REST API calls.

---

## Example API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/auth/signup` | POST | Register a new user |
| `/api/auth/login` | POST | Authenticate a user |
| `/api/events` | GET | Fetch all events |
| `/api/events/create` | POST | Create a new event |
| `/api/groups` | GET | Get all groups |
| `/api/groups/create` | POST | Create a new group |
| `/api/messages/{groupId}` | GET | Fetch messages for a group |
| `/api/messages/send` | POST | Send a chat message |

---

## Conclusion

MeetHive demonstrates the integration of event management, community engagement, and real-time communication in a single scalable application. The project showcases efficient use of Spring Boot's layered architecture, MySQL persistence, and WebSocket-based real-time updates — all wrapped in a clean and responsive interface.

It serves as both a functional platform for community collaboration and a demonstration of full-stack development with Java Spring Boot.

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Contact

For questions or feedback, please open an issue on GitHub.
