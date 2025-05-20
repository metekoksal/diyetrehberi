package diyetrehberi.diyetrehberi;

import java.sql.*;

public class ExerciseEntry extends Entry {
    private int duration;   // minutes
    private int dailyLogId;
    private Timestamp timeDone;
    private int exerciseId;

    // Calories Burned (-)
    public double calculateCalories() {
        return -getCalories();
    }

    // Constructor 1: Sadece ID ve duration ile, detayları DB'den çeker
    public ExerciseEntry(int id, int duration) {
        super(id);
        this.duration = duration;
        try {
            loadExerciseDataFromDatabase(id, duration);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Constructor 2: Tüm bilgileri dışarıdan alan yapı
    public ExerciseEntry(int id, int dailyLogId, String name, int duration, double caloriesBurned,
                         Timestamp timeDone, int exerciseId, String category) {
        super(id);
        this.dailyLogId = dailyLogId;
        this.duration = duration;
        this.setName(name);
        this.setCalories(caloriesBurned);
        this.timeDone = timeDone;
        this.exerciseId = exerciseId;
        this.setCategory(category);
    }

    private void loadExerciseDataFromDatabase(int id, int duration) throws SQLException {
        Connection connection = Database.getInstance().getConnection();
        String sql = "SELECT name, calories_burned_per_min, category FROM exercises WHERE exercise_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    this.setName(rs.getString("name"));
                    this.setCalories(duration * rs.getDouble("calories_burned_per_min"));
                    this.setCategory(rs.getString("category"));
                } else {
                    throw new SQLException("Exercise with ID " + id + " not found");
                }
            }
        }
    }

    // Getters & Setters
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDailyLogId() {
        return dailyLogId;
    }

    public void setDailyLogId(int dailyLogId) {
        this.dailyLogId = dailyLogId;
    }

    public Timestamp getTimeDone() {
        return timeDone;
    }

    public void setTimeDone(Timestamp timeDone) {
        this.timeDone = timeDone;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }
}
