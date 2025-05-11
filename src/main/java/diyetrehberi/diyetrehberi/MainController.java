package diyetrehberi.diyetrehberi;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Label nameLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fetchUserDetailsFromDatabase(); // Automatically call the method when the controller is initialized
    }

    private void fetchUserDetailsFromDatabase() {
        // Get the current user ID from the database
        Database db = Database.getInstance();
        int userId = db.getCurrentUserId();

        User user = db.getUserDetails(userId);

        if (user != null) {
            nameLabel.setText("Hoşgeldin " + user.getName());
        }
    }
}
