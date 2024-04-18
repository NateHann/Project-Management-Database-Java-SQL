import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * This class represents the project management system for PoisePMS.
 * It provides functionalities to manage projects, including adding,
 * updating, finalizing, and deleting projects, along with listing and finding
 * projects.
 */
public class PoisePMS {
    private static final String URL = "jdbc:mysql://localhost:3306/PoisePMS"; // Database URL
    private static final String USER = "root"; // Database username
    private static final String PASS = "1111"; // Database password

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void main(String[] args) {
        PoisePMS system = new PoisePMS();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("\nSelect an option:");
                System.out.println("1 - Add New Project");
                System.out.println("2 - Update Project");
                System.out.println("3 - Finalize Project");
                System.out.println("4 - Delete Project");
                System.out.println("5 - List Uncompleted Projects");
                System.out.println("6 - List Overdue Projects");
                System.out.println("7 - Find Project");
                System.out.println("8 - Show All Projects");
                System.out.println("9 - Show All Engineers");
                System.out.println("10 - Show All Managers");
                System.out.println("11 - Show All Customers");
                System.out.println("0 - Exit");

                int option = scanner.nextInt(); // Read user option
                scanner.nextLine(); // Clear the buffer after reading an integer

                switch (option) {
                    case 1:
                        system.addNewProject(scanner);
                        break;
                    case 2:
                        system.updateProject(scanner);
                        break;
                    case 3:
                        system.finalizeProject(scanner);
                        break;
                    case 4:
                        system.deleteProject(scanner);
                        break;
                    case 5:
                        system.listUncompletedProjects();
                        break;
                    case 6:
                        system.listOverdueProjects();
                        break;
                    case 7:
                        system.findProject(scanner);
                        break;
                    case 8:
                        system.showAllProjects();
                        break;
                    case 9:
                        system.showAll("Engineers");
                        break;
                    case 10:
                        system.showAll("Managers");
                        break;
                    case 11:
                        system.showAll("Customers");
                        break;
                    case 0:
                        System.out.println("Exiting program.");
                        scanner.close();
                        return; // Exit the program
                    default:
                        System.out.println("Invalid option, please try again.");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input, please enter a number.");
                scanner.nextLine(); // Consume the incorrect input
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Displays a table based on the provided table name and prompts the user to
     * select a row based on the ID column.
     * The method determines the correct ID column name for the table and displays
     * the relevant information.
     * If the table name provided does not match any of the predefined cases, an
     * error message is printed
     * and the method returns -1.
     *
     * @param tableName the name of the table to display. The recognized table names
     *                  are "Engineers",
     *                  "Managers", "Architects", and "Customers".
     * @param scanner   the Scanner instance used to read input from the user.
     * @return an integer representing the success or failure of the operation. If
     *         the table name is valid,
     *         this method is intended to return the selected row's ID (this part is
     *         not implemented in the
     *         provided code and depends on additional implementation details).
     *         Returns -1 if an invalid
     *         table name is provided.
     */
    private int displayTableAndSelect(String tableName, Scanner scanner) {
        String idColumn; // Variable to store the correct ID column name based on the table

        // Determine the correct ID column name
        switch (tableName) {
            case "Engineers":
                idColumn = "engineer_id";
                break;
            case "Managers":
                idColumn = "manager_id";
                break;
            case "Architects":
                idColumn = "architect_id";
                break;
            case "Customers":
                idColumn = "customer_id";
                break;
            default:
                System.out.println("Invalid table name provided.");
                return -1;
        }

        String sql = "SELECT " + idColumn + ", name FROM " + tableName;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println(tableName + " available:");
            if (!rs.isBeforeFirst()) { // Check if ResultSet is empty
                System.out.println("No entries found.");
                return addNewPerson(tableName, scanner); // Automatically prompt to add a new entry
            }

            while (rs.next()) {
                System.out.println(rs.getInt(idColumn) + " - " + rs.getString("name"));
            }

            System.out.println("Enter the ID of the " + tableName.toLowerCase() + ", or type 'new' to add a new one:");
            while (true) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("new")) {
                    return addNewPerson(tableName, scanner);
                } else {
                    try {
                        int id = Integer.parseInt(input);
                        return id; // Return the chosen ID
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number or 'new' to add an entry.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error in displayTableAndSelect: " + e.getMessage());
            return -1;
        }
    }

    private int addNewPerson(String tableName, Scanner scanner) {
        System.out.println("Adding new " + tableName.toLowerCase());
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();

        String sql = "INSERT INTO " + tableName + " (name, phone, email, address) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, email);
            pstmt.setString(4, address);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the new ID
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Error when adding new " + tableName.toLowerCase() + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Adds a new project to the database with user-provided details, using various
     * inputs such as project name,
     * building type, address, ERF number, total fee, amount paid, deadline, and
     * descriptions for the project.
     * The method handles project name automatically if left blank by deriving it
     * from the customer's surname.
     * It involves interactions with other entities such as Engineers, Managers,
     * Architects, and Customers.
     * Each related entity is selected or created through a display-and-select
     * interface. This method assumes
     * a valid database connection method named `connect` exists within the same
     * class or accessible scope.
     *
     * <p>
     * Error handling is implemented for SQL exceptions, which are caught and
     * displayed to the user. The method
     * prints success or error messages directly to the console based on the
     * execution outcomes.
     *
     * @param scanner A Scanner object for reading user input, ensuring flexibility
     *                in user interaction.
     *                This scanner should be passed from the calling context to
     *                handle multiple user inputs.
     * @throws SQLException if a database access error occurs or this method is
     *                      called on a closed connection.
     */
    private void addNewProject(Scanner scanner) {
        System.out.println(
                "Enter project details. Leave the project name blank to automatically name it based on the building type and customer's surname.");

        // Display available customers and handle selection
        System.out.println("Available Customers:");
        int customerId = displayTableAndSelect("Customers", scanner); // This method needs to correctly display and
        // handle customer selection

        // Prompt for building type first as it may be used in naming
        System.out.print("Building Type: ");
        String buildingType = scanner.nextLine();

        // Prompt for project name with option to leave blank
        System.out.print("Project Name (optional, leave blank to auto-generate): ");
        String projectName = scanner.nextLine();

        // Generate a project name based on customer surname and building type if left
        // blank
        if (projectName.isBlank()) {
            String sqlCustomer = "SELECT name FROM Customers WHERE customer_id = ?";
            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(sqlCustomer)) {
                pstmt.setInt(1, customerId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    String customerName = rs.getString("name");
                    projectName = buildingType + " " + customerName.split(" ")[1];
                }
            } catch (SQLException e) {
                System.out.println("SQL Error: " + e.getMessage());
                return;
            }
        }

        // Continue with other project details
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("ERF Number: ");
        String erfNumber = scanner.nextLine();
        System.out.print("Total Fee: ");
        double totalFee = scanner.nextDouble();
        System.out.print("Amount Paid: ");
        double amountPaid = scanner.nextDouble();
        scanner.nextLine(); // Clear the buffer after reading a double
        System.out.print("Deadline (YYYY-MM-DD): ");
        String deadline = scanner.nextLine();

        // Additional entity selections
        System.out.println("Available Engineers:");
        int engineerId = displayTableAndSelect("Engineers", scanner);
        System.out.println("Available Managers:");
        int managerId = displayTableAndSelect("Managers", scanner);
        System.out.println("Available Architects:");
        int architectId = displayTableAndSelect("Architects", scanner);
        scanner.nextLine(); // Clear the buffer

        System.out.print("Description: ");
        String description = scanner.nextLine();

        // Insert the project into the database
        String sql = "INSERT INTO Projects (project_name, building_type, project_address, erf_number, total_fee, amount_paid, deadline, engineer_id, manager_id, architect_id, customer_id, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, projectName);
            pstmt.setString(2, buildingType);
            pstmt.setString(3, address);
            pstmt.setString(4, erfNumber);
            pstmt.setDouble(5, totalFee);
            pstmt.setDouble(6, amountPaid);
            pstmt.setDate(7, Date.valueOf(deadline));
            pstmt.setInt(8, engineerId);
            pstmt.setInt(9, managerId);
            pstmt.setInt(10, architectId);
            pstmt.setInt(11, customerId);
            pstmt.setString(12, description);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Project added successfully with name: " + projectName);
            } else {
                System.out.println("Failed to add project.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    /**
     * Updates the deadline of an existing project in the database.
     * This method prompts the user for the project's ID and the new deadline,
     * then updates the corresponding project record in the database.
     *
     * @param scanner A Scanner object for reading user input, used to capture the
     *                project ID and new deadline date.
     */
    private void updateProject(Scanner scanner) {
        System.out.print("Enter the Project ID to update: ");
        int projectId = scanner.nextInt(); // Get project ID from user input
        scanner.nextLine(); // Clear the buffer

        System.out.println("Select the attribute to update:");
        System.out.println("1 - Deadline");
        System.out.println("2 - Engineer");
        System.out.println("3 - Manager");
        System.out.println("4 - Customer");
        System.out.println("5 - Architect");
        System.out.println("6 - Other Project Details");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Clear the buffer

        StringBuilder sqlBuilder = new StringBuilder("UPDATE Projects SET ");
        List<Object> params = new ArrayList<>();

        switch (choice) {
            case 1:
                System.out.print("New deadline (YYYY-MM-DD): ");
                String newDeadlineStr = scanner.nextLine();
                Date newDeadline = Date.valueOf(newDeadlineStr); // Convert the string to a SQL Date object
                sqlBuilder.append("deadline = ?");
                params.add(newDeadline);
                break;
            case 2:
                System.out.println("Available Engineers:");
                int newEngineerId = displayTableAndSelect("Engineers", scanner);
                sqlBuilder.append("engineer_id = ?");
                params.add(newEngineerId);
                break;
            case 3:
                System.out.println("Available Managers:");
                int newManagerId = displayTableAndSelect("Managers", scanner);
                sqlBuilder.append("manager_id = ?");
                params.add(newManagerId);
                break;
            case 4:
                System.out.println("Available Customers:");
                int newCustomerId = displayTableAndSelect("Customers", scanner);
                sqlBuilder.append("customer_id = ?");
                params.add(newCustomerId);
                break;
            case 5:
                System.out.println("Available Architects:");
                int newArchitectId = displayTableAndSelect("Architects", scanner);
                sqlBuilder.append("architect_id = ?");
                params.add(newArchitectId);
                break;
            case 6:
                updateOtherProjectDetails(scanner, sqlBuilder, params);
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        sqlBuilder.append(" WHERE project_id = ?");
        params.add(projectId);

        try (Connection conn = connect();
             PreparedStatement pstmt = prepareStatement(conn, sqlBuilder.toString(), params)) {
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Project updated successfully!");
            } else {
                System.out.println("Failed to update project. Ensure the project ID is correct.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    /**
     * Creates a {@link PreparedStatement} for the given SQL query and sets its
     * parameters based on the types
     * and values provided in the list. Supports parameters of type String, Integer,
     * Double, and Date.
     * Each parameter's type is checked dynamically, and the appropriate setter
     * method is called on the
     * {@link PreparedStatement} object.
     *
     * @param conn   the {@link Connection} object used to create the
     *               {@link PreparedStatement}.
     * @param sql    the SQL query string that needs to be prepared.
     * @param params a list of objects containing the parameters to be set in the
     *               {@link PreparedStatement}.
     *               The method handles the following types: String, Integer,
     *               Double, and Date.
     * @return a {@link PreparedStatement} with the set parameters ready for
     *         execution.
     * @throws SQLException if an SQL error occurs during the creation or parameter
     *                      setting of the {@link PreparedStatement}.
     */
    private PreparedStatement prepareStatement(Connection conn, String sql, List<Object> params) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            if (param instanceof String) {
                pstmt.setString(i + 1, (String) param);
            } else if (param instanceof Integer) {
                pstmt.setInt(i + 1, (Integer) param);
            } else if (param instanceof Double) {
                pstmt.setDouble(i + 1, (Double) param);
            } else if (param instanceof Date) {
                pstmt.setDate(i + 1, new java.sql.Date(((Date) param).getTime()));
            }
        }
        return pstmt;
    }

    /**
     * Updates SQL query parameters for modifying project details based on user
     * input. This method
     * prompts the user to input new values for various project attributes such as
     * the project name,
     * building type, address, ERF number, total fee, amount paid, and description.
     * Each attribute
     * update is optional; the user can press Enter to skip updating any specific
     * attribute.
     *
     * If the user provides a new value for an attribute, this method appends the
     * corresponding SQL
     * update clause to the provided {@link StringBuilder} and adds the new value to
     * the {@link List}
     * of parameters. This facilitates dynamic SQL query construction based on
     * user-specified updates.
     *
     * Note: This method assumes that the calling code handles the execution of the
     * SQL statement built
     * using the {@link StringBuilder} and {@link List} of parameters.
     *
     * @param scanner    The {@link Scanner} object to read user input.
     * @param sqlBuilder The {@link StringBuilder} used to build the SQL update
     *                   statement.
     * @param params     The {@link List} of Objects that holds the parameters for
     *                   the PreparedStatement.
     *                   The types of objects added to this list must be compatible
     *                   with the types expected
     *                   by the SQL statement being built.
     */
    private void updateOtherProjectDetails(Scanner scanner, StringBuilder sqlBuilder, List<Object> params) {
        System.out.println("Update other project details:");
        System.out.print("New Project Name (press Enter to skip): ");
        String projectName = scanner.nextLine();
        if (!projectName.isEmpty()) {
            sqlBuilder.append("project_name = ?, ");
            params.add(projectName);
        }

        System.out.print("New Building Type (press Enter to skip): ");
        String buildingType = scanner.nextLine();
        if (!buildingType.isEmpty()) {
            sqlBuilder.append("building_type = ?, ");
            params.add(buildingType);
        }

        System.out.print("New Address (press Enter to skip): ");
        String address = scanner.nextLine();
        if (!address.isEmpty()) {
            sqlBuilder.append("project_address = ?, ");
            params.add(address);
        }

        System.out.print("New ERF Number (press Enter to skip): ");
        String erfNumber = scanner.nextLine();
        if (!erfNumber.isEmpty()) {
            sqlBuilder.append("erf_number = ?, ");
            params.add(erfNumber);
        }

        System.out.print("New Total Fee (press Enter to skip): ");
        String totalFeeInput = scanner.nextLine();
        if (!totalFeeInput.isEmpty()) {
            sqlBuilder.append("total_fee = ?, ");
            params.add(Double.parseDouble(totalFeeInput));
        }

        System.out.print("New Amount Paid (press Enter to skip): ");
        String amountPaidInput = scanner.nextLine();
        if (!amountPaidInput.isEmpty()) {
            sqlBuilder.append("amount_paid = ?, ");
            params.add(Double.parseDouble(amountPaidInput));
        }

        System.out.print("New Description (press Enter to skip): ");
        String description = scanner.nextLine();
        if (!description.isEmpty()) {
            sqlBuilder.append("description = ?, ");
            params.add(description);
        }

        // Remove the last comma if there is one
        if (sqlBuilder.toString().endsWith(", ")) {
            sqlBuilder.setLength(sqlBuilder.length() - 2);
        }
    }

    /**
     * Finalizes a project by updating its status to 'finalized' and setting the
     * completion date.
     * This method prompts the user for the project ID and the completion date,
     * then updates the project record in the database to reflect its finalization.
     *
     * @param scanner A Scanner object for reading user input, used to capture the
     *                project ID and the completion date.
     */
    private void finalizeProject(Scanner scanner) {
        System.out.print("Enter the Project ID to finalize: ");
        int projectId = scanner.nextInt(); // Get project ID from user input
        scanner.nextLine(); // Clear the buffer
        System.out.print("Completion date (YYYY-MM-DD): ");
        String completionDateStr = scanner.nextLine(); // Get the completion date as a string
        Date completionDate = Date.valueOf(completionDateStr); // Convert the string to a SQL Date object

        // Prepare SQL statement for updating the project's status to 'finalized' and
        // setting the completion date
        String sql = "UPDATE Projects SET is_finalized = 1, completion_date = ? WHERE project_id = ?";
        try (Connection conn = connect(); // Establish connection to the database
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, completionDate); // Set the completion date in the prepared statement
            pstmt.setInt(2, projectId); // Set the project ID in the prepared statement
            int affectedRows = pstmt.executeUpdate(); // Execute the update operation
            if (affectedRows > 0) {
                System.out.println("Project finalized successfully!");
            } else {
                System.out.println("Failed to finalize project."); // Handle case where no rows were updated
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage()); // Handle SQL errors
        }
    }

    /**
     * Deletes a project from the database identified by its project ID.
     * This method prompts the user for the project ID and deletes the corresponding
     * project record from the database.
     *
     * @param scanner A Scanner object for reading user input, used to capture the
     *                project ID.
     */
    private void deleteProject(Scanner scanner) {
        System.out.print("Enter the Project ID to delete: ");
        int projectId = scanner.nextInt(); // Get project ID from user input
        scanner.nextLine(); // Clear the buffer

        // SQL statement to delete the project
        String sql = "DELETE FROM Projects WHERE project_id = ?";
        try (Connection conn = connect(); // Establish connection to the database
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId); // Set the project ID in the prepared statement
            int affectedRows = pstmt.executeUpdate(); // Execute the deletion
            if (affectedRows > 0) {
                System.out.println("Project deleted successfully!");
            } else {
                System.out.println("Failed to delete project."); // Handle case where no rows were deleted
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage()); // Handle SQL errors
        }
    }

    /**
     * Lists all projects that have not been finalized.
     * This method queries the database for projects that are still active (not
     * finalized)
     * and prints their details.
     */
    private void listUncompletedProjects() {
        String sql = "SELECT * FROM Projects WHERE is_finalized = 0"; // SQL query to find uncompleted projects

        try (Connection conn = connect(); // Establish connection to the database
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (!rs.next()) {
                System.out.println("No uncompleted projects found.");
                return;
            }
            do {
                // Print details of each uncompleted project
                System.out.println("Project ID: " + rs.getInt("project_id") + ", Name: " + rs.getString("project_name")
                        + ", Deadline: " + rs.getDate("deadline"));
            } while (rs.next());
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage()); // Handle SQL errors
        }
    }

    /**
     * Lists all projects that are overdue and not yet finalized.
     * This method queries the database for projects whose deadlines have passed but
     * are still marked as active.
     * It then prints the details of these projects.
     */
    private void listOverdueProjects() {
        String sql = "SELECT * FROM Projects WHERE deadline < CURDATE() AND is_finalized = 0"; // SQL query to find
        // overdue projects

        try (Connection conn = connect(); // Establish connection to the database
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (!rs.next()) {
                System.out.println("No overdue projects found.");
                return;
            }
            do {
                // Print details of each overdue project
                System.out.println("Project ID: " + rs.getInt("project_id") + ", Name: " + rs.getString("project_name")
                        + ", Deadline: " + rs.getDate("deadline"));
            } while (rs.next());
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage()); // Handle SQL errors
        }
    }

    /**
     * Allows searching for a project by either its ID or name.
     * This method reads an input which can be either an integer (project ID) or a
     * string (project name)
     * and calls the appropriate method to find and display the project details.
     *
     * @param scanner Scanner object for reading user input.
     */
    private void findProject(Scanner scanner) {
        System.out.print("Enter Project ID or Name: ");
        String input = scanner.nextLine();
        try {
            int id = Integer.parseInt(input); // Attempt to parse input as integer
            findProjectById(id); // Handle input as project ID
        } catch (NumberFormatException e) {
            findProjectByName(input); // Handle input as project name
        }
    }

    /**
     * Displays all projects in the database.
     * Retrieves and prints details for each project stored in the database.
     */
    private void showAllProjects() {
        String sql = "SELECT * FROM Projects";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (!rs.next()) {
                System.out.println("No projects found.");
                return;
            }
            do {
                System.out.println("Project ID: " + rs.getInt("project_id") + ", Name: " + rs.getString("project_name")
                        + ", Status: " + (rs.getInt("is_finalized") == 1 ? "Finalized" : "Not Finalized")
                        + ", Deadline: " + rs.getDate("deadline"));
            } while (rs.next());
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    /**
     * Displays all records from a specified table.
     * The method is generalized to accept any table name to display all records
     * from that table.
     *
     * @param tableName The name of the table from which to fetch and display
     *                  records.
     */
    private void showAll(String tableName) {
        String sql = "SELECT * FROM " + tableName;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (!rs.next()) {
                System.out.println("No " + tableName.toLowerCase() + " found.");
                return;
            }
            System.out.println(tableName + ":");
            do {
                System.out.println("ID: " + rs.getInt(1) + ", Name: " + rs.getString(2) + ", Phone: " + rs.getString(3)
                        + ", Email: " + rs.getString(4) + ", Address: " + rs.getString(5));
            } while (rs.next());
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    /**
     * Finds a project by its ID.
     * This method retrieves a project's details from the database based on its ID
     * and prints them.
     *
     * @param projectId The ID of the project to find.
     */
    private void findProjectById(int projectId) {
        String sql = "SELECT * FROM Projects WHERE project_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No project found with ID: " + projectId);
                    return;
                }
                printProjectDetails(rs);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    /**
     * Finds a project by its name.
     * This method retrieves a project's details from the database based on its name
     * and prints them.
     *
     * @param projectName The name of the project to find.
     */
    private void findProjectByName(String projectName) {
        String sql = "SELECT * FROM Projects WHERE project_name = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, projectName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("No project found with name: " + projectName);
                    return;
                }
                printProjectDetails(rs);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    /**
     * Prints the details of a project from a ResultSet.
     * This method is used to display project details after a query has been
     * performed.
     *
     * @param rs The ResultSet from which project details are to be printed.
     * @throws SQLException if there is a problem accessing data from the ResultSet.
     */
    private void printProjectDetails(ResultSet rs) throws SQLException {
        System.out.println("\nProject Details:");
        System.out.println("Project ID: " + rs.getInt("project_id"));
        System.out.println("Name: " + rs.getString("project_name"));
        System.out.println("Building Type: " + rs.getString("building_type"));
        System.out.println("Address: " + rs.getString("project_address"));
        System.out.println("ERF Number: " + rs.getString("erf_number"));
        System.out.println("Total Fee: " + rs.getDouble("total_fee"));
        System.out.println("Amount Paid: " + rs.getDouble("amount_paid"));
        System.out.println("Deadline: " + rs.getDate("deadline"));
        System.out.println("Engineer ID: " + rs.getInt("engineer_id"));
        System.out.println("Manager ID: " + rs.getInt("manager_id"));
        System.out.println("Architect ID: " + rs.getInt("architect_id"));
        System.out.println("Customer ID: " + rs.getInt("customer_id"));
        System.out.println("Status: " + (rs.getInt("is_finalized") == 1 ? "Finalized" : "Not Finalized"));
        System.out.println("Description: " + rs.getString("description"));
        if (rs.getDate("completion_date") != null) {
            System.out.println("Completion Date: " + rs.getDate("completion_date"));
        }
    }
}
