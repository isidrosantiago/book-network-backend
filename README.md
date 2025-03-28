<h1 align="center">📚 Book Network Backend Project 📚</h1>
<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java Logo">
  &nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot Logo">
  &nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker Logo">
  &nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="Postgresql Logo">
  &nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20Web%20Tokens&logoColor=white" alt="JWT Logo">
  &nbsp;&nbsp;&nbsp;&nbsp;
  <img src="https://img.shields.io/badge/Swagger-89bf04?style=for-the-badge&logo=swagger&logoColor=white" alt="JWT Logo">
</p>

## Table of Contents

1. [Introduction](#introduction)
2. [Features Implemented](#features-implemented)
3. [API Endpoints](#api-endpoints)
4. [Prerequisites](#prerequisites)
5. [Installation and Setup](#installation-and-setup)

## Introduction

This backend project provides a robust API for managing books, user authentication, feedback, and book-related operations such as borrowing, returning, and sharing.

The API is designed to support functionalities like user sign-up and sign-in, book management (uploading, borrowing, returning, archiving), and feedback submission. It also includes features for managing shareable statuses and verifying user accounts.

## Features Implemented

### Authentication

- **Sign Up**: Register new users with essential details.
- **Sign In**: Authenticate users with email and password.
- **Email Verification**: Verify user email addresses.
- **Account Activation**: Activate user accounts using a confirmation token.

### Book Management

- **Create Book**: Add new books with details like title, author, synopsis, and ISBN.
- **Retrieve Books**: Fetch all books or filter by owner, borrowed status, or returned status.
- **Get Book by ID**: Retrieve detailed information about a specific book.
- **Upload Book Cover**: Upload a cover image for a specific book.
- **Borrow Book**: Borrow a book by its ID.
- **Return Borrowed Book**: Return a borrowed book.
- **Approve Return**: Approve the return of a borrowed book.
- **Update Shareable Status**: Mark a book as shareable or non-shareable.
- **Archive Book**: Archive a book to indicate it is no longer available.

### Feedback Management

- **Submit Feedback**: Provide feedback for a book, including a rating and comment.
- **Retrieve Feedback**: Fetch all feedback for a specific book.

## API Endpoints

Below are the key API endpoints implemented in this project:

### Authentication

- `POST /auth/sign-up`: Register a new user.
- `POST /auth/sign-in`: Authenticate a user.
- `GET /auth/verify-email`: Verify a user's email address.
- `GET /auth/activate-account`: Activate a user account using a token.

### Book Management

- `GET /books`: Retrieve a paginated list of all books.
- `POST /books`: Create a new book.
- `GET /books/{id}`: Retrieve detailed information about a specific book.
- `POST /books/cover/{book-id}`: Upload a cover image for a book.
- `POST /books/borrowed/{book-id}`: Borrow a book.
- `PATCH /books/borrow/return/{book-id}`: Return a borrowed book.
- `PATCH /books/borrow/return/approve/{book-id}`: Approve the return of a borrowed book.
- `PATCH /books/shareable/{book-id}`: Update the shareable status of a book.
- `PATCH /books/archive/{book-id}`: Archive a book.
- `GET /books/returned`: Retrieve a paginated list of returned books.
- `GET /books/owner`: Retrieve a paginated list of books owned by the authenticated user.
- `GET /books/borrowed`: Retrieve a paginated list of books borrowed by the authenticated user.

### Feedback Management

- `POST /feedback`: Submit feedback for a book.
- `GET /feedback/book/{book-id}`: Retrieve feedback for a specific book.

## Prerequisites

Before running the project, ensure the following prerequisites are met:

1. **Java Development Kit (JDK)**: This project requires **Java 17**. Verify your installation by running:

   ```bash
   java -version
   ```

   Ensure the output indicates Java 17 (e.g., `openjdk version "17.x.x"`).

2. **Docker and Docker Compose**: Install Docker and Docker Compose to run the application and its dependencies in containers.
   - Verify installation by running:
     ```bash
     docker --version
     docker-compose --version
     ```

## Installation and Setup

To set up this project locally, follow these steps:

1. **Clone the Repository**:

   ```bash
    git clone https://github.com/isidrosantiago/book-network-backend.git
   ```

2. **Navigate to the Project Directory**:

   ```bash
    cd book-network-backend
   ```

3. **Run Docker Compose**:

   ```bash
    docker-compose up -d
   ```

4. **Run the Application**:

   ```bash
    mvnw spring-boot:run
   ```

5. **Access the API**:
   The API will be accessible at `http://localhost:8080/api/v1`.

<br>

**Swagger**:
You can access the interactive API documentation and test endpoints using Swagger at the following URL `http://localhost:8080/api/v1/swagger-ui/index.html`.
