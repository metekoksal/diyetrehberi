package diyetrehberi.diyetrehberi;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private VBox homePanel, exercisePanel, foodPanel, profilePanel, settingsPanel;

    @FXML
    private Label nameLabel, userNameLabel, ageLabel, genderLabel, weightLabel, heightLabel;
    @FXML
    private TextField weightField, heightField;

    // Yeni: Yemek Paneli Bileşenleri
    @FXML
    private ComboBox<String> foodComboBox, exerciseComboBox;
    @FXML
    private TextField portionField, durationField;
    @FXML
    private Label caloriesLabel, carbsLabel, proteinLabel, fatLabel, caloriesBurnedLabel;

    // Yemek verileri için harita
    private Map<String, FoodItem> foodMap = new HashMap<>();
    private Map<String, ExerciseItem> exerciseMap = new HashMap<>();

    private Database db;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        db = Database.getInstance();
        fetchUserDetailsFromDatabase();
        loadFoodsFromDatabase();
        loadExercisesFromDatabase(); // <-- Eklendi
        setupFoodComboBoxListener();
        setupExerciseComboBoxListener(); // <-- Eklendi
        handleHome();


    }


    private void fetchUserDetailsFromDatabase() {
        int userId = db.getCurrentUserId();
        User user = db.getUserDetails(userId);

        if (user != null) {
            userNameLabel.setText("Hoşgeldin " + user.getName());
            ageLabel.setText("Yaş: " + user.getAge());
            genderLabel.setText("Cinsiyet: " + user.getGender());
            weightLabel.setText(user.getWeight() + " kg");
            heightLabel.setText(user.getHeight() + " cm");
        }
    }

    private void hideAllPanels() {
        homePanel.setVisible(false);
        exercisePanel.setVisible(false);
        foodPanel.setVisible(false);
        profilePanel.setVisible(false);
        settingsPanel.setVisible(false);
    }

    @FXML
    private void handleHome() {
        hideAllPanels();
        homePanel.setVisible(true);
    }

    @FXML
    private void handleAddExercise() {
        hideAllPanels();
        exercisePanel.setVisible(true);
    }

    @FXML
    private void handleAddFood() {
        hideAllPanels();
        foodPanel.setVisible(true);
    }

    @FXML
    private void handleProfile() {
        hideAllPanels();
        profilePanel.setVisible(true);
    }

    @FXML
    private void handleSettings() {
        hideAllPanels();
        settingsPanel.setVisible(true);
    }

    @FXML
    private void handleEditWeight() {
        weightField.setText(weightLabel.getText().replace(" kg", ""));
        weightField.setVisible(true);
        weightLabel.setVisible(false);
        weightField.requestFocus();

        weightField.setOnAction(event -> {
            try {
                double newWeight = Double.parseDouble(weightField.getText());
                if (newWeight > 0) {
                    Database db = Database.getInstance();
                    int userId = db.getCurrentUserId();
                    db.updateUserWeight(userId, newWeight);
                    weightLabel.setText(newWeight + " kg");
                    weightLabel.setVisible(true);
                    weightField.setVisible(false);
                } else {
                    showError("Geçersiz ağırlık değeri.");
                }
            } catch (NumberFormatException e) {
                showError("Lütfen geçerli bir sayı girin.");
            }
        });
    }

    @FXML
    private void handleEditHeight() {
        heightField.setText(heightLabel.getText().replace(" cm", ""));
        heightField.setVisible(true);
        heightLabel.setVisible(false);
        heightField.requestFocus();

        heightField.setOnAction(event -> {
            try {
                double newHeight = Double.parseDouble(heightField.getText());
                if (newHeight > 0) {
                    Database db = Database.getInstance();
                    int userId = db.getCurrentUserId();
                    db.updateUserHeight(userId, newHeight);
                    heightLabel.setText(newHeight + " cm");
                    heightLabel.setVisible(true);
                    heightField.setVisible(false);
                } else {
                    showError("Geçersiz boy değeri.");
                }
            } catch (NumberFormatException e) {
                showError("Lütfen geçerli bir sayı girin.");
            }
        });
    }

    @FXML
    private void handleRemoveUser() {
        Database db = Database.getInstance();
        int userId = db.getCurrentUserId();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Kullanıcı Silme");
        alert.setHeaderText("Kullanıcıyı silmek üzeresiniz");
        alert.setContentText("Bu işlemi onaylıyorsanız, 'OK' butonuna tıklayın.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                db.removeUser(userId);
                db.setCurrentUserId(-1);

                Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
                confirmationAlert.setTitle("Başarılı");
                confirmationAlert.setHeaderText("Kullanıcı başarıyla silindi");
                confirmationAlert.setContentText("Yeni bir kullanıcı seçmelisiniz.");
                confirmationAlert.showAndWait();

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/selectuser.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    Stage stage = (Stage) settingsPanel.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showError("Kullanıcıyı silerken hata oluştu.");
                }
            }
        });
    }

    private void loadFoodsFromDatabase() {
        Database db = Database.getInstance();
        foodMap = db.loadFoodsFromDatabase();
        foodComboBox.getItems().addAll(foodMap.keySet());
    }

    private void setupFoodComboBoxListener() {
        foodComboBox.setOnAction(event -> updateNutritionalValues());
        portionField.setOnAction(event -> updateNutritionalValues());
    }

    private void updateNutritionalValues() {
        String selected = foodComboBox.getValue();
        if (selected == null || !foodMap.containsKey(selected)) return;

        FoodItem item = foodMap.get(selected);

        double portion = 1.0;
        try {
            String portionText = portionField.getText();
            if (!portionText.isBlank()) {
                portion = Double.parseDouble(portionText);
            }
        } catch (NumberFormatException e) {
            portion = 1.0;
        }

        caloriesLabel.setText("Kalori: " + round(item.kalori * portion) + " kcal");
        carbsLabel.setText("Karbonhidrat: " + round(item.karbonhidrat * portion) + " g");
        proteinLabel.setText("Protein: " + round(item.protein * portion) + " g");
        fatLabel.setText("Yağ: " + round(item.yag * portion) + " g");
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }


    private void loadExercisesFromDatabase() {
        Database db = Database.getInstance();
        exerciseMap = db.loadExercisesFromDatabase();
        exerciseComboBox.getItems().addAll(exerciseMap.keySet());
    }

    private void setupExerciseComboBoxListener() {
        exerciseComboBox.setOnAction(event -> {
            String selected = exerciseComboBox.getValue();
            if (selected != null && exerciseMap.containsKey(selected)) {
                ExerciseItem item = exerciseMap.get(selected);
                System.out.println("Seçilen Egzersiz: " + item.getName() +
                        ", Kategori: " + item.getCategory() +
                        ", Kalori/dk: " + item.getCaloriesBurnedPerMinute());

                // Dakika girilmişse yakılan kaloriyi hesapla ve göster
                updateCaloriesBurned();
            }
        });

        // Kullanıcı süre girdikçe yakılan kaloriyi güncelle
        durationField.setOnAction(event -> updateCaloriesBurned());
        durationField.textProperty().addListener((obs, oldVal, newVal) -> updateCaloriesBurned());
    }
    private void updateCaloriesBurned() {
        String selectedExercise = exerciseComboBox.getValue();
        if (selectedExercise == null || !exerciseMap.containsKey(selectedExercise)) return;

        ExerciseItem item = exerciseMap.get(selectedExercise);
        double duration = 0;

        try {
            String durationText = durationField.getText();
            if (!durationText.isBlank()) {
                duration = Double.parseDouble(durationText);
            }
        } catch (NumberFormatException e) {
            duration = 0;
        }

        double totalCalories = item.getCaloriesBurnedPerMinute() * duration;
        caloriesBurnedLabel.setText("Yakılan Kalori: " + round(totalCalories) + " kcal");
    }
    // Hata mesajı göster
    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}




