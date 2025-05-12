package diyetrehberi.diyetrehberi;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;


//db'ye kaydedilen her şey için log manager
//logMeal tamamlanmadı, veritabanından veri çekmesi sağlanacak - main methodunda örnek entry bulunmakta
//logExercise yazılacak

public class DailyLogManager {

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

    public void logMeal(int dailyLogId, MealEntry meal, Time eatenTime) throws SQLException {
        Connection connection = Database.getInstance().getConnection();
        String sql = "INSERT INTO meal_entry " +
                "(daily_log_id, meal_id, name, calories, protein, carbs, fat, time_eaten) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, dailyLogId);
            pstmt.setInt(2, meal.getId());  //------Foreign key to meals table
            pstmt.setString(3, meal.getName());
            pstmt.setDouble(4, meal.getCalories());
            pstmt.setDouble(5, meal.getProteins());
            pstmt.setDouble(6, meal.getCarbs());
            pstmt.setDouble(7, meal.getFats());
            pstmt.setTime(8, eatenTime);

            pstmt.executeUpdate();
        }
    }

    public static void main(String[] args) {
        try {
            DailyLogManager logManager = new DailyLogManager();
            int userId = 1;
            LocalDate today = LocalDate.now();

            // Create daily log
            int dailyLogId = logManager.createDailyLog(userId, today);

            // Log a meal
            MealEntry meal = new MealEntry(
                    1, "Sa", 5, 6, 7, 8
            );

            // Create Time object using milliseconds since midnight
            LocalTime localTime = LocalTime.of(7, 30);



            // Log meal at a specific time
            logManager.logMeal(dailyLogId, meal, Time.valueOf(localTime));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
