package diyetrehberi.diyetrehberi;

import java.sql.*;
// verileri id'ye göre db'den çekecek ve obje oluşturacak

public class MealEntry extends Entry {
    private double proteins, fats, carbs;
    private int servings;

    // Calories Taken
    public double calculateCalories() {
        return servings * getCalories();
    }

    // ana constructor
    public MealEntry(int id, int servings) {
        super(id);
        this.servings = servings;
        try {
            loadMealDataFromDatabase(id, servings);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    private void loadMealDataFromDatabase(int id, int servings) throws SQLException {
        Connection connection = Database.getInstance().getConnection();

        String sql = "SELECT name, calories, proteins, carbs, fats, category FROM meals WHERE food_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    this.setName(rs.getString("name"));
                    this.setCalories(servings * rs.getDouble("calories"));
                    this.proteins = servings * rs.getDouble("proteins");
                    this.carbs = servings * rs.getDouble("carbs");
                    this.fats = servings * rs.getDouble("fats");
                    this.setCategory(rs.getString("category"));
                } else {
                    throw new SQLException("Meal with ID " + id + " not found");
                }
            }
        }
    }


    // Getters & Setters
    public double getProteins() {
        return proteins;
    }

    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }


    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }
}
