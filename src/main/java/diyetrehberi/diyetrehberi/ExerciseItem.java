package diyetrehberi.diyetrehberi;

public class ExerciseItem {
    private String name;
    private String category; // in minutes
    private double caloriesBurnedPerMin;

    public ExerciseItem(String Name, String Category, double CaloriesBurnedPerMin) {
        this.name = Name;
        this.category = Category;
        this.caloriesBurnedPerMin = CaloriesBurnedPerMin;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getCaloriesBurnedPerMinute() {
        return caloriesBurnedPerMin;
    }
}
