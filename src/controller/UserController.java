package controller;

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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import model.Album;
import model.Superuser;
import model.User;

/** 
 * User controller controls all the actions from opening/adding/deleting albums to searching 
 * @author Akshat Adlakha (aa2040) and Spandan Parikh (snp131)
 * 
 */
public class UserController implements LogoutController{
	@FXML
	public ListView<Album> listview;
	
	@FXML
	public Button mLogOut, mDisplay, mOpenAlbum, mRenameAlbum, mDeleteAlbum, mSearch, mAddAlbum;
	
	@FXML
	public Text tUser, tNumber, tDate;
	
	@FXML
	public TextField tfAlbumName, tfNewAlbum; // user1 and user2
	
	/**
	 * Current Username
	 */
	public static String username;
	
	/**
	 * Stores instances of all albumss
	 */
	public static ArrayList<Album> albumList = new ArrayList<>();
	
	/**
	 * Helps display the list of albums
	 */
	public ObservableList<Album> observableList;	
	
	/**
	 * A Superuser instance that helps maintain the state of the program
	 */
	public static Superuser adminUser = Photos.driver;
	
	/**
	 * Stores current user
	 */
	public static User user;
	
	/**
	 * Current stock photo
	 */
	public static boolean stock;
	
	/**
	 * When the scene loads the page updates the visible list of albums
	 * @param app_stage
	 */
	public void start(Stage app_stage) {
		update();
		app_stage.setTitle(adminUser.getCurrent().getUsername() + " Collection of Photo's");
		if (!albumList.isEmpty()) listview.getSelectionModel().select(0); // select first user
	
		// Listen for selection changes
		if (albumList.size() > 0) {
			tfAlbumName.setText(albumList.get(0).albumName);	
			tNumber.setText("# of Photos: " + albumList.get(0).photoCount);
			tDate.setText("Date(s) (First, Last): \n\t" + albumList.get(0).getFirstDate() + "\n\t" + albumList.get(0).getLastDate());
		}
		listview.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> updateContent(newValue) );
	}

	/**
	 * updateContent is a void method that updates the values of the Album properties
	 * @param newValue The new album value
	 */
	private void updateContent(Album newValue) {
		if (newValue != null) {
			tfAlbumName.setText(newValue.albumName);	
			tNumber.setText("# of Photos: " + newValue.photoCount);
			tDate.setText("Date(s): \n\t" + newValue.getFirstDate() + " \n\t" + newValue.getLastDate());
		}
	}
	
	/**
	 * updateContentBack is a void method that updates properties of the album
	 */
	public void updateContentBack() {
		if (albumList.size() > 0) {
			Album alb = listview.getSelectionModel().getSelectedItem();
			tNumber.setText("# of Photos: " + alb.photoCount);
			tDate.setText("Date(s): \n\t" + alb.getFirstDate() + "\n\t" + alb.getLastDate());
		}
	}
	
	/**
	 * addAlbum is a void method that adds an album to the user's album list
	 * @throws IOException
	 */
	public void addAlbum() throws IOException {
		String albName = tfNewAlbum.getText().trim();
		Album album = new Album(albName);
		
		if ((albName.isEmpty()) || (albName == null)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Empty Field!");
			alert.setContentText("Enter an album name.");
			alert.showAndWait();
			return;
		} else if (user.exists(album)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Album already exists.");
			alert.setContentText("Try entering a new album!");
			alert.showAndWait();
			return;
		} else {
			user.addAlbum(album);
			update();
			tfNewAlbum.clear();
		}
		User.save(user);	
	}
	
	/**
	 * renameAlbum is a void method that is used to rename an album
	 * @throws IOException
	 */
	public void renameAlbum() throws IOException {
		String newName = tfAlbumName.getText().trim();

		int index = listview.getSelectionModel().getSelectedIndex();
		Album album = user.getAlbum(index);
		Optional<ButtonType> result;
		Album tempAlbum = new Album(newName);
		
		if (newName.length() == 0) {
			Alert alert2 = new Alert(AlertType.ERROR);
			alert2.setTitle("Rename Error");
			alert2.setContentText("Please enter a valid album name.");
			alert2.showAndWait();
			return;
		} else if (newName.equals(album.albumName)) {
			Alert alert2 = new Alert(AlertType.ERROR);
			alert2.setTitle("Rename Error");
			alert2.setContentText("No changes made. Please enter a valid album name before clicking 'Rename'.");
			alert2.showAndWait();
			return;
		} else if (user.exists(tempAlbum)) {
			Alert alert2 = new Alert(AlertType.ERROR);
			alert2.setTitle("Rename Error");
			alert2.setContentText("Album name already in use.");
			alert2.showAndWait();
			return;
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirm Rename");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure you want to rename this album?");
			result = alert.showAndWait();
		}
		
		if (result.get() == ButtonType.OK) {
			album.rename(newName);
			update();
			User.save(user);
		} else {
			return;
		}
		return;
	}
	
	/**
	 * openAlbum is a void method that opens a new photo view scene
	 * @param event
	 * @throws IOException
	 */
	public void openAlbum(ActionEvent event) throws IOException {
		AlbumViewController.user = user;
		AlbumViewController.album = listview.getSelectionModel().getSelectedItem();
		AlbumViewController.albumList = albumList;

		int albIndex = listview.getSelectionModel().getSelectedIndex();
		int currUserIndex = adminUser.getUserIndex();
		if(adminUser.getUsers().get(currUserIndex).getAlbums().size() == 0) { 
		      Alert alert = new Alert(AlertType.ERROR); 
		      alert.setTitle("Empty Deletion"); 
		      alert.setHeaderText(null); 
		      alert.setContentText("Cannot delete nothing"); 
		      alert.showAndWait(); 
		      return; 
		    } 
		Album album = adminUser.getUsers().get(currUserIndex).getAlbums().get(albIndex);
		
		adminUser.getUsers().get(currUserIndex).setCurrentAlbum(album);
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/AlbumView.fxml"));
		Parent sceneManager = (Parent) fxmlLoader.load();
		AlbumViewController albumController = fxmlLoader.getController();
		Scene adminScene = new Scene(sceneManager);
		Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		albumController.start(appStage);
		appStage.setScene(adminScene);
		appStage.show();
	}
	
	/**
	 * deleteAlbum is a void method that deletes an album from the list of users
	 * @throws IOException
	 */
	public void deleteAlbum() throws IOException {
		int index = listview.getSelectionModel().getSelectedIndex();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm Delete");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure you want to delete this album?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			user.deleteAlbum(index);
			update();
			User.save(user);
			   
			if (user.getAlbums().size() == 0) {
				mDeleteAlbum.setVisible(false);
		    } else {
		    	int lastuserindex = user.getAlbums().size();
				if (user.getAlbums().size() == 1) {
					listview.getSelectionModel().select(0);
				} else if (index == lastuserindex) {
					listview.getSelectionModel().select(lastuserindex-1);
				} else { 
					listview.getSelectionModel().select(index);
				}
			}
		} else {
			return;
		}
		return;
	}
	
	/**
	 * serach is a void method that opens a new scene (redirects user) to the search page
	 * @param event
	 * @throws IOException
	 */
	public void search(ActionEvent event) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Search.fxml"));
		Parent sceneManager = (Parent) fxmlLoader.load();
		SearchController searchController = fxmlLoader.getController();
		Scene adminScene = new Scene(sceneManager);
		Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		searchController.start();
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
	
	/**
	 * update is a void method that updates the album properties in album list upon changes such as add, delete, and rename
	 */
	public void update() {
		tUser.setText(username + "'s Album List:");
		// tfAlbumName.setText(listview.getSelectionModel().getSelectedItem());
		user = adminUser.getUser(username);
		
		albumList.clear();
		for (int i = 0; i < user.getAlbums().size(); i++) {
			albumList.add(user.getAlbums().get(i));
		}
		observableList = FXCollections.observableArrayList(albumList);
		listview.setItems(observableList);
		listview.refresh();
	}
}