package main.Application;


import javafx.application.Application;
import javafx.stage.Stage;
import main.View.LoginForm;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        LoginForm loginForm = new LoginForm();
        loginForm.start(primaryStage);
    }
}
