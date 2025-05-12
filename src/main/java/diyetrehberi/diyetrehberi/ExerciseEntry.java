package diyetrehberi.diyetrehberi;

import java.sql.Time;

public class ExerciseEntry implements Trackable {
    private int id;
    private String name;
    private int duration;
    private double calories;
    private Time timeDone;

    // Time exercise was done
    public Time getTime(){
        return timeDone;
    }
    // Calories Burned (-)
    public double calculateCalories(){
        return -calories;
    }

    // Constructor
    public ExerciseEntry(int id, String name, int duration, double calories, Time timeDone) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.calories = calories;
        this.timeDone = timeDone;
    }

    // Getters & Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public Time getTimeDone() {
        return timeDone;
    }

    public void setTimeDone(Time timeDone) {
        this.timeDone = timeDone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
