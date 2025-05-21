package diyetrehberi;

public class FoodItem {
    private String name, kategori;
    private int foodid;
    private double porsiyon, kalori, protein, karbonhidrat, yag;

    public FoodItem(String name, double porsiyon, double kalori, double protein, double karbonhidrat, double yag, String category, int foodid) {
        this.name = name;
        this.porsiyon = porsiyon;
        this.kalori = kalori;
        this.protein = protein;
        this.karbonhidrat = karbonhidrat;
        this.yag = yag;
        this.kategori = category;
        this.foodid = foodid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFoodid() {
        return foodid;
    }

    public void setFoodid(int foodid) {
        this.foodid = foodid;
    }

    public double getPorsiyon() {
        return porsiyon;
    }

    public void setPorsiyon(double porsiyon) {
        this.porsiyon = porsiyon;
    }

    public double getKalori() {
        return kalori;
    }

    public void setKalori(double kalori) {
        this.kalori = kalori;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getKarbonhidrat() {
        return karbonhidrat;
    }

    public void setKarbonhidrat(double karbonhidrat) {
        this.karbonhidrat = karbonhidrat;
    }

    public double getYag() {
        return yag;
    }

    public void setYag(double yag) {
        this.yag = yag;
    }
}