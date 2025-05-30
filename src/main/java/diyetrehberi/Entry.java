package diyetrehberi;

public abstract class Entry implements Trackable{

    // overriden in Exercise & Meal Entry
    public double calculateCalories(){
        return calories;
    }
    private int id; //item id
    private int userID;
    private String name;
    private double calories;
    private String category;

    //boş constructor
    public Entry() {

    }

    //id constructor
    public Entry(int id) {
        this.id = id;
    }

    //debug constructor
    public Entry(int id, int userID, String name, double calories) {
        this.id = id;
        this.userID = userID;
        this.name = name;
        this.calories = calories;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
