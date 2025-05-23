package diyetrehberi;

import java.sql.*;
import java.time.LocalDate;


//db'ye kaydedilen her şey için log manager

public class DailyLogManager {

    //varsa mevcut kullanıcının daily log'unun id'sini döner, yoksa createDailyLog çağırır
    private int getDailyLogId(int userId, LocalDate date) throws SQLException {
        Connection connection = Database.getInstance().getConnection();
        String sql = "SELECT id FROM daily_log WHERE user_id = ? AND log_date = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(date));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        // If no existing log is found, create a new one
        return createDailyLog(userId, date);
    }

    //user id ve bugün için daily log açmaya çalışır, zaten varsa getDailyLogId çağırır
    public int createDailyLog(int userId, LocalDate date) throws SQLException {
        Connection connection = Database.getInstance().getConnection();
        String sql = "INSERT INTO daily_log (user_id, log_date) VALUES (?, ?) " +
                "ON CONFLICT (user_id, log_date) DO NOTHING RETURNING id";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(date));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        // If insert fails (likely due to existing record), fetch the existing ID
        return getDailyLogId(userId, date);
    }

    //db'e yemek kaydı
    public void logMeal(int dailyLogId, MealEntry meal, Time eatenTime) throws SQLException {
        Connection connection = Database.getInstance().getConnection();
        String sql = "INSERT INTO meal_entry (daily_log_id, meal_id, name, calories, proteins, carbs, fats, time_eaten, servings, category) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, dailyLogId);
            pstmt.setInt(2, meal.getId());  //------Foreign key to meals table
            pstmt.setString(3, meal.getName());
            pstmt.setDouble(4, meal.getCalories());
            pstmt.setDouble(5, meal.getProteins());
            pstmt.setDouble(6, meal.getCarbs());
            pstmt.setDouble(7, meal.getFats());
            pstmt.setTime(8, eatenTime);
            pstmt.setInt(9, meal.getServings());
            pstmt.setString(10, meal.getCategory());

            pstmt.executeUpdate();
        }
    }

    //db'e egzersiz kaydı
    public void logExercise(int dailyLogId, ExerciseEntry exercise, Time timeDone) throws SQLException {
        Connection connection = Database.getInstance().getConnection();
        String sql = "INSERT INTO exercise_entry (daily_log_id, exercise_id, name, calories_burned, time_done, duration, category) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, dailyLogId);
            pstmt.setInt(2, exercise.getId());  // foreign key to exercise table
            pstmt.setString(3, exercise.getName());
            pstmt.setDouble(4, exercise.getCalories()); // calories burned
            pstmt.setTime(5, timeDone);
            pstmt.setInt(6, exercise.getDuration());
            pstmt.setString(7, exercise.getCategory());

            pstmt.executeUpdate();
        }
    }

}
