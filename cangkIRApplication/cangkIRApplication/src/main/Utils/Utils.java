package main.Utils;
import javafx.scene.control.Alert;

public class Utils {
    
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
}
