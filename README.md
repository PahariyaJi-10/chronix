Chronix – Distributed Job Scheduler & Rate Limiter

Chronix is a backend application designed to schedule, execute, and manage background jobs reliably across multiple application instances. It uses distributed locking to prevent multiple instances from executing the same scheduled job simultaneously.

The project focuses on backend engineering concepts such as distributed systems, concurrency control, job scheduling, rate limiting, fault tolerance, and database-backed coordination.

🚀 Features

1. Job Management

- Create, retrieve, update, and delete jobs.
- Pause and resume scheduled jobs.
- Cancel job execution.
- Retry failed jobs.
- Manage job execution status.

2. Job Scheduling

- Automated background job scheduling.
- Scheduler checks for eligible jobs every 10 seconds.
- Supports scheduled execution of jobs.
- Job timeout monitoring every 5 seconds.

3. Distributed Locking

- PostgreSQL-based distributed locking.
- Prevents duplicate execution across multiple application instances.
- Supports multiple Spring Boot instances sharing the same database.
- Ensures coordinated job execution across instances.

4. Concurrency & Rate Limiting

- Per-job concurrency limits.
- Rate limiting for controlled request execution.
- Job dependency management.

5. Reliability & Monitoring

- Automatic retry mechanism.
- Dead-letter job handling.
- Job execution metrics.
- Audit logging.
- Timeout detection and cleanup.

🛠️ Tech Stack

Technology| Purpose
Java 17| Backend programming
Spring Boot| Backend framework
Spring Data JPA| Database interaction
PostgreSQL 17| Persistent storage
Maven| Build and dependency management
REST APIs| Client-server communication
JUnit| Unit and integration testing

🏗️ Architecture

Chronix follows a backend architecture in which multiple application instances coordinate through a shared PostgreSQL database.

             Client / REST API
                    |
             Spring Boot App
                    |
          +---------+---------+
          |                   |
      Job Service         Scheduler
          |                   |
          +---------+---------+
                    |
             PostgreSQL DB
                    |
          Distributed Job Locks
                    |
          +---------+---------+
          |                   |
      Instance 1           Instance 2
      Port 8080            Port 8082

Both instances share the same PostgreSQL database and coordinate job execution using distributed locks.

🔌 API Endpoints

Base URL: "http://localhost:8080"

Job Management

Method| Endpoint| Description
POST| "/api/jobs"| Create a job
GET| "/api/jobs"| Get all jobs
GET| "/api/jobs/{id}"| Get job by ID
PUT| "/api/jobs/{id}"| Update a job
DELETE| "/api/jobs/{id}"| Delete a job

Additional job operations include:

- Retry
- Pause
- Resume
- Cancel

⚙️ Getting Started

Prerequisites

Make sure you have installed:

- Java 17
- PostgreSQL 17
- Maven
- Git

1. Clone the repository



2. Create PostgreSQL Database

Open PostgreSQL and execute:

CREATE DATABASE chronix;

3. Configure Database

Update your "application.properties" file:

spring.datasource.url=jdbc:postgresql://localhost:5432/chronix
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

4. Run the Application

mvn spring-boot:run

The application will start on:

http://localhost:8080

5. Run Tests

mvn test

🧪 Testing & Validation

The project has successfully passed 11 tests in the latest recorded test run:

- 1 application context test
- 4 job lock repository integration tests
- 6 job lock service unit tests

Build Status: BUILD SUCCESS

Distributed locking was also tested with two application instances running on different ports (8080 and 8082), sharing the same PostgreSQL database.

🎯 Project Objectives

Chronix aims to demonstrate practical implementation of:

- Distributed job scheduling.
- Database-backed distributed coordination.
- Concurrent task execution.
- Rate limiting.
- Fault tolerance and retry mechanisms.
- Multi-instance backend architecture.

📌 Current Development Status

The project has progressed beyond basic CRUD operations and includes distributed job locking, multi-instance coordination, and reliability mechanisms.

Further development can focus on improving scalability, observability, performance, and production readiness.

👨‍💻 By :-

Divyansh Pahariya

GitHub: "PahariyaJi-10" (https://github.com/PahariyaJi-10)
