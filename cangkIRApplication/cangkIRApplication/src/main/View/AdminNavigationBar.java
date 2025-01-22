package main.View;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AdminNavigationBar extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("cangkIR");

        // Create a BorderPane as the main layout
        BorderPane borderPane = new BorderPane();

        // Create a MenuBar
        MenuBar menuBar = new MenuBar();

        // Create a Menu for navigation options
        Menu navigationMenu = new Menu("Menu");

        // Create MenuItems for Home, Cart, and Log Out options
        MenuItem cupManagementItem = new MenuItem("Cup Management");
        MenuItem logoutMenuItem = new MenuItem("Log Out");

        // Add MenuItems to the Menu
        navigationMenu.getItems().addAll(cupManagementItem, logoutMenuItem);

        // Add the Menu to the MenuBar
        menuBar.getMenus().add(navigationMenu);

        // Set the MenuBar at the top of the BorderPane
        borderPane.setTop(menuBar);

        // Handle actions for each MenuItem
        cupManagementItem.setOnAction(e -> redirectToHomePage());
        logoutMenuItem.setOnAction(e -> redirectToLoginPage(primaryStage));

        // Set layout properties
        BorderPane.setAlignment(menuBar, Pos.CENTER);

        // Create a Scene with the BorderPane
        Scene scene = new Scene(borderPane, 1000, 1000);

        // Set the Scene to the Stage
        primaryStage.setScene(scene);

        // Show the Stage
        primaryStage.show();
    }

    private void redirectToHomePage() {
        // Implement logic to redirect to the Home Page
        System.out.println("Redirecting to Home Page");
    }

    private void redirectToLoginPage(Stage primaryStage) {
        RegisterForm registerForm = new RegisterForm();
        registerForm.showLoginForm();
        primaryStage.close();	
    }
}
