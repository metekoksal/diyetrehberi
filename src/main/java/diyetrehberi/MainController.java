package diyetrehberi;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class MainController implements Initializable {

    @FXML
    private VBox homePanel, exercisePanel, foodPanel, profilePanel, settingsPanel;

    @FXML
    private Label nameLabel, userNameLabel, ageLabel, genderLabel, weightLabel, heightLabel,
            caloriesLabel, carbsLabel, proteinLabel, fatLabel, caloriesBurnedLabel, yAxisLabel;

    @FXML
    private TextField weightField,ageField, heightField, portionField, durationField;

    @FXML
    private ComboBox<String> foodComboBox, exerciseComboBox,
            hourComboBoxMeal, minuteComboBoxMeal, hourComboBoxExercise, minuteComboBoxExercise,
            calorieTypeComboBox, dateRangeComboBox;

    @FXML
    private VBox exerciseSummaryBox, mealSummaryBox;

    @FXML
    private LineChart<String, Number> calorieChart;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private CategoryAxis xAxis;

    private boolean weightFieldActionSet = false;
    private boolean heightFieldActionSet = false;
    private boolean ageFieldActionSet = false;


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
        populateTimeComboBoxes();
        updateExerciseSummaryBox();
        updateMealSummaryBox();

        calorieTypeComboBox.getItems().addAll("Alınan Kalori", "Yakılan Kalori", "Egzersiz Süresi");
        calorieTypeComboBox.setValue("Alınan Kalori");

        dateRangeComboBox.getItems().addAll("Son 1 Hafta", "Son 1 Ay", "Son 3 Ay", "Bu Yıl", "Tüm Zamanlar");
        dateRangeComboBox.setValue("Son 1 Hafta");

        // Event listener'lar
        calorieTypeComboBox.setOnAction(e -> updateChart());
        dateRangeComboBox.setOnAction(e -> updateChart());

        // Başlangıçta çiz
        updateChart();
        //home için chartın oluşturulması gerekiyor ondan aşağıya aldım
        handleHome();
    }

    private void fetchUserDetailsFromDatabase() {
        int userId = db.getCurrentUserId();
        User user = db.getUserDetails(userId);

        if (user != null) {
            userNameLabel.setText("Hoşgeldin " + user.getName());
            ageLabel.setText("" + user.getAge());
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
        updateChart();
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
        weightField.setManaged(true);
        weightLabel.setVisible(false);
        weightLabel.setManaged(false);
        weightField.requestFocus();

        if (!weightFieldActionSet) {
            weightField.setOnAction(event -> {
                try {
                    double newWeight = Double.parseDouble(weightField.getText());
                    if (newWeight > 0) {
                        Database db = Database.getInstance();
                        int userId = db.getCurrentUserId();
                        db.updateUserWeight(userId, newWeight);
                        weightLabel.setText(newWeight + " kg");

                        // TextField gizle, label göster
                        weightField.setVisible(false);
                        weightField.setManaged(false);
                        weightLabel.setVisible(true);
                        weightLabel.setManaged(true);
                        weightField.clear();
                    } else {
                        showError("Geçersiz ağırlık değeri.");
                    }
                } catch (NumberFormatException e) {
                    showError("Lütfen geçerli bir sayı girin.");
                }
            });
            weightFieldActionSet = true;
        }
    }

    @FXML
    private void handleEditHeight() {
        heightField.setText(heightLabel.getText().replace(" cm", ""));
        heightField.setVisible(true);
        heightField.setManaged(true);
        heightLabel.setVisible(false);
        heightLabel.setManaged(false);
        heightField.requestFocus();

        if (!heightFieldActionSet) {
            heightField.setOnAction(event -> {
                try {
                    double newHeight = Double.parseDouble(heightField.getText());
                    if (newHeight > 0) {
                        Database db = Database.getInstance();
                        int userId = db.getCurrentUserId();
                        db.updateUserHeight(userId, newHeight);
                        heightLabel.setText(newHeight + " cm");

                        // TextField gizle, label göster
                        heightField.setVisible(false);
                        heightField.setManaged(false);
                        heightLabel.setVisible(true);
                        heightLabel.setManaged(true);
                        heightField.clear();
                    } else {
                        showError("Geçersiz boy değeri.");
                    }
                } catch (NumberFormatException e) {
                    showError("Lütfen geçerli bir sayı girin.");
                }
            });
            heightFieldActionSet = true;
        }
    }

    @FXML
    private void handleEditAge() {
        ageField.setText(ageLabel.getText().replace("", ""));
        ageField.setVisible(true);
        ageField.setManaged(true);
        ageLabel.setVisible(false);
        ageLabel.setManaged(false);
        ageField.requestFocus();

        if (!ageFieldActionSet) {
            ageField.setOnAction(event -> {
                try {
                    Integer newHeight = Integer.parseInt(ageField.getText());
                    if (newHeight > 0) {
                        Database db = Database.getInstance();
                        int userId = db.getCurrentUserId();
                        db.updateUserAge(userId, newHeight);
                        ageLabel.setText(newHeight + "");

                        // TextField gizle, label göster
                        ageField.setVisible(false);
                        ageField.setManaged(false);
                        ageLabel.setVisible(true);
                        ageLabel.setManaged(true);
                        ageField.clear();
                    } else {
                        showError("Geçerli yıl değeri.");
                    }
                } catch (NumberFormatException e) {
                    showError("Lütfen geçerli bir sayı girin.");
                }
            });
            ageFieldActionSet = true;
        }
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

    @FXML
    private void handleLogExercise() {
        ExerciseItem item = exerciseMap.get(exerciseComboBox.getValue());

        int duration = 1;
        int errorFlag = 0;
        try{
            String durationText = durationField.getText();
            if(!durationText.isBlank()){
                duration = Math.abs(Integer.parseInt(durationText));
            }
        } catch(NumberFormatException e){
            System.out.println(e);
            showError("Lütfen geçerli bir sayı girin.");
            errorFlag = 1;
        }

        if(errorFlag == 0){
            try{
                ExerciseEntry exerciseentry = new ExerciseEntry(item.getExerciseId(), duration);

                int hourDone = Integer.parseInt(hourComboBoxExercise.getValue());
                int minuteDone = Integer.parseInt(minuteComboBoxExercise.getValue());

                DailyLogManager logManager = new DailyLogManager();
                int userId = db.getCurrentUserId();
                LocalDate today = LocalDate.now();
                int dailyLogId = logManager.createDailyLog(userId, today);
                long msSinceMidnight = (hourDone*60 +minuteDone)*60*1000;
                Time sqlTime = new Time(msSinceMidnight);
                // log exercise
                logManager.logExercise(dailyLogId, exerciseentry, sqlTime);
                updateExerciseSummaryBox();
            } catch (Exception e) {
                System.out.println(e);
            }
        }



    }

    @FXML
    private void handleLogMeal() {
        FoodItem item = foodMap.get(foodComboBox.getValue());

        //get portion text field value as int (number format exception - default to 1)
        int portion = 1;
        int errorFlag = 0;
        try {
            String portionText = portionField.getText();
            if (!portionText.isBlank()) {
                portion = Math.abs(Integer.parseInt(portionText));
            }
        } catch (NumberFormatException e) {
            System.out.println(e);
            showError("Lütfen geçerli bir sayı girin.");
            errorFlag = 1;
        }
        if(errorFlag == 0){
            try{
                MealEntry mealentry = new MealEntry(item.getFoodid(), portion);

                // set hour & minute from selected vaules in combo boxes
                int hourEaten = Integer.parseInt(hourComboBoxMeal.getValue());
                int minuteEaten = Integer.parseInt(minuteComboBoxMeal.getValue());

                DailyLogManager logManager = new DailyLogManager();
                int userId = db.getCurrentUserId();
                LocalDate today = LocalDate.now();
                int dailyLogId = logManager.createDailyLog(userId, today);
                long msSinceMidnight = (hourEaten*60 +minuteEaten)*60*1000;
                Time sqlTime = new Time(msSinceMidnight);
                //log meal
                logManager.logMeal(dailyLogId, mealentry, sqlTime);
                updateMealSummaryBox();
            } catch (Exception e){
                System.out.println(e);
            }
        }

    }

    // yemeğin kaçta yenildiğini ve egzersizin kaçta yapıldığını gösteren combo boxları doldurur
    // saatler ve dk'lar 00, 01... 24 diye gider (string, db'e kaydedilirken int'e çevrilir)
    private void populateTimeComboBoxes(){
        List<String> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d", i));
        }
        hourComboBoxMeal.setItems(FXCollections.observableArrayList(hours));
        hourComboBoxExercise.setItems(FXCollections.observableArrayList(hours));
        List<String> minutes = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            minutes.add(String.format("%02d", i));
        }
        minuteComboBoxExercise.setItems(FXCollections.observableArrayList(minutes));
        minuteComboBoxMeal.setItems(FXCollections.observableArrayList(minutes));
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
                // porsiyon miktarı mutlak olarak alınır (negatif girilemez)
                portion = Math.abs(Double.parseDouble(portionText));
            }
        } catch (NumberFormatException e) {
            portion = 1.0;
        }

        caloriesLabel.setText("Kalori: " + round(item.getKalori() * portion) + " kcal");
        carbsLabel.setText("Karbonhidrat: " + round(item.getKarbonhidrat() * portion) + " g");
        proteinLabel.setText("Protein: " + round(item.getProtein() * portion) + " g");
        fatLabel.setText("Yağ: " + round(item.getYag() * portion) + " g");
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
        String selected = exerciseComboBox.getValue();
        if (selected == null || !exerciseMap.containsKey(selected)) return;

        ExerciseItem item = exerciseMap.get(selected);
        int duration = 1;
        try {
            String durationText = durationField.getText();
            if (!durationText.isBlank()) {
                duration = Math.abs(Integer.parseInt(durationText));
            }
        } catch (NumberFormatException e) {
            duration = 0;
        }

        double calories = item.getCaloriesBurnedPerMinute() * duration;
        caloriesBurnedLabel.setText("Yakılan Kalori: " + round(calories) + " kcal");
    }

    public void updateExerciseSummaryBox() {
        List<ExerciseEntry> exerciseList = db.loadTodaysExerciseEntriesForUser(db.getCurrentUserId());
        exerciseSummaryBox.getChildren().clear();

        if (exerciseList.isEmpty()) {
            Label noDataLabel = new Label("Henüz egzersiz kaydı yok.");
            exerciseSummaryBox.getChildren().add(noDataLabel);
            return;
        }

        int totalDuration = 0;
        int totalCalories = 0;

        for (ExerciseEntry entry : exerciseList) {
            totalDuration += entry.getDuration();
            totalCalories += entry.getCalories();

            Label exerciseLabel = new Label(
                    entry.getName() + " - Süre: " + entry.getDuration() + " dk, Kalori: " + entry.getCalories() + " kcal"
            );

            Button deleteButton = new Button("X");
            deleteButton.setOnAction(e -> {
                boolean success = db.deleteExerciseEntry(entry.getId());
                if (success) {
                    updateExerciseSummaryBox();  // Güncelle
                } else {
                    System.out.println("Silme işlemi başarısız.");
                }
            });

            HBox row = new HBox(10, exerciseLabel, deleteButton);
            exerciseSummaryBox.getChildren().add(row);
        }

        Label totalLabel = new Label("Toplam Süre: " + totalDuration + " dk, Toplam Kalori: " + totalCalories + " kcal");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 10 0;");
        exerciseSummaryBox.getChildren().add(0, totalLabel);
    }

    public void updateMealSummaryBox() {
        List<MealEntry> mealList = db.loadTodaysMealEntriesForUser(db.getCurrentUserId());
        mealSummaryBox.getChildren().clear();

        if (mealList.isEmpty()) {
            Label noDataLabel = new Label("Henüz yemek kaydı yok.");
            mealSummaryBox.getChildren().add(noDataLabel);
            return;
        }

        double totalCalories = 0;
        double totalCarbs = 0;
        double totalProtein = 0;
        double totalFat = 0;

        for (MealEntry entry : mealList) {
            totalCalories += entry.getCalories();
            totalCarbs += entry.getCarbs();
            totalProtein += entry.getProteins();
            totalFat += entry.getFats();

            // Resim yükleme
            String imagePath = "/Assets/Images/"+entry.getMealId()+".png"; // resources klasöründen erişim için başa '/' koy
            Image image;
            try {
                image = new Image(getClass().getResourceAsStream(imagePath), 50, 50, true, true);
            } catch (Exception ex) {
                System.out.println("Resim yüklenemedi: " + imagePath);
                image = new Image(getClass().getResourceAsStream("/assets/images/default.png"), 50, 50, true, true);
            }
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setPreserveRatio(true);

            Rectangle clip = new Rectangle(50, 50);
            clip.setArcWidth(15);
            clip.setArcHeight(15);

            imageView.setClip(clip);



            // Etiket
            Label mealLabel = new Label(
                    entry.getName() +
                            " - Kalori: " + entry.getCalories() +
                            " kcal, KH: " + entry.getCarbs() +
                            " g, Protein: " + entry.getProteins() +
                            " g, Yağ: " + entry.getFats() + " g"
            );

            // Silme butonu
            Button deleteButton = new Button("X");
            deleteButton.setOnAction(e -> {
                boolean success = db.deleteMealEntry(entry.getId());
                if (success) {
                    updateMealSummaryBox();  // Güncelle
                } else {
                    System.out.println("Silme işlemi başarısız.");
                }
            });

            HBox row = new HBox(10, imageView, mealLabel, deleteButton);
            mealSummaryBox.getChildren().add(row);
        }

        Label totalLabel = new Label(
                "Toplam Kalori: " + totalCalories + " kcal, " +
                        "KH: " + totalCarbs + " g, Protein: " + totalProtein + " g, Yağ: " + totalFat + " g"
        );
        totalLabel.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 10 0;");
        mealSummaryBox.getChildren().add(0, totalLabel);
    }

    private void updateChart() {
        String selectedType = calorieTypeComboBox.getValue();
        String selectedRange = dateRangeComboBox.getValue();

        LocalDate[] range = getDateRange(selectedRange);
        LocalDate start = range[0];
        LocalDate end = range[1];

        yAxisLabel.setText(selectedType);
        loadChartByType(db.getCurrentUserId(), start, end, selectedType);
    }

    private LocalDate[] getDateRange(String rangeLabel) {
        LocalDate today = LocalDate.now();
        switch (rangeLabel) {
            case "Son 1 Hafta":
                return new LocalDate[]{ today.minusDays(6), today };
            case "Son 1 Ay":
                return new LocalDate[]{ today.minusMonths(1).plusDays(1), today };
            case "Son 3 Ay":
                return new LocalDate[]{ today.minusMonths(3).plusDays(1), today };
            case "Bu Yıl":
                return new LocalDate[]{ LocalDate.of(today.getYear(), 1, 1), today };
            case "Tüm Zamanlar":
                return new LocalDate[]{ db.getFirstEntryDate(db.getCurrentUserId()), today };
            default:
                return new LocalDate[]{ today.minusDays(6), today };
        }
    }

    public void loadChartByType(int userId, LocalDate start, LocalDate end, String type) {
        Map<LocalDate, Integer> data;
        String seriesName = "";

        switch (type) {
            case "Alınan Kalori":
                data = db.getCaloriesConsumedByDate(userId, start, end);
                yAxis.setLabel("Alınan Kalori (kcal)");
                seriesName = "Alınan Kalori";
                break;
            case "Yakılan Kalori":
                data = db.getCaloriesBurnedByDate(userId, start, end);
                yAxis.setLabel("Yakılan Kalori (kcal)");
                seriesName = "Yakılan Kalori";
                break;
            case "Egzersiz Süresi":
                data = db.getExerciseDurationByDate(userId, start, end);
                yAxis.setLabel("Egzersiz Süresi (dk)");
                seriesName = "Egzersiz Süresi";
                break;
            default:
                System.out.println("Geçersiz veri türü: " + type);
                return;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(seriesName);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate current = start;
        while (!current.isAfter(end)) {
            int value = data.getOrDefault(current, 0);
            String label = current.format(formatter);
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(label, value);
            series.getData().add(dataPoint);
            current = current.plusDays(1);
        }

        calorieChart.getData().clear();
        calorieChart.getData().add(series);

        // Tooltip ve grafik sonrası düzenlemeleri Platform.runLater ile yapıyor
        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> d : series.getData()) {
                Tooltip tooltip = new Tooltip(d.getXValue() + ": " + d.getYValue());
                tooltip.setShowDelay(Duration.millis(100));
                Tooltip.install(d.getNode(), tooltip);
            }

            // X eksenini ve diğer grafik parametrelerini sıfırlayıp düzenliyor
            xAxis.setCategories(FXCollections.observableArrayList());
            List<String> categories = new ArrayList<>();
            LocalDate temp = start;
            while (!temp.isAfter(end)) {
                categories.add(temp.format(formatter));
                temp = temp.plusDays(1);
            }
            try {
                xAxis.setCategories(FXCollections.observableArrayList(categories));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
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




