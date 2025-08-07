import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
import java.util.Scanner;

public class CrudOperation {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "db22056#";

    private static final Scanner scanner = new Scanner(System.in);
//    private static final List<User> userList = new ArrayList<>();

    public static boolean notExistById(int id) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next();
            }
        }
    }

    public void createUser() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        System.out.print("Enter user id: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.print("Enter user name: ");
        String name = scanner.nextLine();
        System.out.print("Enter user gender: ");
        String gender = scanner.nextLine();
        System.out.print("Enter user email: ");
        String email = scanner.nextLine();
        System.out.print("Enter user date of birth: ");
        String dob = scanner.nextLine();
        User user = new User(id, name, gender, email, dob);
        String sql = """
                  insert into users
                    values (?, ?, ?, ?, ?::date)
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, user.getId());
        ps.setString(2, user.getName());
        ps.setString(3, user.getGender());
        ps.setString(4, user.getEmail());
        ps.setDate(5, Date.valueOf(user.getDob()));
        int rowsInserted = ps.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("A new user was inserted successfully!");
        } else {
            System.out.println("New User cannot be insert");
        }
        conn.close();
        ps.close();
    }

    public void readUserById() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        System.out.print("Enter User ID to find: ");
        int id = Integer.parseInt(scanner.nextLine());
        String sql = """
                select * from users where id = ?
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (notExistById(id)) {
            System.out.println("User does not exist!");
            return;
        }
        while (rs.next()) {
            User user = new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("gender"),
                    rs.getString("email"),
                    String.valueOf(rs.getDate("date_of_birth"))
            );
            System.out.println(user);
        }
        rs.close();
        ps.close();
        conn.close();
    }

    public void updateUserById() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        System.out.print("Enter ID to Update: ");
        int id = Integer.parseInt(scanner.nextLine());
        if (notExistById(id)) {
            System.out.println("User cannot be found");
            return;
        }
        System.out.print("Enter new Name: ");
        String newName = scanner.nextLine();
        System.out.print("Enter new Gender: ");
        String newGender = scanner.nextLine();
        System.out.print("Enter new Email: ");
        String newEmail = scanner.nextLine();
        System.out.print("Enter new Date of Birth: ");
        String newDob = scanner.nextLine();

        String sql = """
                update users set name = ?, gender = ?, email = ?, date_of_birth= ?::date
                where id = ?
                """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, newName);
        ps.setString(2, newGender);
        ps.setString(3, newEmail);
        ps.setDate(4, Date.valueOf(newDob));
        ps.setInt(5, id);

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Update Successfully");
        } else {
            System.out.println("Failed to Update");
        }
        ps.close();
        conn.close();
    }

    public void deleteUserById() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        System.out.print("Enter ID to Delete: ");
        int id = Integer.parseInt(scanner.nextLine());

        if (notExistById(id)) {
            System.out.println("User does not exist!");
            return;
        }

        String sql = "DELETE FROM users WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);

        int rowsAffected = ps.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("User deleted successfully!");
        } else {
            System.out.println("Failed to delete user");
        }
        ps.close();
        conn.close();
    }

    public void displayAllUsers() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        String sql = "SELECT * FROM users ORDER BY id";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        System.out.println("\nList of all users:");
        System.out.println("----------------------------------------");
        while (rs.next()) {
            User user = new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("gender"),
                    rs.getString("email"),
                    String.valueOf(rs.getDate("date_of_birth"))
            );
            System.out.println(user);
            System.out.println("----------------------------------------");
        }

        rs.close();
        stmt.close();
        conn.close();
    }

    public static void main(String[] args) {
        CrudOperation operation = new CrudOperation();

        while (true) {
            System.out.println("\nCRUD Operations Menu:");
            System.out.println("1. Create New User");
            System.out.println("2. Display User By ID");
            System.out.println("3. Update by ID");
            System.out.println("4. Delete by ID");
            System.out.println("5. Display All");
            System.out.println("0. Exit Program");
            System.out.print("Enter an Option: ");

            try {
                int op = Integer.parseInt(scanner.nextLine());

                switch (op) {
                    case 1 -> operation.createUser();
                    case 2 -> operation.readUserById();
                    case 3 -> operation.updateUserById();
                    case 4 -> operation.deleteUserById();
                    case 5 -> operation.displayAllUsers();
                    case 0 -> {
                        System.out.println("Exiting program...");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid Option...");
                }
            } catch (SQLException e) {
                System.out.println("Database Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }
}