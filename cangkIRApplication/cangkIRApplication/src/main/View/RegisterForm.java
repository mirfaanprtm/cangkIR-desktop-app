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

public class RegisterForm extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("cangkIR");
        
        // Membuat label judul
        Label titleLabel = new Label("Register");
        titleLabel.setStyle("-fx-font-size: 35; -fx-font-weight: bold;");

        // Membuat label dan input untuk username
        Label usernameLabel = new Label("Username");
        TextField usernameInput = new TextField();
        usernameInput.setPromptText("Input your username here");
        
        // Membuat label dan input untuk email
        Label emailLabel = new Label("Email");
        TextField emailInput = new TextField();
        emailInput.setPromptText("Input your email here");

        // Membuat label dan input untuk password
        Label passwordLabel = new Label("Password");
        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Input your password here");
        
        // Membuat label dan input untuk gender
        Label genderLabel = new Label("Gender");
        genderLabel.setStyle("-fx-font-size: 15; -fx-font-weight: bold;");
        RadioButton maleRadioButton = new RadioButton("Male");
        RadioButton femaleRadioButton = new RadioButton("Female");
        ToggleGroup genderToggleGroup = new ToggleGroup();
        maleRadioButton.setToggleGroup(genderToggleGroup);
        femaleRadioButton.setToggleGroup(genderToggleGroup);

        // Membuat tombol untuk register
        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> handleRegister(
        		usernameInput.getText(),
        		emailInput.getText(), 
        		passwordInput.getText(),
        		validateGender(maleRadioButton, femaleRadioButton),
        		primaryStage));
                
        // Membuat hyperlink login
        Hyperlink hyperlink = new Hyperlink("Already have an account? Click here to login!");
        hyperlink.setOnAction(e -> handleLoginForm(primaryStage));
        
        // Membuat HBox untuk label register
        HBox registerLabelHBox = new HBox(10);
        registerLabelHBox.setAlignment(Pos.CENTER);
        registerLabelHBox.getChildren().addAll(titleLabel);
        
        // Membuat HBox untuk tombol register
        HBox registerButtonHBox = new HBox(10);
        registerButtonHBox.setAlignment(Pos.CENTER);
        registerButtonHBox.getChildren().addAll(registerButton);
        
        // Membuat HBox untuk label login
        HBox loginLabel = new HBox(10);
        loginLabel.setAlignment(Pos.CENTER);
        loginLabel.getChildren().addAll(hyperlink);
        
        HBox genderGroup = new HBox(10); // 10 adalah spacing antar elemen
        genderGroup.getChildren().addAll(maleRadioButton, femaleRadioButton);
        
        // Membuat VBox untuk menyusun elemen-elemen secara vertikal
        VBox vbox = new VBox(10); // 10 adalah spacing antar elemen
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setPadding(new Insets(25, 25, 25, 25));

        // Menambahkan elemen-elemen ke dalam VBox
        vbox.getChildren().addAll(registerLabelHBox, usernameLabel, usernameInput, emailLabel, emailInput, passwordLabel, passwordInput, genderLabel, genderGroup, registerButtonHBox, loginLabel);

        // Membuat scene
        Scene scene = new Scene(vbox, 1000, 1000);

        // Menetapkan scene ke stage
        primaryStage.setScene(scene);

        // Menampilkan stage
        primaryStage.show();
    }
    
    private static String incrementID() {
    	Connection connection = DatabaseConn.connection();
    	String totalData = "SELECT COUNT(*) from msuser";
    	try {
    		PreparedStatement preparedStatement = connection.prepareStatement(totalData);
    		
    		ResultSet rs = preparedStatement.executeQuery();
    		if(rs.next()) {
    			int count = rs.getInt(1) + 1;
                String newID = "US" + String.format("%03d", count);
                return newID;
    		}
    	} catch (SQLException e) {
    		e.getMessage();
    	}
		return null;
    }
    
    private static String lastID() {
    	Connection connection = DatabaseConn.connection();
    	String query = "SELECT MAX(UserID) from msuser";
    	try {
    		PreparedStatement preparedStatement = connection.prepareStatement(query);
    		
    		ResultSet rs = preparedStatement.executeQuery();
    		
    		if(rs.next()) {
    			String newID = rs.getString(1);
    			int count = Integer.parseInt(newID.substring(2)) + 1;
                newID = "US" + String.format("%03d", count);
                return newID;
    		}
    	} catch (SQLException e) {
    		e.getMessage();
    	}
		return null;
	}
    
    private static String validateGender(RadioButton maleRadioButton, RadioButton femaleRadioButton) {
        if (maleRadioButton.isSelected()) {
            return "Male";
        } else if (femaleRadioButton.isSelected()) {
            return "Female";
        } else {
            return null;
        }
    }

    private static boolean validateRegister(String id, String username, String email, String password, String gender, String role) {
    	Connection connection = DatabaseConn.connection();
    	String queryRegis = "INSERT INTO msuser (UserID, Username, UserEmail, UserPassword, UserGender, UserRole) VALUES (?,?,?,?,?,?)";
    	try {
			PreparedStatement preparedStatement = connection.prepareStatement(queryRegis);
			preparedStatement.setString(1, id);
			preparedStatement.setString(2, username);
			preparedStatement.setString(3, email);
			preparedStatement.setString(4, password);
			preparedStatement.setString(5, gender);
			preparedStatement.setString(6, role);
			
			preparedStatement.executeUpdate();
			
			return true;
		} catch (SQLException e) {
			e.getMessage();
		}
		return false;
    }
    
    private static void handleRegister(String username, String email, String password, String gender, Stage primaryStage) {
        String role = "User";
        if (username.toLowerCase().contains("admin")) {
            role = "Admin";
        }
        
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || gender == null) {
            showAlertErr("Error", "Register Error", "Please fill out your username, email, password, or gender");
            return; 
        }

        if (usernameExist(username)) {
            showAlertErr("Error", "Register Error", "Please choose a different username");
            return;
        }

        if (emailExist(email)) {
            showAlertErr("Error", "Register Error", "Please choose a different email");
            return;
        }

        if (!email.endsWith("@gmail.com")) {
            showAlertErr("Error", "Register Error", "Make sure your email ends with @gmail.com");
            return;
        }

        if (password.length() < 8 || password.length() > 15) {
            showAlertErr("Error", "Register Error", "Make sure your password has a length of 8 - 15 characters");
            return;
        }

        if (!isAlphanumeric(password)) {
            showAlertErr("Error", "Register Error", "Password must be alphanumeric");
            return;
        }

        if (validateRegister(lastID(), username, email, password, gender, role)) {
        	LoginForm loginForm = new LoginForm();
            showAlertSucc("Success", "Register Success", "Please log in");
            primaryStage.close();
            loginForm.start(primaryStage);
        }
    }

    private static boolean usernameExist(String username) {
    	Connection connection = DatabaseConn.connection();
        String query = "SELECT COUNT(*) FROM msuser WHERE Username = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.getMessage();
        }

        return false;
    }

    private static boolean emailExist(String email) {
    	Connection connection = DatabaseConn.connection();
        String query = "SELECT COUNT(*) FROM msuser WHERE UserEmail = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.getMessage();
        }

        return false;
    }
    
    private static boolean isAlphanumeric(String password) {
        boolean hasLetter = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }

            if (hasLetter && hasDigit) {
                return true;
            }
        }

        return false;
    }
    
    private static void showAlertSucc(String title, String content, String desc) {
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
    	alert.setTitle(title);
    	alert.setHeaderText(content);
    	alert.setContentText(desc);
    	alert.showAndWait();
    }
    
    private static void showAlertErr(String title, String content, String desc) {
    	Alert alert = new Alert(Alert.AlertType.ERROR);
    	alert.setTitle(title);
    	alert.setHeaderText(content);
    	alert.setContentText(desc);
    	alert.showAndWait();
    }
    
    private void handleLoginForm(Stage primaryStage) {
        primaryStage.close();
        showLoginForm();
    }

    public void showLoginForm() {
        Stage loginStage = new Stage();
        LoginForm loginForm = new LoginForm();
        loginForm.start(loginStage);
    }
}
