package lab10;

import lab10.controller.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        MainController controller = new MainController();

        Scene scene = new Scene(controller.getView(), 750, 450);

        stage.setTitle("Система обліку проєктів");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
