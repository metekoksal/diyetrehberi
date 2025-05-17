package diyetrehberi.diyetrehberi;

import java.sql.*;

public class ExerciseEntry extends Entry {
    private int duration;   //minutes

    // Calories Burned (-)
    public double calculateCalories(){
        return -getCalories();
    }

    public ExerciseEntry(int id, int duration) {
        super(id);
        this.duration = duration;
        try {
            loadExerciseDataFromDatabase(id, duration);
        } catch(Exception e){
            System.out.println(e);
        }
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
