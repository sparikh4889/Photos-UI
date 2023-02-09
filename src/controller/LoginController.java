package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import application.Photos;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.Album;
import model.Superuser;
import model.User;

/**
 * @author Akshat Adlakha (aa2040) and Spandan Parikh (snp131)
 * Controller for the login page
 */
public class LoginController {
	
	@FXML public Button mLogin;
	
	@FXML public TextField tfUsername;
	
	/**
	 * A string that holds the value of admin
	 */
	public final String admin = "admin";
	
	/**
	 * A Superuser instance that helps maintain the state of the program
	 */
	public static Superuser driver = Photos.driver;

	/**
	 * login is a void method that redirects the user based on their username.
	 * (i.e) If the username was admin then user is redirected to the admin page.
	 * Otherwise, if not admin and the username exists, they will be redirected to their own album page.
	 * Lastly, if no user exists, an error message will appear
	 * @param event
	 * @throws IOException
	 */
	public void login(ActionEvent event) throws IOException {
		
		String username = tfUsername.getText().trim();

		if (username.equals(admin)) {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Admin.fxml"));
			Parent sceneManager = (Parent) fxmlLoader.load();
			AdminController adminController = fxmlLoader.getController();
			Scene adminScene = new Scene(sceneManager);
			Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			adminController.start();
			appStage.setScene(adminScene);
			appStage.show();
		} else if (driver.checkUser(username)) {
			User currentUser = driver.getCurrent();
			ArrayList<Album> useralbums = currentUser.getAlbums();
			UserController.username = username;
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/User.fxml"));
			Parent sceneManager = (Parent) fxmlLoader.load();
			UserController userController = fxmlLoader.getController();
			Scene userScene = new Scene(sceneManager);
			Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			userController.start(appStage);
			appStage.setScene(userScene);
			appStage.show();
		} else if ((username.isEmpty()) || (username == null)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Please enter a username");
			Optional<ButtonType> buttonClicked = alert.showAndWait();
			if (buttonClicked.get() == ButtonType.OK) {
				alert.close();
			} else {
				alert.close();
			}
		} else {
			System.out.println("Incorrect Input");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Login Issue Encountered");
			alert.setHeaderText("Please enter a valid username");
			Optional<ButtonType> buttonClicked = alert.showAndWait();
			if (buttonClicked.get() == ButtonType.OK) {
				alert.close();
			} else {
				alert.close();
			}
		}
	}
}