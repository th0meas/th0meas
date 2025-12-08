package musicplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // 1. Create the loader
        // Make sure the filename matches EXACTLY what you named it in resources
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/MainView.fxml"));

        // 2. Load the FXML (This turns your Scene Builder design into real Java objects)
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        // 3. Add your CSS (Optional, but good to ensure it loads)
        String css = this.getClass().getResource("/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        // 4. Show the window
        stage.setTitle("My MP3 Player");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
