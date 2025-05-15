package diyetrehberi.diyetrehberi;

import java.sql.*;
// verileri id'ye göre db'den çekecek ve obje oluşturacak

public class MealEntry extends Entry {
    private double proteins, fats, carbs;
    private int servingSize;

    // Calories Taken
    public double calculateCalories() {
        return servingSize * getCalories();
    }

    // ana constructor
    public MealEntry(int id, int servingSize) {
        super(id);
        this.servingSize = servingSize;
        try {
            loadMealDataFromDatabase(id, servingSize);
        } catch(Exception e){
            System.out.println(e);
        }
    }

    private void loadMealDataFromDatabase(int id, int servingSize) throws SQLException {
        Connection connection = Database.getInstance().getConnection();

        String sql = "SELECT name, serving_size, calories, proteins, carbs, fats, category FROM meals WHERE food_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {

                    this.setName(rs.getString("name"));
                    this.setCalories(servingSize * rs.getDouble("calories"));
                    this.proteins = servingSize * rs.getDouble("proteins");
                    this.carbs = servingSize * rs.getDouble("carbs");
                    this.fats = servingSize * rs.getDouble("fats");
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


    public int getServingSize() {
        return servingSize;
    }

    public void setServingSize(int servingSize) {
        this.servingSize = servingSize;
    }
}
