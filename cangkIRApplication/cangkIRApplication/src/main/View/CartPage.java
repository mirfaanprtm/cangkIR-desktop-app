package main.View;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.omg.CORBA.PRIVATE_MEMBER;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import main.Db.DatabaseConn;
import main.Models.Cart;
import main.Models.Courier;
import main.Models.Cup;
import main.Models.TransactionDetail;
import main.Models.TransactionHeader;
import main.Repos.CartRepo;
import main.Repos.CourierRepo;
import main.Repos.CupRepo;
import main.Repos.TransactionRepo;

public class CartPage extends Application {
	
    private TableView<Cart> cartTable;
    private CupRepo cupRepo = new CupRepo();
    private ComboBox<Courier> courierDatas;
    private CheckBox insuranceCheckBox = new CheckBox();
    private Label totalPriceLabel = new Label("Total price:"); 
    
    private int selectedCourier;
    private String selectCourier;
    private String selectedCupName;
    private int selectedCupPrice;
    private int selectedQuantity;
    private int insuranceFee = 2000;
    private int selectedUseDelivery;
    private LoginForm loginForm = new LoginForm();
    public static Cart selectedCart;
    

    public CartPage(CupRepo cupRepo) {
		this.cupRepo = cupRepo;
	}
    
    public CartPage() {
    	
    }

	@Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("cangkIR");
        
        Spinner<Integer> spinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueSpinner = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1);
        spinner.setValueFactory(valueSpinner);
        
        
        String userLogin = LoginForm.myUsername;
        Label titleLabel = new Label(userLogin + "'s Cart");
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
        
        homeMenuItem.setOnAction(e -> {
            try {
                redirectToHomePage(primaryStage);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        logoutMenuItem.setOnAction(e -> redirectToLoginPage(primaryStage));
        
        Label labelDeleteItem = new Label("Delete Item");
        labelDeleteItem.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        
        Label courierNameLabel = new Label("Courier");
        courierNameLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        
        
        totalPriceLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        
        Label courierPriceLabel = new Label("Courier price: ");
        courierPriceLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        
        Label deliveryInsurenceLabel = new Label("Use Delivery Insurence");
        deliveryInsurenceLabel.setStyle("-fx-font-size: 15;");
 
        Button checkout = new Button("Checkout");
        checkout.setStyle("-fx-font-size: 12;");
        checkout.setMinWidth(120); 
        checkout.setMinHeight(50);
        
        Button deleteItem = new Button("Delete Item");
        deleteItem.setStyle("-fx-font-size: 12;");
        deleteItem.setMinWidth(120); 
        deleteItem.setMinHeight(50);

        cartTable = new TableView<>();
        cartTable.setMaxWidth(500);
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Cart, String> cupNameColumn = new TableColumn<>("Cup Name");
        TableColumn<Cart, Integer> cupPriceColumn = new TableColumn<>("Cup Price");
        TableColumn<Cart, Integer> quantityColumn = new TableColumn<>("Quantity");
        TableColumn<Cart, Integer> totalColumn = new TableColumn<>("Total");
        cupNameColumn.setCellValueFactory(new PropertyValueFactory<>("CupName"));
        cupPriceColumn.setCellValueFactory(new PropertyValueFactory<>("CupPrice"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("Total"));
        cartTable.getColumns().addAll(cupNameColumn, cupPriceColumn, quantityColumn, totalColumn); 
        
        LoginForm loginForm = new LoginForm();
        String userId = loginForm.findUserIdByUsername(userLogin);

        CartRepo cartRepo = new CartRepo();
        List<Cart> cartsData = cartRepo.getAllByUser(userId);
        cartTable.getItems().addAll(cartsData);
        cartTable.refresh();
        
        deleteItem.setOnAction(e -> {
        	if(cartsData.isEmpty()) {
        		showAlertErr("Error", "Error Cart", "Cart is Empty");
        	}
        	removeCart();
        });
        
        checkout.setOnAction(e -> {
        	if(cartsData.isEmpty()) {
        		showAlertErr("Error", "Error Cart", "Cart is Empty");
        		return;
        	}
        	try {
        		Cart selectedCart = cartTable.getSelectionModel().getSelectedItem();
				if(selectedCart != null) {
					boolean validate = validateCheckout();
					if(validate) {
						popUpConfirmation();
					}
					return;
				}
				showAlertErr("Error", "Checkout Error", "Please select an item before checking out");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        });
        
        courierDatas = new ComboBox<>();
        CourierRepo courierRepo = new CourierRepo();
        List<Courier> courierData = courierRepo.getAll();
        Courier courier = new Courier();
        courierDatas.getItems().addAll(courierData);
        courierDatas.setPromptText("Select Courier");
        
        TableView.TableViewSelectionModel<Cart> selectionModel = cartTable.getSelectionModel();
        selectionModel.selectedItemProperty().addListener(new ChangeListener<Cart>() {
            @Override
            public void changed(ObservableValue<? extends Cart> observable, Cart oldValue, Cart newValue) {
                if (newValue != null) {
                    totalPriceLabel.setText(String.valueOf("Total price: " + newValue.getCupPrice() * newValue.getQuantity()));
                    selectedCupPrice = newValue.getCupPrice();
                    selectedQuantity = newValue.getQuantity();
                    selectedCupName = newValue.getCupName();
                }
            }
        });
        
        courierDatas.valueProperty().addListener((observable, updateValue, newValue) -> {
            if (newValue != null) {
                courierPriceLabel.setText("Courier price: " + newValue.getCourierPrice());
                selectedCourier = newValue.getCourierPrice();
                selectCourier = newValue.getCourierName();
                totalPriceLabel.setText(String.valueOf("Total price: " + ((selectedCupPrice * selectedQuantity) + selectedCourier)));
                
            } else {
                courierPriceLabel.setText("Courier price: ");
            }
        });
        
        insuranceCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateTotalPrice();
        });
        

        HBox devInsuranceBox = new HBox(5);
        devInsuranceBox.getChildren().addAll(insuranceCheckBox, deliveryInsurenceLabel);

        VBox leftVBox = new VBox(10);
        leftVBox.setAlignment(Pos.CENTER_LEFT);
        leftVBox.getChildren().addAll(
                labelDeleteItem,
                deleteItem,
                courierNameLabel,
                courierDatas,
                courierPriceLabel,
                devInsuranceBox,
                totalPriceLabel,
                checkout
        );

        VBox rightVBox = new VBox(10);
        rightVBox.setAlignment(Pos.CENTER_LEFT);
        rightVBox.getChildren().addAll(titleLabel, cartTable);

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
	
	private void updateTotalPrice() {
	    int totalPrice = (selectedCupPrice * selectedQuantity) + selectedCourier;
	    if (insuranceCheckBox.isSelected()) {
	        totalPrice += insuranceFee;
	        selectedUseDelivery = 1; 
	    } else {
	        selectedUseDelivery = 0;
	    }
	    totalPriceLabel.setText("Total price: " + totalPrice);
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
	                 
    private void removeCart() {
    	try {
            CartRepo cartRepo = new CartRepo();
            String userLogin = LoginForm.myUsername;
            String userID = loginForm.findUserIdByUsername(userLogin);
            String cupID = cupRepo.findCupIdByCupName(selectedCupName);
            cartRepo.deleteByUserAndCup(userID, cupID);

            boolean removed = cartTable.getItems().removeIf(c -> c.getCupName().equalsIgnoreCase(selectedCupName));

            if (removed) {
                cartTable.refresh();
                showAlertSucc("Message", "Delete Information", "Cart deleted successfully");
            } else {
            	showAlertErr("Error", "Deletion Error", "Please select the item you want to delete");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlertErr("Error", "Cart Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    private void removeAfterCheckout() {
    	try {
            CartRepo cartRepo = new CartRepo();
            String userLogin = LoginForm.myUsername;
            String userID = loginForm.findUserIdByUsername(userLogin);
            String cupID = cupRepo.findCupIdByCupName(selectedCupName);
            cartRepo.deleteByUserAndCup(userID, cupID);

            boolean removed = cartTable.getItems().removeIf(c -> c.getCupName().equalsIgnoreCase(selectedCupName));

            if (removed) {
                cartTable.refresh();
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean validateCheckout() {
    	TransactionRepo transactionRepo = new TransactionRepo();
        String transactionId = transactionRepo.lastID();
        String cupID = cupRepo.findCupIdByCupName(selectedCupName);

        String userLogin = LoginForm.myUsername;
        String userId = loginForm.findUserIdByUsername(userLogin);

        CourierRepo courierRepo = new CourierRepo();
        String courierId = courierRepo.findCourierIdByCourierName(selectCourier);
        
        if (transactionId == null || transactionId.isEmpty() ||
                userId == null || userId.isEmpty() ||
                courierId == null || courierId.isEmpty()) {
                showAlertErr("Error", "Checkout Error", "Please fill out all fields before checking out");
                return false;
        		}
		return true;
    }

    public void checkout() throws Exception {
    	
        selectedCart = cartTable.getSelectionModel().getSelectedItem();
                
        TransactionRepo transactionRepo = new TransactionRepo();
        String transactionId = transactionRepo.lastID();
        String cupID = cupRepo.findCupIdByCupName(selectedCupName);

        String userLogin = LoginForm.myUsername;
        String userId = loginForm.findUserIdByUsername(userLogin);

        CourierRepo courierRepo = new CourierRepo();
        String courierId = courierRepo.findCourierIdByCourierName(selectCourier);

        if (selectedCart == null) {
          showAlertErr("Error", "Checkout Error", "Please select an item before checking out");
        }
        
        TransactionHeader th = new TransactionHeader();
        th.setTransactionID(transactionId);
        th.setUserID(userId);
        th.setCourierID(courierId);
        
        Date currentDate = new Date();
        th.setTransactionDate(new java.sql.Date(currentDate.getTime()));
        th.setUseDeliveryInsurance(selectedUseDelivery);
        
        TransactionDetail td = new TransactionDetail();
        td.setTransactionID(transactionId);
        td.setCupID(cupID);
        td.setQuantity(selectedQuantity);

        transactionRepo.create(th);
        transactionRepo.create(td);
        removeAfterCheckout();
        cartTable.refresh();
        showAlertSucc("Success", "Checkout Information", "Checkout Successful");
        
    }

    
    public void showHomePage() throws Exception {
    	Stage homePageStage = new Stage();
    	HomePageUser homePageuser = new HomePageUser();
    	homePageuser.start(homePageStage);
    }
    
    private void redirectToHomePage(Stage primaryStage) throws Exception {
    	HomePageUser homePageUser = new HomePageUser();
        homePageUser.showHomePage();
        primaryStage.close();
    }

    public void showCartPage() throws Exception {
    	Stage cartPageStage = new Stage();
    	CartPage cartPage = new CartPage();
    	cartPage.start(cartPageStage);
    }
    
    public void redirectToCartPage(Stage primaryStage) throws Exception {
    	CartPage cartPage = new CartPage();
        cartPage.showCartPage();
        primaryStage.close();
    }

    private void redirectToLoginPage(Stage primaryStage) {
        RegisterForm registerForm = new RegisterForm();
        registerForm.showLoginForm();
        primaryStage.close();	
    }
    
    private void popUpConfirmation() {
    	Stage primaryStage = new Stage();
    	
    	BorderPane borderPane = new BorderPane();
    	
    	borderPane.setPadding(new Insets(0, 0, 0, 0));
        
        Label headerLabel = new Label("Checkout confirmation");
        headerLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox headerBox = new VBox(1);
        headerBox.getChildren().add(headerLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setStyle("-fx-background-color: black; -fx-padding: 2;");

        borderPane.setTop(headerBox);

        BackgroundFill backgroundFill = new BackgroundFill(Color.LIGHTBLUE, null, null);
        Background background = new Background(backgroundFill);
        borderPane.setBackground(background);

        Text promptText = new Text("Are you sure you want to purchase?");
        promptText.setFill(Color.BLACK);
        promptText.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        Button btnYes = new Button("Yes");
        btnYes.setStyle("-fx-font-size: 18;");
        btnYes.setMinWidth(80); 
        btnYes.setMinHeight(40); 

        Button btnNo = new Button("No");
        btnNo.setStyle("-fx-font-size: 18;");
        btnNo.setMinWidth(80);
        btnNo.setMinHeight(40);
        
        CartPage cartPage = new CartPage();
        
        btnNo.setOnAction(e -> primaryStage.close());

        btnYes.setOnAction(e -> {
            try {
                checkout();
                primaryStage.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        
        HBox buttonBox = new HBox(12); 
        buttonBox.getChildren().addAll(btnYes, btnNo);
        buttonBox.setAlignment(Pos.CENTER);

        
        VBox vBox = new VBox(30);
        vBox.getChildren().addAll(promptText, buttonBox);
        vBox.setAlignment(Pos.CENTER);

        borderPane.setCenter(vBox);

        Scene scene = new Scene(borderPane, 600, 600);

        primaryStage.setTitle("");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}