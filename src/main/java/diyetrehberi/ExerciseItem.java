package diyetrehberi;

public class ExerciseItem {
    private String name;
    private String category;
    private int exerciseId;
    private double caloriesBurnedPerMin;

    public ExerciseItem(String Name, String Category, double CaloriesBurnedPerMin, int ExerciseId) {
        this.name = Name;
        this.category = Category;
        this.caloriesBurnedPerMin = CaloriesBurnedPerMin;
        this.exerciseId = ExerciseId;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public double getCaloriesBurnedPerMin() {
        return caloriesBurnedPerMin;
    }

    public void setCaloriesBurnedPerMin(double caloriesBurnedPerMin) {
        this.caloriesBurnedPerMin = caloriesBurnedPerMin;
    }
}
