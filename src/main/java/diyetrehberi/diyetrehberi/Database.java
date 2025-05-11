package diyetrehberi.diyetrehberi;

import java.sql.*;

public class Database {
    private static Database instance;
    private Connection connection;

    private static final String DB_URL = "jdbc:sqlite:diyetrehberi.db";

    private static int currentUserId = -1;  // Store the current user ID after signup

    private Database() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Veritabanına bağlanıldı.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    // Method to create a user in the database
    public void createUser(String name, int age, String gender, double height, double weight) {
        String sql = "INSERT INTO users (name, age, gender, height, weight) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, gender);
            pstmt.setDouble(4, height);
            pstmt.setDouble(5, weight);
            pstmt.executeUpdate();

            // Retrieve the generated user ID after inserting
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                currentUserId = rs.getInt(1);  // Save the current user ID
                System.out.println("Kullanıcı eklendi: " + name + " ID: " + currentUserId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to remove a user from the database
    public void removeUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Kullanıcı silindi. ID: " + id);
            } else {
                System.out.println("Belirtilen ID ile kullanıcı bulunamadı: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to get all details of a user based on the ID
    public User getUserDetails(int id) {
        String sql = "SELECT name, age, gender, height, weight FROM users WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String gender = rs.getString("gender");
                double height = rs.getDouble("height");
                double weight = rs.getDouble("weight");
                return new User(name, age, gender, height, weight);
            } else {
                System.out.println("Kullanıcı bulunamadı. ID: " + id);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    // Getter for the current user ID
    public int getCurrentUserId() {
        return currentUserId;
    }

    // Setter to manually set the current user ID (if needed)
    public void setCurrentUserId(int id) {
        currentUserId = id;
    }
}
