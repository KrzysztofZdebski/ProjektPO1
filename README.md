# Sports Betting App

**Sports Betting App** is a Java-based application designed to simulate and manage sports betting activities. Developed as part of a university project, it offers functionalities for placing bets, managing user accounts, and simulating sports events.

## Features

- **User Management**: Create and manage user accounts with secure authentication.
- **Bet Placement**: Users can place bets on various sports events with real-time odds.
- **Event Simulation**: Simulate sports events and determine outcomes based on predefined algorithms.
- **Account Balance Tracking**: Monitor user balances, winnings, and losses.
- **Administrative Controls**: Admin panel for managing events, users, and system settings.

## Installation

To run the Sports Betting App locally, follow these steps:

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/KrzysztofZdebski/ProjektPO1.git
   cd ProjektPO1
   ```

2. **Build the Project**:
   Ensure you have [Maven](https://maven.apache.org/) installed. Then, build the project:
   ```bash
   mvn clean install
   ```

3. **Run the Application**:
   ```bash
   mvn exec:java -Dexec.mainClass="com.example.Main"
   ```
   Replace `com.example.Main` with the actual main class path if different.

## Usage

Upon running the application:

- **Register/Login**: Create a new account or log in with existing credentials.
- **Place Bets**: Navigate to the betting section to view available events and place bets.
- **View Results**: After event simulations, check the outcomes and your account balance.

## Project Structure

```
ProjektPO1/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── example/
│                   ├── Main.java
│                   ├── controllers/
│                   ├── models/
│                   └── views/
├── pom.xml
└── README.md
```

- **controllers/**: Contains the logic for handling user interactions.
- **models/**: Defines the data structures and business logic.
- **views/**: Manages the user interface components.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request for any enhancements or bug fixes.

## License

This project is licensed under the [MIT License](LICENSE).

## Contact

For any questions or feedback, please contact [Krzysztof Zdebski](mailto:krzysztof.zdebski@example.com).
