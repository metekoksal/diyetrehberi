package diyetrehberi.diyetrehberi;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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
            System.out.println(e);
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
            System.out.println(e);
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
            System.out.println(e);
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
            System.out.println(e);
            return null;
        }
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int id) {
        currentUserId = id;
    }

    public void updateUserHeight(int userId, double newHeight) {
        String sql = "UPDATE users SET height = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, newHeight);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            System.out.println("Boy güncellendi: " + newHeight);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void updateUserWeight(int userId, double newWeight) {
        String sql = "UPDATE users SET weight = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, newWeight);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            System.out.println("Ağırlık güncellendi: " + newWeight);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    // Yemekleri veritabanından yükle
    public Map<String, FoodItem> loadFoodsFromDatabase() {
        Map<String, FoodItem> foodMap = new HashMap<>();

        String sql = "SELECT * FROM meals";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");
                FoodItem item = new FoodItem(
                        name,
                        rs.getDouble("serving_size_grams"),
                        rs.getDouble("calories"),
                        rs.getDouble("proteins"),
                        rs.getDouble("carbs"),
                        rs.getDouble("fats"),
                        rs.getString("category"),
                        rs.getInt("food_id")
                );
                foodMap.put(name, item);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return foodMap;
    }

    public Map<String, ExerciseItem> loadExercisesFromDatabase() {
        Map<String, ExerciseItem> exerciseMap = new HashMap<>();
        String sql = "SELECT name, category, calories_burned_per_min, exercise_id FROM exercises";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String name = rs.getString("name");
                String category = rs.getString("category");
                double caloriesBurnedPerMin = rs.getDouble("calories_burned_per_min");
                int exerciseId = rs.getInt("exercise_id");
                exerciseMap.put(name, new ExerciseItem(name, category, caloriesBurnedPerMin, exerciseId));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return exerciseMap;
    }

}
