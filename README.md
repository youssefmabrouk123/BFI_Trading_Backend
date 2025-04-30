## BFI Trading Backend



A Spring Boot backend application for a trading platform with JWT authentication, role-based authorization, and token management. This system provides secure API endpoints for trading operations, user management, and financial transactions.



## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)
- [Acknowledgements](#acknowledgements)



## Features

- Secure JWT-based authentication system
- Role-based access control for different user types
- RESTful API endpoints for trading operations
- Real-time order processing with delay functionality
- Cross-currency pair trading support
- Comprehensive error handling and validation



## Technologies Used

- Java
- Spring Boot
- Spring Security
- JSON Web Tokens (JWT)
- Maven
- MySQL (or relevant database)
- Git
## Getting Started

Instructions on setting up your project locally.

### Prerequisites

Ensure you have the following installed:

- Java Development Kit (JDK) 11 or later
- Apache Maven
- MySQL Server (or your configured database)
- Git

```bash
# Check Java version
java -version
# Check Maven version
mvn -version
```

### Installation

1. Clone the repository
   ```bash
   git clone https://github.com/youssefmabrouk123/BFI_Trading_Backend.git
   cd BFI_Trading_Backend
   ```
2. Configure environment variables
   - Create a `.env` file based on the `.env.example` (if provided) or configure application properties (`application.properties` or `application.yml`) with your database connection details, JWT secret, etc.
   - Example `application.properties` settings:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
     spring.datasource.username=your_db_user
     spring.datasource.password=your_db_password
     jwt.secret=your_jwt_secret_key
     ```
3. Build the project
   ```bash
   mvn clean install
   ```
4. Run the application
   ```bash
   mvn spring-boot:run
   # Or run the packaged jar file
   # java -jar target/bfi_trading_backend-0.0.1-SNAPSHOT.jar
   ```

## Usage

### API Endpoints

The application exposes the following main API endpoints:

#### Authentication
- `POST /api/auth/login` - Authenticate a user and get JWT token
- `POST /api/auth/register` - Register a new user

#### Trading Operations
- `GET /api/trading/currencies` - Get list of available currencies
- `GET /api/trading/crossparities` - Get list of available cross-parities
- `POST /api/trading/order` - Create a new trading order
- `GET /api/trading/orders` - Get user's orders
- `GET /api/trading/order/{id}` - Get specific order details
- `PUT /api/trading/order/{id}` - Update order status

### Example API Requests

#### Login Request
```json
POST /api/auth/login
{
  "username": "trader1",
  "password": "securepassword"
}
```

#### Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "trader1",
  "roles": ["ROLE_USER", "ROLE_TRADER"]
}
```

#### Creating a Trading Order
```json
POST /api/trading/order
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

{
  "currencyPair": "EUR/USD",
  "amount": 10000,
  "price": 1.0875,
  "type": "BUY",
  "status": "PENDING"
}
```


## Contributing

Guidelines for contributing to this project:

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

Distributed under the MIT License. See `LICENSE` for more information.
## Contact

Youssef Mabrouk - [GitHub Profile](https://github.com/youssefmabrouk123)

Project Link: [https://github.com/youssefmabrouk123/BFI_Trading_Backend](https://github.com/youssefmabrouk123/BFI_Trading_Backend)

