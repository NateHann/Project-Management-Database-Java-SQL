# Poise Project Management System (PoisePMS)

PoisePMS is a project management system designed to efficiently manage construction projects. It allows construction managers and their teams to add, update, finalize, and delete projects, and provides functionalities for listing and searching for projects, managing project deadlines, and maintaining detailed records of all involved parties.

## Features

- **Add New Projects**: Input details to track new construction projects.
- **Update Projects**: Modify details of existing projects as needed.
- **Finalize Projects**: Mark projects as completed and archive their details.
- **Delete Projects**: Remove projects from the system when no longer needed.
- **List Uncompleted Projects**: View a list of all active projects.
- **List Overdue Projects**: Identify projects that have passed their planned completion dates.
- **Find Projects**: Search for projects by ID or name.
- **Management Views**: Access comprehensive listings of engineers, managers, and customers involved.

## Getting Started

These instructions will help you get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

Ensure you have Java JDK 11 or newer and MySQL 5.7 or newer installed. You will also need the JDBC driver for MySQL added to your project.

### Installation

1. **Clone the repository**:
git clone https://yourrepositorylink.com
2. **Set up the MySQL database**:
- Create a MySQL database named `PoisePMS`.
- Import the provided `schema.sql` to set up the necessary database tables.

3. **Configure database credentials**:
- Open `PoisePMS.java`.
- Modify the `USER` and `PASS` constants in the PoisePMS class to match your MySQL credentials.

4. **Compile and run the project**:
- Navigate to the project directory in your terminal.
- Compile the Java code using:
  ```
  javac PoisePMS.java
  ```
- Run the compiled Java program using:
  ```
  java PoisePMS
  ```

## Usage

Once you start the application, follow the on-screen prompts to interact with the system. Use the main menu to select actions such as adding, updating, finalizing, or deleting projects. You can also view lists of uncompleted or overdue projects and search for specific projects by their identifiers.

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the `LICENSE` file for details.
