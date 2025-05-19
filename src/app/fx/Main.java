package app.fx;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        AppManager app = new AppManager();
        app.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
