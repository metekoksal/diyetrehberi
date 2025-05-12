package diyetrehberi.diyetrehberi;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SignupController {

    // Injecting the VBox
    @FXML
    private VBox vbox;

    @FXML
    private TextField nameField;

    @FXML
    private TextField ageField;

    @FXML
    private ChoiceBox<String> genderChoice;

    @FXML
    private TextField heightField;

    @FXML
    private TextField weightField;

    @FXML
    private Label statusLabel;

    @FXML
    public void onSignupClicked() {
        try {
            String name = nameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String gender = genderChoice.getValue();
            double height = Double.parseDouble(heightField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());

            if (name.isEmpty() || gender == null) {
                statusLabel.setText("Lütfen tüm alanları doldurun.");
                return;
            }

            Database db = Database.getInstance();
            db.createUser(name, age, gender, height, weight);  // Save user and ID to the database
            statusLabel.setText("Kayıt başarılı!");

            // Load the new scene after sign-up
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
            AnchorPane newRoot = loader.load();  // Load as AnchorPane instead of VBox
            Scene newScene = new Scene(newRoot, 600, 400);

            // Get the stage and set the new scene
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(newScene);
            stage.setTitle("Ana Sayfa");
            stage.show();

        } catch (NumberFormatException e) {
            statusLabel.setText("Lütfen geçerli sayısal değerler girin.");
        } catch (Exception e) {
            statusLabel.setText("Bir hata oluştu.");
            e.printStackTrace();
        }
    }
}
