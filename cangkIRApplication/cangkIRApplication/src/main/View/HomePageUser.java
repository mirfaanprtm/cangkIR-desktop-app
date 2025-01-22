package main.View;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import main.Models.Cart;
import main.Models.Cup;
import main.Models.User;
import main.Repos.CartRepo;
import main.Repos.CupRepo;

public class HomePageUser extends Application {
	
    private TableView<Cup> cupTable;
    private CupRepo cupRepo = new CupRepo();
    private Spinner<Integer> spinner = new Spinner<>();
    private LoginForm loginForm = new LoginForm();

    public HomePageUser(CupRepo cupRepo) {
		this.cupRepo = cupRepo;
	}

	public HomePageUser() {
		this.cupRepo = new CupRepo();
	}

	@Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("cangkIR");
        
        
        SpinnerValueFactory<Integer> valueSpinner = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1);
        spinner.setValueFactory(valueSpinner);
        
        Label titleLabel = new Label("Cup List");
        titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
     
        BorderPane borderPane = new BorderPane();


        MenuBar menuBar = new MenuBar();
        Menu navigationMenu = new Menu("Menu");
        MenuItem homeMenuItem = new MenuItem("Home");
        MenuItem cartMenuItem = new MenuItem("Cart");
        MenuItem logoutMenuItem = new MenuItem("Log Out");
        
        navigationMenu.getItems().addAll(homeMenuItem, cartMenuItem, logoutMenuItem);
        menuBar.getMenus().add(navigationMenu);
        borderPane.setTop(menuBar);
        
        cartMenuItem.setOnAction(e -> {
			try {
				redirectToCartPage(primaryStage);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
        logoutMenuItem.setOnAction(e -> redirectToLoginPage(primaryStage));
        
        Label cupNameLabel = new Label("Cup Name");
        cupNameLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        
        Label totalPriceLabel = new Label("Price");
        totalPriceLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
 
        Button addButton = new Button("Add To Cart");
        addButton.setStyle("-fx-font-size: 12;");
        addButton.setMinWidth(120); 
        addButton.setMinHeight(50);

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
                    cupNameLabel.setText(newValue.getCupName());
                    totalPriceLabel.setText(String.valueOf("Total price: " + newValue.getCupPrice() * spinner.getValue()));
                }
            }
        });

        addButton.setOnAction(e -> addCart());

        VBox leftVBox = new VBox(10);
        leftVBox.setAlignment(Pos.CENTER_LEFT);
        leftVBox.getChildren().addAll(
                cupNameLabel,
                spinner,
                totalPriceLabel,
                addButton
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
    
    private void addCart() {
        try {
            Cup selectedCup = cupTable.getSelectionModel().getSelectedItem();
            CartRepo cartRepo = new CartRepo();
            LoginForm loginForm = new LoginForm();
            String userLogin = LoginForm.myUsername;

            String userId = loginForm.findUserIdByUsername(userLogin);

            int quantity = spinner.getValue();

            if (userId == null || selectedCup == null) {
                showAlertErr("Error", "Cart Error", "Please select a cup to be added");
                return;
            }

            String cupId = cupRepo.findCupIdByCupName(selectedCup.getCupName());
            if (cupId == null) {
                showAlertErr("Error", "Cart Error", "Cup ID is null");
                return;
            }

            Cart cart = new Cart(userId, cupId, quantity);
            cartRepo.createOrUpdate(cart);

            showAlertSucc("Success", "Cart Info", "Item successfully added to the cart!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlertErr("Error", "Cart Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    public void showHomePage() throws Exception {
    	Stage homePageStage = new Stage();
    	HomePageUser homePageuser = new HomePageUser();
    	homePageuser.start(homePageStage);
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