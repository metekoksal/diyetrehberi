package diyetrehberi;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private static Database instance;
    private Connection connection;

    // db dosya url'i
    private static final String DB_URL = "jdbc:sqlite:diyetrehberi.db";

    //user id init.
    private static int currentUserId = -1;

    //constructor, singleton veritabanı bağlantısı oluşturmak için
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

    // db'e kullanıcı ekleme
    public void createUser(String name, int age, String gender, String email, double height, double weight) {
        String sql = "INSERT INTO users (name, age, gender, email, height, weight) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, gender);
            pstmt.setString(4, email);
            pstmt.setDouble(5, height);
            pstmt.setDouble(6, weight);
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                currentUserId = rs.getInt(1);
                System.out.println("Kullanıcı eklendi: " + name + " ID: " + currentUserId);
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // db'den kullanıcı silme
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

    // get all details of a user based on the ID
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

    public void updateUserAge(int userId, int age) {
        String sql = "UPDATE users SET age = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, age);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            System.out.println("age güncellendi: " + age);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    // Yemekleri veritabanından yükleme
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

    // Egzersizleri veritabanından yükleme
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

    // günlük özet egzersizleri yükleme
    public List<ExerciseEntry> loadTodaysExerciseEntriesForUser(int userId) {
        List<ExerciseEntry> exerciseList = new ArrayList<>();
        String sql = "SELECT e.id, e.daily_log_id, e.name, e.duration, e.calories_burned, e.time_done, e.exercise_id, e.category " +
                "FROM exercise_entry e " +
                "JOIN daily_log d ON e.daily_log_id = d.id " +
                "WHERE d.user_id = ? AND d.log_date = ? " +
                "ORDER BY e.time_done DESC";

        LocalDate today = LocalDate.now();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, java.sql.Date.valueOf(today));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int dailyLogId = rs.getInt("daily_log_id");
                    String name = rs.getString("name");
                    int duration = rs.getInt("duration");
                    int caloriesBurned = rs.getInt("calories_burned");
                    Timestamp timeDone = rs.getTimestamp("time_done");
                    int exerciseId = rs.getInt("exercise_id");
                    String category = rs.getString("category");

                    ExerciseEntry entry = new ExerciseEntry(id, dailyLogId, name, duration, caloriesBurned, timeDone, exerciseId, category);
                    exerciseList.add(entry);
                }
            }
        } catch (SQLException e) {
            System.err.println("Egzersiz verileri yüklenirken hata oluştu:");
            e.printStackTrace();
        }

        return exerciseList;
    }

    // günlük özet yemekleri yükleme
    public List<MealEntry> loadTodaysMealEntriesForUser(int userId) {
        List<MealEntry> mealList = new ArrayList<>();
        String sql = "SELECT m.id, m.daily_log_id, m.name, m.calories, m.proteins, m.carbs, m.fats, " +
                "m.servings, m.time_eaten, m.meal_id, m.category " +
                "FROM meal_entry m " +
                "JOIN daily_log d ON m.daily_log_id = d.id " +
                "WHERE d.user_id = ? AND d.log_date = ? " +
                "ORDER BY m.time_eaten DESC";

        LocalDate today = LocalDate.now();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, java.sql.Date.valueOf(today));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    double calories = rs.getDouble("calories");
                    double proteins = rs.getDouble("proteins");
                    double carbs = rs.getDouble("carbs");
                    double fats = rs.getDouble("fats");
                    int servings = rs.getInt("servings");
                    String category = rs.getString("category");
                    String timeEaten = rs.getString("time_eaten");
                    int mealId = rs.getInt("meal_id");

                    MealEntry entry = new MealEntry(id, name, calories, proteins, carbs, fats, servings, category, timeEaten, mealId);
                    mealList.add(entry);
                }
            }
        } catch (SQLException e) {
            System.err.println("Yemek verileri yüklenirken hata oluştu:");
            e.printStackTrace();
        }

        return mealList;
    }

    public boolean deleteExerciseEntry(int id) {
        // Burada DB bağlantısı ve sorgu işlemi yapılıyor
        try {
            String sql = "DELETE FROM exercise_entry WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMealEntry(int id) {
        try {
            String sql = "DELETE FROM meal_entry WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // grafik için alınan, yakılan kalori ve egzersiz süresi dönerler
    public Map<LocalDate, Integer> getCaloriesConsumedByDate(int userId, LocalDate start, LocalDate end) {
        Map<LocalDate, Integer> result = new HashMap<>();
        String sql = "SELECT dl.log_date, SUM(me.calories) as total_calories FROM meal_entry me " +
                "JOIN daily_log dl ON me.daily_log_id = dl.id " +
                "WHERE dl.user_id = ? AND dl.log_date BETWEEN ? AND ? GROUP BY dl.log_date";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, java.sql.Date.valueOf(start));
            pstmt.setDate(3, java.sql.Date.valueOf(end));

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Date sqlDate = rs.getDate("log_date");
                LocalDate date = sqlDate.toLocalDate();
                int calories = rs.getInt("total_calories");
                result.put(date, calories);
            }
        } catch (SQLException e) {
            System.out.println("Error in getCaloriesConsumedByDate: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public Map<LocalDate, Integer> getCaloriesBurnedByDate(int userId, LocalDate start, LocalDate end) {
        Map<LocalDate, Integer> result = new HashMap<>();
        String sql = "SELECT dl.log_date, SUM(ex.calories_burned) as total_burned FROM exercise_entry ex " +
                "JOIN daily_log dl ON ex.daily_log_id = dl.id " +
                "WHERE dl.user_id = ? AND dl.log_date BETWEEN ? AND ? GROUP BY dl.log_date";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setLong(2, start.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            pstmt.setLong(3, end.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                long logDateMillis = rs.getLong("log_date");
                LocalDate date = Instant.ofEpochMilli(logDateMillis).atZone(ZoneId.systemDefault()).toLocalDate();
                int caloriesBurned = rs.getInt("total_burned");
                result.put(date, caloriesBurned);
            }
        } catch (SQLException e) {
            System.out.println("Error in getCaloriesBurnedByDate: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public Map<LocalDate, Integer> getExerciseDurationByDate(int userId, LocalDate start, LocalDate end) {
        Map<LocalDate, Integer> result = new HashMap<>();
        String sql = "SELECT dl.log_date, SUM(ex.duration) as total_duration FROM exercise_entry ex " +
                "JOIN daily_log dl ON ex.daily_log_id = dl.id " +
                "WHERE dl.user_id = ? AND dl.log_date BETWEEN ? AND ? GROUP BY dl.log_date";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setLong(2, start.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
            pstmt.setLong(3, end.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                long logDateMillis = rs.getLong("log_date");
                LocalDate date = Instant.ofEpochMilli(logDateMillis).atZone(ZoneId.systemDefault()).toLocalDate();
                int caloriesBurned = rs.getInt("total_duration");
                result.put(date, caloriesBurned);
            }
        } catch (SQLException e) {
            System.out.println("Error in getExerciseDurationByDate: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    public LocalDate getFirstEntryDate(int userId) {
        // Veritabanından en eski tarihi döndür (örnek için sabit tarih)
        return LocalDate.of(2024, 1, 1);
    }
}
