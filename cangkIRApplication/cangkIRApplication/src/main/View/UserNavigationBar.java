package main.View;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class UserNavigationBar extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("cangkIR");

        BorderPane borderPane = new BorderPane();

        Text welcomeText = new Text("Welcome to cangkIR Online Shop");
        welcomeText.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        borderPane.setCenter(welcomeText);

        MenuBar menuBar = new MenuBar();
        Menu navigationMenu = new Menu("Menu");
        MenuItem homeMenuItem = new MenuItem("Home");
        MenuItem cartMenuItem = new MenuItem("Cart");
        MenuItem logoutMenuItem = new MenuItem("Log Out");

        navigationMenu.getItems().addAll(homeMenuItem, cartMenuItem, logoutMenuItem);
        menuBar.getMenus().add(navigationMenu);
        borderPane.setTop(menuBar);

        homeMenuItem.setOnAction(e -> {
            try {
                redirectToHomePage(primaryStage);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        cartMenuItem.setOnAction(e -> {
			try {
				redirectToCartPage(primaryStage);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
        logoutMenuItem.setOnAction(e -> redirectToLoginPage(primaryStage));

        BorderPane.setAlignment(menuBar, Pos.CENTER);

        Scene scene = new Scene(borderPane, 1000, 1000);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void redirectToHomePage(Stage primaryStage) throws Exception {
        HomePageUser homePageUser = new HomePageUser();
        homePageUser.showHomePage();
        primaryStage.close();
    }

    private void redirectToCartPage(Stage primaryStage) throws Exception {
    	CartPage cartPage = new CartPage();
        cartPage.showCartPage();
        primaryStage.close();
    }

    private void redirectToLoginPage(Stage primaryStage) {
        RegisterForm registerForm = new RegisterForm();
        registerForm.showLoginForm();
        primaryStage.close();
    }
}
