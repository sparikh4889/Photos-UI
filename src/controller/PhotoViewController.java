package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import application.Photos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import model.Photo;
import model.Superuser;
import model.Tag;

/**
 * This class controls the view/ functions of a single image
 * @author Akshat Adlakha (aa2040) and Spandan Parikh (snp131)
 * @author 
 *
 */
public class PhotoViewController implements LogoutController {
	@FXML
	public ListView<String> listview;
	
	@FXML
	public ImageView displayArea;
	
	@FXML
	public Button mLogOut, mBack, mCaption, mAddTag, mDeleteTag;
	
	@FXML
	public TextField tfCaption, tfTagName, tfTagValue;

	/**
	 * An instance of the admin that is created to help keep track of current values
	 */
	public static Superuser adminUser = Photos.driver;
	
	/**
	 * Stores the instances of tags
	 */
	public static ArrayList<Tag> tagList = new ArrayList<>();
	
	/**
	 * Stores the properties of a tag in a string format
	 */
	public static ArrayList<String> tagDisplay = new ArrayList<>();
	
	/**
	 * Helps display a list of tags in a listview
	 */
	public ObservableList<String> obsTag;
	
	/**
	 * Current instance of photo
	 */
	public static Photo photo; 
	
	/**
	 * sets the title of the scene to current caption of the photo. As well as updates the current list of tags.
	 * @param app_stage
	 */
	public void start(Stage app_stage) {
		
		app_stage.setTitle(adminUser.getCurrent().getCurrentAlbum().getCurrentPhoto().getCaption() + " ");
		displayArea.setFitHeight(250);
		displayArea.setFitWidth(400);
		displayArea.setPreserveRatio(false);
		
		update();
		if (!tagList.isEmpty()) listview.getSelectionModel().select(0);
	}
	
	
	/**
	 * saveCaption is a void method that uses the text view to save captions
	 * @param event
	 * @throws IOException
	 */
	public void saveCaption(ActionEvent event) throws IOException {
		String caption = tfCaption.getText().trim();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Caption Confirmation");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure you want to change the caption to: " + caption);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			photo.setCaption(caption);
			photo.save(photo);
		} else {
			return;
		}
	}
	
	/**
	 * addTag is a void method that adds a tag to the list of tags of the current photo (visible)
	 * @param event
	 * @throws IOException
	 */
	public void addTag(ActionEvent event) throws IOException {
		String tagName = tfTagName.getText().trim();
		String tagValue = tfTagValue.getText().trim();
		if ((tagName.isEmpty()) || (tagValue.isEmpty())) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Tag Add Error");
			alert.setContentText("Incomplete Tag Pair.");
			alert.showAndWait();
			return;
		} else {
			Tag tag = new Tag(tagName, tagValue);
			adminUser.getCurrent().getCurrentAlbum().getCurrentPhoto().addTag(tag.name, tag.value);
			System.out.println(adminUser.getCurrent().getCurrentAlbum().getCurrentPhoto().getTagList());

			update();
			Superuser.save(adminUser);
		}
	}
	/**
	 * deleteTag is a void method that deletes the tag from the list of tags for that particular photo
	 * @param event
	 * @throws IOException
	 */
	public void deleteTag(ActionEvent event) throws IOException{
		int index = listview.getSelectionModel().getSelectedIndex();
		
		ArrayList<Tag> tagList = adminUser.getCurrent().getCurrentAlbum().getCurrentPhoto().getTagList();
		adminUser.getCurrent().getCurrentAlbum().getCurrentPhoto().deleteTag(tagList.get(index).name, tagList.get(index).value);
		
		update();
		Superuser.save(adminUser);
		
	}
	
	/**
	 * update is a void method that updates the visible list of tags anytime a tag is added or deleted
	 */
	public void update() {
		File file;
		if (photo != null) {
			file = photo.getPic();
			Image image = new Image(file.toURI().toString());
			displayArea.setImage(image);
		}
		
		tagDisplay.clear();
		ArrayList<Tag> tags = adminUser.getCurrent().getCurrentAlbum().getCurrentPhoto().getTagList();
		
		for(Tag tag : tags) {
			tagDisplay.add("Name: " + tag.name +    " | Value: " + tag.value);
		}
		obsTag = FXCollections.observableArrayList(tagDisplay);
		listview.setItems(obsTag);
		System.out.println(tagList.toString());
		tfTagName.clear();
		tfTagValue.clear();
	}
	
	/**
	 * back is a void method that redirects the user to the previous page --> the Album page
	 * @param event
	 * @throws IOException
	 */
	public void back(ActionEvent event) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AlbumView.fxml"));
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
	 * @param event
	 * @throws IOException
	 */
	public void logOut(ActionEvent event) throws IOException {
		logMeOut(event);
	}
	
}