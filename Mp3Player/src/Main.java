import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("JavaFX MP3 Player Starting...");
        StackPane root = new StackPane(label);

        Scene scene = new Scene(root, 400, 200);
        stage.setScene(scene);
        stage.setTitle("MP3 Player");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
