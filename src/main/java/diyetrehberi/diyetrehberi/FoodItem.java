package diyetrehberi.diyetrehberi;

public class FoodItem {
    String name;
    double porsiyon, kalori, protein, karbonhidrat, yag;

    public FoodItem(String name, double porsiyon, double kalori, double protein, double karbonhidrat, double yag) {
        this.name = name;
        this.porsiyon = porsiyon;
        this.kalori = kalori;
        this.protein = protein;
        this.karbonhidrat = karbonhidrat;
        this.yag = yag;
    }
}