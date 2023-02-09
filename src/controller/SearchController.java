package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import application.Photos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Album;
import model.Photo;
import model.Tag;

/**
 * A search class that has multiple search functions for the user
 * @author Akshat Adlakha (aa2040) and Spandan Parikh (snp131)
 *
 */
public class SearchController implements LogoutController {

	@FXML
	public Button mBack, mLogOut, mAddTag, mAnySearch, mAllSearch, mSearchDate, mCreateAlbum;
	
	@FXML
	public TextField tfName, tfValue;
	
	@FXML
	public ListView<String> listview;
	
	@FXML
	public ListView<Photo> photolistview;
	
	@FXML
	public ImageView imageview;
	
	@FXML
	public DatePicker dTo, dFrom;
	
	/**
	 * Stores instances of tags
	 */
	public ArrayList<Tag> taglist = new ArrayList<Tag>();
	
	/**
	 * Stores the properties of a tag in a string format
	 */
	public ArrayList<String> tagdisplay = new ArrayList<String>();
	
	/**
	 * Helps display a list of tags in a listview
	 */
	public ObservableList<String> obsTag;
	
	/**
	 * Helps display the results of a search in a listview
	 */
	public ObservableList<Photo> obsPhoto;
	
	/**
	 * An array list that stores instances of photos that are returned from searches
	 */
	public ArrayList<Photo> photolist = new ArrayList<Photo>();
	
	
	/**
	 * On start
	 */
	public void start() {
		
	}
	
	/**
	 * searchByDate is a void method that allows users to search for a group of photos within a certain date
	 * @param event
	 * @throws IOException
	 */
	public void searchByDate(ActionEvent event) throws IOException {
		this.photolist.clear();
		LocalDate from = dFrom.getValue();
		LocalDate to = dTo.getValue();
		
		if (from == null || to == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Incomplete date fields!");
			alert.setContentText("Dates can't be left blank!");
			Optional<ButtonType> buttonClicked = alert.showAndWait();

			if (buttonClicked.get() == ButtonType.OK) {
				alert.close();
			} else {
				alert.close();
		    }
			return;
		}
		
		if (to.isBefore(from)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Invalid Date Range");
			alert.setContentText("The From date should fall before the To date.");

			Optional<ButtonType> buttonClicked = alert.showAndWait();
			if (buttonClicked.get() == ButtonType.OK) {
				alert.close();
			} else {
				alert.close();
			}
			return;
		}

		mAnySearch.setVisible(false);
		mAnySearch.setDisable(true);
		mAllSearch.setVisible(false);
		mAllSearch.setVisible(true);
		this.photolist = Photos.driver.getCurrent().getPhotosInRange(from,to);

		displayPhotos();
	}
	
	/**
	 * orSearch is a void method that allows the users to search for photos based on any tags that appear in the taglist.
	 * It then populates photolist with photos that match any of the tags that exist in the taglist
	 * @param event
	 * @throws IOException
	 */
	public void orSearch(ActionEvent event) throws IOException{
		this.photolist.clear();
		if ((taglist.isEmpty()) || (taglist == null)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Empty List!");
			alert.setHeaderText("Add tags to the list");
			alert.setContentText("List of tags is empty!");

			Optional<ButtonType> buttonClicked = alert.showAndWait();
			if (buttonClicked.get() == ButtonType.OK) {
				alert.close();
			} else {
				alert.close();
			}
			return;	
		}

		mAllSearch.setVisible(false); 
		mAllSearch.setDisable(true);
		mSearchDate.setVisible(false);
		mSearchDate.setDisable(true);
		this.photolist = Photos.driver.getCurrent().getOrTaggedPhotos(taglist);

		displayPhotos();
	}
	
	/**
	 * andSearch is a void method that allows the users to search for photos based on specific tags in the taglist.
	 * It then populates photolist with photos that match if and only if all the tags in the taglist appear in the photo.
	 * @param event
	 * @throws IOException
	 */
	public void andSearch(ActionEvent event) throws IOException {
		if (taglist.isEmpty() || taglist == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Empty List!");
			alert.setHeaderText("Add tags to the list");
			alert.setContentText("List of tags is empty!");

			Optional<ButtonType> buttonClicked = alert.showAndWait();
			if (buttonClicked.get() == ButtonType.OK) {
				alert.close();
			} else {
				alert.close();
			}
			return;	
		}

		// System.out.println("And search");
		mAnySearch.setVisible(false);
		mAnySearch.setDisable(true);
		mSearchDate.setVisible(false);
		mSearchDate.setDisable(true);
		this.photolist = Photos.driver.getCurrent().getAndTaggedPhotos(taglist);
		displayPhotos();
	}
	
	/**
	 * addTag is a void method that allows the user to add a tag to the visible list of tags
	 * @param event
	 * @throws IOException
	 */
	public void addTag(ActionEvent event) throws IOException {
		if (tfName.getText().trim().isEmpty() || tfValue.getText().trim().isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Missing Values");
			alert.setHeaderText("Enter a key and its value!");
			alert.setContentText("The key and value must both be filled!");

			Optional<ButtonType> buttonClicked=alert.showAndWait();
			if (buttonClicked.get() == ButtonType.OK) {
				alert.close();
			} else {
				alert.close();
			}
			return;		
		}

		Tag tag = new Tag(tfName.getText().trim(), tfValue.getText().trim());
		taglist.add(tag);
		updateTagList();
	}
	
	/**
	 * updateTagList is a void method that refreshes the visible listview according to the changes in the taglist (add and remove tags)
	 */
	public void updateTagList() {
		tagdisplay.clear();
		for(Tag tag : taglist) {
			tagdisplay.add("Name: " + tag.name +    " | Value: " + tag.value);
		}
		obsTag = FXCollections.observableArrayList(tagdisplay);
		listview.setItems(obsTag);
		
		tfName.clear();
		tfValue.clear();
	}
	
	/**
	 * displayPhotos is a void method that displays the photos in the visible listview
	 */
	public void displayPhotos() {
		obsPhoto = FXCollections.observableArrayList(photolist);
		photolistview.setCellFactory(new Callback<ListView<Photo>, ListCell<Photo>>() {

			@Override
			public ListCell<Photo> call(ListView<Photo> p){
				return new Results();
			}
		});
		
		photolistview.setItems(obsPhoto);
		
	    if (!obsPhoto.isEmpty()) photolistview.getSelectionModel().select(0); // select first photo of album
	}
	
	/**
	 * 
	 * Helper class that populates the Imageview based on the the results of the search results
	 *
	 */
	private class Results extends ListCell<Photo> {
		AnchorPane anchor = new AnchorPane();
		StackPane stackpane = new StackPane();
		ImageView imageView = new ImageView();
		
		public Results() {
			super();
			imageView.setFitWidth(100.0);
			imageView.setFitHeight(100.0);
			imageView.setPreserveRatio(true);

			StackPane.setAlignment(imageView, Pos.TOP_LEFT);
			stackpane.getChildren().add(imageView);			
			stackpane.setPrefHeight(110.0);
			stackpane.setPrefWidth(90.0);
			
			AnchorPane.setLeftAnchor(stackpane, 0.0);
			anchor.getChildren().addAll(stackpane);
			anchor.setPrefHeight(110.0);
			setGraphic(anchor);	
		}
		
		@Override
		public void updateItem(Photo photo, boolean empty) {
			super.updateItem(photo, empty);
			setText(null);
			if (photo == null) {				
			} else{
				Image img = new Image(photo.pic.toURI().toString());
				imageView.setImage(img);
			}
		}
	}
	
	/**
	 * createAlbum is a void method that creates an album for the current user from the search results
	 * @param event
	 * @throws IOException
	 */
	public void createAlbum(ActionEvent event) throws IOException{
		if (photolist.isEmpty()) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Cannot create album! Searched photos is empty!");
			alert.setContentText("Please try different search results");

			Optional<ButtonType> buttonClicked=alert.showAndWait();
			if (buttonClicked.get() == ButtonType.OK) {
				alert.close();
			} else {
				alert.close();
			}
			return;			
		}

		Dialog<String> dialog = new Dialog<>();
		dialog.setTitle("Create a New Album from search results");
		dialog.setHeaderText("Name for album created from search results ");
		dialog.setResizable(true);
		   
		Label albumnameLabel = new Label("Album Name: ");
		TextField albumnameTextField = new TextField();
		  
		GridPane grid = new GridPane();
		grid.add(albumnameLabel, 1, 1);
		grid.add(albumnameTextField, 2, 1);
		   
		dialog.getDialogPane().setContent(grid);
		   
		ButtonType buttonTypeOk = new ButtonType("Add", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
		   
		dialog.setResultConverter(new Callback<ButtonType, String>() {
			@Override
			public String call(ButtonType b) {
				if (b == buttonTypeOk) {
					if (albumnameTextField.getText().trim().isEmpty()) {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error Dialog");
						alert.setHeaderText("Album name required!");
						alert.setContentText("Please retry!");

						Optional<ButtonType> buttonClicked = alert.showAndWait();
						if (buttonClicked.get() == ButtonType.OK) {
							alert.close();
						} else {
							alert.close();
						}
						return null;
					}
					return albumnameTextField.getText().trim();
				}
				return null;
			}
			
		});
		   
		Optional<String> result = dialog.showAndWait();
		   
		if (result.isPresent()) {
			Album albumFromSearch = new Album(result.get());
			Photos.driver.getCurrent().addAlbum(albumFromSearch);
			for(Photo photo : photolist) {
				   albumFromSearch.addPhoto(photo);
			}  
		}
	}
	
	/**
	 * back is a void method that redirects the user to the previous page --> User page
	 * @param event
	 * @throws IOException
	 */
	public void back(ActionEvent event) throws IOException{
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/User.fxml"));
		Parent sceneManager = (Parent) fxmlLoader.load();
		UserController userController = fxmlLoader.getController();
		Scene adminScene = new Scene(sceneManager);
		Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		userController.start(appStage);
		appStage.setScene(adminScene);
		appStage.show();
	}
	
	/**
	 * logOut is a void method that logs the user out and returns the user to the login page
	 * @param event
	 * @throws IOException
	 */
	public void logOut(ActionEvent event) throws IOException{
		logMeOut(event);
	}
}