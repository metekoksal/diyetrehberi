package diyetrehberi.diyetrehberi;

import java.sql.Time;

public class MealEntry implements Trackable {
    private int id;
    private String name;
    private double calories, proteins, fats, carbs;
    private int servingSize;
    private Time timeEaten;


    // Time meal was eaten
    public Time getTime() {
        return timeEaten;
    }

    // Calories Taken
    public double calculateCalories() {  // kalori hesabı yapılacak
        return calories;
    }

    // Constructor
    public MealEntry(int id, String name, double calories, double proteins, double fats, double carbs) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbs = carbs;
    }

    // Getters & Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public double getProteins() {
        return proteins;
    }

    public void setProteins(float proteins) {
        this.proteins = proteins;
    }

    public double getFats() {
        return fats;
    }

    public void setFats(float fats) {
        this.fats = fats;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }

    public Time getTimeEaten() {
        return timeEaten;
    }

    public void setTimeEaten(Time timeEaten) {
        this.timeEaten = timeEaten;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServingSize() {
        return servingSize;
    }

    public void setServingSize(int servingSize) {
        this.servingSize = servingSize;
    }
}
