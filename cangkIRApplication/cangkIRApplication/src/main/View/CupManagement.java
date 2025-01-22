package main.View;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Db.DatabaseConn;
import main.Models.Cup;
import main.Repos.CupRepo;

public class CupManagement extends Application {
	
    private TableView<Cup> cupTable;
    private TextField cupNameInput;
    private TextField cupPriceInput;
    private CupRepo cupRepo = new CupRepo();
    

    public CupManagement(CupRepo cupRepo) {
		this.cupRepo = cupRepo;
	}

	public CupManagement() {
		this.cupRepo = new CupRepo();
	}

	@Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("cangkIR");
        
        Label titleLabel = new Label("Cup Management");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
     
        BorderPane borderPane = new BorderPane();

        MenuBar menuBar = new MenuBar();
        Menu navigationMenu = new Menu("Menu");
        MenuItem cupManagementItem = new MenuItem("Cup Management");
        MenuItem logoutMenuItem = new MenuItem("Log Out");
        logoutMenuItem.setOnAction(e -> redirectToLoginPage(primaryStage));
        navigationMenu.getItems().addAll(cupManagementItem, logoutMenuItem);
        menuBar.getMenus().add(navigationMenu);
        borderPane.setTop(menuBar);
        
        Label cupNameLabel = new Label("Cup Name");
        cupNameInput = new TextField();
        cupNameInput.setMaxWidth(200);
        cupNameInput.setPromptText("Input cup name here");
        
        Label cupPriceLabel = new Label("Cup Price");
        cupPriceInput = new TextField();
        cupPriceInput.setMaxWidth(200);
        cupPriceInput.setPromptText("Input cup price here");
 
        Button addButton = new Button("Add Cup");
        addButton.setStyle("-fx-font-size: 12;");
        addButton.setMinWidth(120); 
        addButton.setMinHeight(50);

        Button updateButton = new Button("Update Price");
        updateButton.setStyle("-fx-font-size: 12;");
        updateButton.setMinWidth(120);
        updateButton.setMinHeight(50);

        Button removeButton = new Button("Remove Cup");
        removeButton.setStyle("-fx-font-size: 12;");
        removeButton.setMinWidth(120);
        removeButton.setMinHeight(50);

        cupTable = new TableView<>();
        cupTable.setMaxWidth(500);
        cupTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Cup, String> cupNameColumn = new TableColumn<>("Cup Name");
        TableColumn<Cup, Integer> cupPriceColumn = new TableColumn<>("Cup Price");
        cupNameColumn.setCellValueFactory(new PropertyValueFactory<>("CupName"));
        cupPriceColumn.setCellValueFactory(new PropertyValueFactory<>("CupPrice"));
        cupTable.getColumns().addAll(cupNameColumn, cupPriceColumn); 

        CupRepo cupRepo = new CupRepo();
        List<Cup> cupsData = cupRepo.getAll();
        cupTable.getItems().addAll(cupsData);
        cupTable.refresh();
        
        TableView.TableViewSelectionModel<Cup> selectionModel = cupTable.getSelectionModel();
        selectionModel.selectedItemProperty().addListener(new ChangeListener<Cup>() {
            @Override
            public void changed(ObservableValue<? extends Cup> observable, Cup oldValue, Cup newValue) {
                if (newValue != null) {
                    cupNameInput.setText(newValue.getCupName());
                    cupPriceInput.setText(String.valueOf(newValue.getCupPrice()));
                }
            }
        });
        
        removeButton.setOnAction(e -> {
            String cupName = cupNameInput.getText();
            if (!cupName.isEmpty()) {
            	removeCup(cupName);
            } else {
                showAlertErr("Error", "Cup Management", "Please select a cup from table to be deleted");
            }
        }); 
        
        Cup cup = new Cup();                
        addButton.setOnAction(e -> addCup());
        updateButton.setOnAction(e -> {
        	String cupName = cupNameInput.getText();
            if (!cupName.isEmpty()) {
            	updatePrice(cupName);
            } else {
                showAlertErr("Error", "Cup Management", "Please select a cup from table to be updated");
            }
        });
        

        VBox leftVBox = new VBox(10);
        leftVBox.setAlignment(Pos.CENTER_LEFT);
        leftVBox.getChildren().addAll(
                new Label("Cup Name"), cupNameInput,
                new Label("Cup Price"), cupPriceInput,
                addButton, updateButton, removeButton
        );

        VBox rightVBox = new VBox(10);
        rightVBox.setAlignment(Pos.CENTER_LEFT);
        rightVBox.getChildren().addAll(titleLabel, cupTable);

        HBox layout = new HBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(rightVBox, leftVBox);

        borderPane.setLeft(rightVBox);
        borderPane.setCenter(leftVBox);
        BorderPane.setAlignment(leftVBox, Pos.CENTER);
        BorderPane.setAlignment(rightVBox, Pos.CENTER);
        BorderPane.setMargin(leftVBox, new Insets(0, 50, 0, 20)); 
        BorderPane.setMargin(rightVBox, new Insets(0, 0, 0, 150));	

        Scene scene = new Scene(borderPane, 1000, 1000);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showAlertSucc(String title, String content, String desc) {
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
    	alert.setTitle(title);
    	alert.setHeaderText(content);
    	alert.setContentText(desc);
    	alert.showAndWait();
    }
    
    public static void showAlertErr(String title, String content, String desc) {
    	Alert alert = new Alert(Alert.AlertType.ERROR);
    	alert.setTitle(title);
    	alert.setHeaderText(content);
    	alert.setContentText(desc);
    	alert.showAndWait();
    }
	 
    private void addCup() {
        try {
            String fieldCupName = cupNameInput.getText();
            String fieldCupPrice = cupPriceInput.getText();

            if (fieldCupName.isEmpty() || fieldCupPrice.isEmpty()) {
                showAlertErr("Error", "Cup Management", "Please fill out the cup name and price");
                return;
            }

            int cupPrice;
            try {
                cupPrice = Integer.parseInt(fieldCupPrice);

                if (cupPrice < 5000 || cupPrice > 1000000) {
                    showAlertErr("Error", "Cup Management", "Cup price must be 5000 - 1000000");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlertErr("Error", "Cup Management", "Invalid Cup Price. Please enter a valid price");
                return;
            }

            Cup newCup = new Cup(fieldCupName, cupPrice);
            boolean isCupExist = cupTable.getItems().stream()
                    .anyMatch(existingCup -> existingCup.getCupName().equalsIgnoreCase(newCup.getCupName()));

            if (isCupExist) {
                showAlertErr("Error", "Cup Management", "Cup Already Exists");
                return;
            }  
            
            CupRepo cupRepo = new CupRepo();
            cupRepo.create(newCup);
            cupTable.refresh();
            cupTable.getItems().add(newCup);
            showAlertSucc("Success", "Cup Management", "Cup Successfully Added");
        } catch (Exception e) {
            e.printStackTrace();
            showAlertErr("Error", "Cup Management", "An unexpected error occurred: " + e.getMessage());
        }
    }

    private void removeCup(String cupName) {
        try {
            CupRepo cupRepo = new CupRepo();
            List<Cup> cups = cupRepo.findByName(cupName);
            cupRepo.delete(cupName);

            boolean removed = cupTable.getItems().removeIf(cup -> cup.getCupName().equalsIgnoreCase(cupName));

            if (removed) {
                cupTable.refresh();
                showAlertSucc("Success", "Cup Management", "Delete Successfully");
            } else {
                showAlertErr("Error", "Cup Management", "Error removing cup from table");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlertErr("Error", "Cup Management", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    private void updatePrice(String cupName) {
        try {
            String fieldCupPrice = cupPriceInput.getText();
            
            try {
            	int newCupPrice = Integer.parseInt(fieldCupPrice);

                if (newCupPrice < 5000 || newCupPrice > 1000000) {
                    showAlertErr("Error", "Cup Management", "Cup price must be between 5000 and 1000000.");
                    return;
                }

                Optional<Cup> cupToUpdate = cupTable.getItems().stream()
                        .filter(existingCup -> existingCup.getCupName().equalsIgnoreCase(cupName))
                        .findFirst();

                if (cupToUpdate.isPresent()) {
                    Cup existingCup = cupToUpdate.get(); // Ambil item yang ditemukan
                    Cup updatedCup = new Cup(existingCup.getCupName(), newCupPrice);
                    CupRepo cupRepo = new CupRepo();
                    cupRepo.update(updatedCup);
                    
                    int index = cupTable.getItems().indexOf(existingCup);
                    cupTable.getItems().set(index, updatedCup);
                    cupTable.refresh();
                    showAlertSucc("Success", "Cup Management", "Cup price successfully updated.");
                } else {
                    showAlertErr("Error", "Cup Management", "Cup Not Found");
                }
            } catch (NumberFormatException e) {
                showAlertErr("Error", "Cup Management", "Invalid Cup Price. Please enter a valid price");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlertErr("Error", "Cup Management", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    private void redirectToLoginPage(Stage primaryStage) {
        RegisterForm registerForm = new RegisterForm();
        registerForm.showLoginForm();
        primaryStage.close();	
    }
    
    private static String incrementID() throws Exception {
    	Connection connection = DatabaseConn.connection();
    	String totalData = "SELECT COUNT(*) from mscup";
    	try {
    		PreparedStatement preparedStatement = connection.prepareStatement(totalData);
    		
    		ResultSet rs = preparedStatement.executeQuery();
    		if(rs.next()) {
    			int count = rs.getInt(1) + 1;
                String newID = "CU" + String.format("%03d", count);
                return newID;
    		}
    	} catch (SQLException e) {
    		e.getMessage();
    	}
		return null;
    }
}