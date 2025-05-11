package diyetrehberi.diyetrehberi;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class SelectUserController implements Initializable {

    @FXML
    private FlowPane userContainer;

    @FXML
    private AnchorPane rootPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUsers();
    }

    private void loadUsers() {
        Connection conn = Database.getInstance().getConnection();
        String sql = "SELECT id, name FROM users";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("id");
                String name = rs.getString("name");

                Button userButton = new Button(name);
                userButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
                userButton.setOnAction(e -> {
                    Database.getInstance().setCurrentUserId(userId);
                    System.out.println("Giriş yapıldı: " + name + " (ID: " + userId + ")");
                    goToMain();
                });

                userContainer.getChildren().add(userButton);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Yeni Kullanıcı butonu
        Button newUserButton = new Button("Yeni Kullanıcı");
        newUserButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
        newUserButton.setOnAction(e -> goToSignup());

        userContainer.getChildren().add(newUserButton);
    }

    private void goToSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/signup.fxml"));
            AnchorPane signupPane = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(signupPane));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void goToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
            AnchorPane mainPane = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(mainPane));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
