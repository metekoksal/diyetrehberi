package diyetrehberi.diyetrehberi;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;


//db'ye kaydedilen her şey için log manager

//kategori için sql insert eklenecek
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
        String sql = "INSERT INTO meal_entry (daily_log_id, meal_id, name, calories, proteins, carbs, fats, time_eaten, serving_size) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, dailyLogId);
            pstmt.setInt(2, meal.getId());  //------Foreign key to meals table
            pstmt.setString(3, meal.getName());
            pstmt.setDouble(4, meal.getCalories());
            pstmt.setDouble(5, meal.getProteins());
            pstmt.setDouble(6, meal.getCarbs());
            pstmt.setDouble(7, meal.getFats());
            pstmt.setTime(8, eatenTime);
            pstmt.setInt(9, meal.getServingSize());

            pstmt.executeUpdate();
        }
    }

    // TEST MAIN'İ
    // ilk user id belirlenir, daily log oluşturulur, örneğin 4 id'li yemekten 2 porsiyon yemek saat 10:30 için db'e eklenir
    public static void main(String[] args) {
        try {
            DailyLogManager logManager = new DailyLogManager();
            int userId = 2;

            //for daily log time (today)
            LocalDate today = LocalDate.now();
            // creates daily log
            int dailyLogId = logManager.createDailyLog(userId, today);

            MealEntry meal = new MealEntry(4, 1);

            int hour = 9, minute = 30;
            long msSinceMidnight = (hour*60+minute)*60*1000;

            // sql.Time unix time'dan beri geçen ms gösteriyor (Time türünde), saat için değer doğrudan alınabilir
            Time sqlTime = new Time(msSinceMidnight);

            // log meal at a specific time on daily log dailyLogId
            logManager.logMeal(dailyLogId, meal, sqlTime);

            //debug print
            System.out.println(logManager.getDailyLogId(userId, today) + "id'li günlük log'a kayıt eklendi. ID:" + meal.getId());

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
