package diyetrehberi.diyetrehberi;

import java.sql.Time;

// Loglanabilen classlar için interface
public interface Trackable {
    Time getTime();    // Time meal was eaten or time exercise was done
    double calculateCalories();  // Calories eaten or burned
}
