package main.View;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Db.DatabaseConn;
import main.Models.User;

public class LoginForm extends Application {
	
	public static String myUsername = null;
	
    @Override
    public void start(Stage primaryStage) {
    	
        primaryStage.setTitle("cangkIR");
        
        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 35; -fx-font-weight: bold;");

        Label usernameLabel = new Label("Username");
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Input your username here");

        Label passwordLabel = new Label("Password");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Input your password here");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> handleLogin(
        		usernameInput.getText(), 
        		passwordInput.getText(), 
        		primaryStage));
        
        Hyperlink hyperlink = new Hyperlink("Don't have an account yet? Register Here!");
        hyperlink.setOnAction(e -> handleRegisterForm(primaryStage));

        HBox loginLabelHBox = new HBox(10);
        loginLabelHBox.setAlignment(Pos.CENTER);
        loginLabelHBox.getChildren().addAll(titleLabel);
        
        HBox loginButtonHBox = new HBox(10);
        loginButtonHBox.setAlignment(Pos.CENTER);
        loginButtonHBox.getChildren().addAll(loginButton);

        HBox registerLabel = new HBox(10);
        registerLabel.setAlignment(Pos.CENTER);
        registerLabel.getChildren().addAll(hyperlink);

        VBox vbox = new VBox(10); 
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setPadding(new Insets(25, 25, 25, 25));
        vbox.getChildren().addAll(loginLabelHBox, usernameLabel, usernameInput, passwordLabel, passwordInput, loginButtonHBox, registerLabel);

        Scene scene = new Scene(vbox, 1000, 1000);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private static boolean validateLogin(String username, String password) {
    	Connection connection = DatabaseConn.connection();
    	String queryLogin = "SELECT * from msuser WHERE Username = ? AND UserPassword = ?";
    	try {
			PreparedStatement preparedStatement = connection.prepareStatement(queryLogin);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.getMessage();
		}
		return false;
    }
    
    public User getUsername(String username) {
    	Connection connection = DatabaseConn.connection();
    	String queryLogin = "SELECT * from msuser WHERE Username = ?";
    	try {
			PreparedStatement preparedStatement = connection.prepareStatement(queryLogin);
			preparedStatement.setString(1, username);
			
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				User user = new User();
				user.setUsername(rs.getString(username));
				return user;
			}
		} catch (SQLException e) {
			e.getMessage();
		}
		return null;
    }
    
    public String findUserIdByUsername (String username) {
    	Connection connection = DatabaseConn.connection();
    	String query = "SELECT UserID from msuser WHERE Username = ?";
    	try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, username);
			ResultSet rs = preparedStatement.executeQuery();
			if(rs.next()) {
				String rows = rs.getNString(1);
				return rows;
			}
		} catch (SQLException e) {
			e.getMessage();
		}
    	return null;
    }

    private void handleLogin(String username, String password, Stage primaryStage) {
    	
        if(username.isEmpty() || password.isEmpty()) {
        	showAlertErr("Error", "Login Error", "Please fill out your username or password");
        	return;
        } 
        
        boolean loginSuccess = validateLogin(username, password);
        
        if(loginSuccess) {
        	myUsername = username;
        	if(username.toLowerCase().contains("admin")) {
        		showAlertSucc("Success", "Success Login", "Login is successfully");
        		primaryStage.close();
        		showCupManagement();
        	} else if (!username.toLowerCase().contains("admin")) {
        		showAlertSucc("Success", "Success Login", "Login is successfully");
        		primaryStage.close();
        		showUserNavigationBar();
        	}
        } else {
        	showAlertErr("Error", "Login Error", "Invalid credentials");
        }     
    }
    
    public void showAlertSucc(String title, String content, String desc) {
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
    	alert.setTitle(title);
    	alert.setHeaderText(content);
    	alert.setContentText(desc);
    	alert.showAndWait();
    }
    
    public void showAlertErr(String title, String content, String desc) {
    	Alert alert = new Alert(Alert.AlertType.ERROR);
    	alert.setTitle(title);
    	alert.setHeaderText(content);
    	alert.setContentText(desc);
    	alert.showAndWait();
    }
    
    private static void handleRegisterForm(Stage primaryStage) {
        primaryStage.close();
        showRegisterForm();
    }

    private static void showRegisterForm() {
        Stage registerStage = new Stage();
        RegisterForm registerForm = new RegisterForm();
        registerForm.start(registerStage);
    }

    private static void showUserNavigationBar() {
        Stage userNavBarStage = new Stage();
        UserNavigationBar userNavBar = new UserNavigationBar();
        userNavBar.start(userNavBarStage);
    }
    
    private static void showAdminNavigationBar() {
        Stage adminNavBarStage = new Stage();
        AdminNavigationBar adminNavBar = new AdminNavigationBar();
        adminNavBar.start(adminNavBarStage);
    }
    
    private static void showCupManagement() {
        Stage cupManagementStage = new Stage();
        CupManagement cupManagement = new CupManagement(null);
        try {
			cupManagement.start(cupManagementStage);
		} catch (Exception e) {
			e.getMessage();
		}
    }
}
