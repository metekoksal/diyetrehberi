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
    private TextField emailField;


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
            String email = emailField.getText().trim();

            if (email.isEmpty()) {
                statusLabel.setText("Lütfen tüm alanları doldurun.");
                return;
            }

            if (!isValidEmail(email)) {
                statusLabel.setText("Lütfen geçerli bir e-posta adresi girin.");
                return;
            }


            Database db = Database.getInstance();
            db.createUser(name, age, gender, email, height, weight);
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

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"; // regex kullanımı
        return email.matches(emailRegex);
    }

}
