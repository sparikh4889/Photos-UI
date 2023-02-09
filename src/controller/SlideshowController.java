package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Photo;

/**
 * 
 * @author Akshat Adlakha (aa2040) and Spandan Parikh (snp131)
 *
 */
public class SlideshowController implements LogoutController {

	@FXML
	public ImageView displayArea;
	
	@FXML
	public Button mForward, mBackward, mBack, mLogOut;
	
	@FXML
	public TextField tfCaption, tfTagName, tfTagValue;
	
	@FXML
	public Text tStatus;
	
	/** 
	 * Stores instances of photos going to be displayed 
	 */ 
	public static ArrayList<Photo> album = new ArrayList<>();
	public static final int frontIndex = 0;
	public static int backIndex;
	public static int currentIndex;

	
	/** 
	 * When the scene loads the page updates the slideshow
	 */ 
	public void start() {
		displayArea.setFitHeight(300);
		displayArea.setFitWidth(550);
		displayArea.setPreserveRatio(false);
		update();
	}

	/**
	 * update is a void method that populates the visible imageview with the current image 
	 */
	public void update() {
		currentIndex = 0;
		backIndex = album.size() - 1;
		
		File file;
		Photo photo = album.get(0);
		if (photo != null) {
			file = photo.getPic();
			Image image = new Image(file.toURI().toString());
			displayArea.setImage(image);
		}
		
		int ci = currentIndex+1;
		int bi = backIndex+1;
		tStatus.setText("Photo: " + ci + " of " + bi);
	}
	
	/**
	 * forward is a void method that changes the image view to the next photo if there are any 
	 */
	public void forward() {
		if (currentIndex + 1 > backIndex) {
			return;
		} else {
			currentIndex++;
			File file;
			Photo photo = album.get(currentIndex);
			if (photo != null) {
				file = photo.getPic();
				Image image = new Image(file.toURI().toString());
				displayArea.setImage(image);
				int ci = currentIndex+1;
				int bi = backIndex+1;
				tStatus.setText("Photo: " + ci + " of " + bi);
			}
		}
	}
	
	/**
	 * backward is a void method that switches the imageview to the previous photo 
	 */
	public void backward() {
		if (currentIndex - 1 < frontIndex) {
			return;
		} else {
			currentIndex--;
			File file;
			Photo photo = album.get(currentIndex);
			if (photo != null) {
				file = photo.getPic();
				Image image = new Image(file.toURI().toString());
				displayArea.setImage(image);
				int ci = currentIndex+1;
				int bi = backIndex+1;
				tStatus.setText("Photo: " + ci + " of " + bi);
			}
		}
	}
	
	/**
	 * back is a void method that redirects the user to the previous page --> the AlbumView page
	 * @param event
	 * @throws IOException
	 */
	public void back(ActionEvent event) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AlbumView.fxml")); // changed from PhotoView.fxml
		Parent sceneManager = (Parent) fxmlLoader.load();
		AlbumViewController albumViewController = fxmlLoader.getController();
		Scene adminScene = new Scene(sceneManager);
		Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		albumViewController.start(appStage);
		appStage.setScene(adminScene);
		appStage.show();
	}
	
	/**
	 * logOut is a void method that logs the user out and returns the user to the login page
	 */
	public void logOut(ActionEvent event) throws IOException {
		logMeOut(event);
	}
}