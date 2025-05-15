package diyetrehberi.diyetrehberi;

import java.sql.*;

public class ExerciseEntry extends Entry {
    private int duration;

    // Calories Burned (-)
    public double calculateCalories(){
        return -getCalories();
    }

    // Getters & Setters
    public int getDuration() {
        return duration;
    }
}
