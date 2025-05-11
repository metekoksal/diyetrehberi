package diyetrehberi.diyetrehberi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Database db = Database.getInstance();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/SelectUser.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Diyet Rehberi");

        // İkonu ayarlamak
        stage.getIcons().add(new javafx.scene.image.Image(Objects.requireNonNull(getClass().getResource("/Assets/icon.png")).toExternalForm()));

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
