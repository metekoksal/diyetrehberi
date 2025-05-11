package diyetrehberi.diyetrehberi;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private VBox homePanel, exercisePanel, foodPanel, profilePanel, settingsPanel;

    @FXML
    private Label nameLabel, userNameLabel, ageLabel, genderLabel, weightLabel, heightLabel;

    @FXML
    private TextField weightField, heightField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fetchUserDetailsFromDatabase();
        handleHome(); // Uygulama başladığında Ana Sayfa panelini göster
    }

    private void fetchUserDetailsFromDatabase() {
        Database db = Database.getInstance();
        int userId = db.getCurrentUserId();
        User user = db.getUserDetails(userId);

        if (user != null) {
            userNameLabel.setText("Hoşgeldin " + user.getName());
            ageLabel.setText("Yaş: " + user.getAge());
            genderLabel.setText("Cinsiyet: " + user.getGender());
            weightLabel.setText(+ user.getWeight() + " kg");
            heightLabel.setText(+ user.getHeight() + " cm");
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

    // Boyu düzenleme işlemi
    @FXML
    private void handleEditWeight() {
        // Only set the numeric part from the label into the TextField
        weightField.setText(weightLabel.getText().replace(" kg", "")); // Remove " kg"
        weightField.setVisible(true);
        weightLabel.setVisible(false);  // Hide the label when editing

        // Focus on TextField for easy editing
        weightField.requestFocus();

        // Handle "Enter" key press to update the weight
        weightField.setOnAction(event -> {
            try {
                double newWeight = Double.parseDouble(weightField.getText());
                if (newWeight > 0) {
                    // Update the database (if needed)
                    Database db = Database.getInstance();
                    int userId = db.getCurrentUserId();
                    db.updateUserWeight(userId, newWeight);

                    // Update the label with the new value, only the numeric part and add "kg"
                    weightLabel.setText(newWeight + " kg");
                    weightLabel.setVisible(true);  // Show the label again
                    weightField.setVisible(false); // Hide the TextField after editing
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
        // Only set the numeric part from the label into the TextField
        heightField.setText(heightLabel.getText().replace(" cm", "")); // Remove " cm"
        heightField.setVisible(true);
        heightLabel.setVisible(false);  // Hide the label when editing

        // Focus on TextField for easy editing
        heightField.requestFocus();

        // Handle "Enter" key press to update the height
        heightField.setOnAction(event -> {
            try {
                double newHeight = Double.parseDouble(heightField.getText());
                if (newHeight > 0) {
                    // Update the database (if needed)
                    Database db = Database.getInstance();
                    int userId = db.getCurrentUserId();
                    db.updateUserHeight(userId, newHeight);

                    // Update the label with the new value, only the numeric part and add "cm"
                    heightLabel.setText(newHeight + " cm");
                    heightLabel.setVisible(true);  // Show the label again
                    heightField.setVisible(false); // Hide the TextField after editing
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

        // Confirm before removing the user
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Kullanıcı Silme");
        alert.setHeaderText("Kullanıcıyı silmek üzeresiniz");
        alert.setContentText("Bu işlemi onaylıyorsanız, 'OK' butonuna tıklayın.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Remove the user from the database
                db.removeUser(userId);

                // Clear the current user ID
                db.setCurrentUserId(-1);

                // Show a confirmation message
                Alert confirmationAlert = new Alert(Alert.AlertType.INFORMATION);
                confirmationAlert.setTitle("Başarılı");
                confirmationAlert.setHeaderText("Kullanıcı başarıyla silindi");
                confirmationAlert.setContentText("Yeni bir kullanıcı seçmelisiniz.");
                confirmationAlert.showAndWait();

                // Load the selectuser.fxml to allow the user to select a new user
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




    // Hata mesajı göster
    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
