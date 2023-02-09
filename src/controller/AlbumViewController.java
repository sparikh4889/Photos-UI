package controller;

import java.io.*;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Album;
import model.Photo;
import model.Superuser;
import model.User;

/**
 * Controller for the Album View Page
 * @author Akshat Adlahka (aa2040) and Spandan Parikh (snp131) 
 *
 */
public class AlbumViewController implements LogoutController {
	@FXML
	public ListView<Photo> listview;
	
	@FXML
	public ImageView displayArea;
	private Image image;
	
	@FXML
	public Button mLogOut, mBack, mAdd, mDelete, mSlideShow, mSearch, mDisplay, mCopy, mMove;
	
	@FXML
	public TextField tfCopy, tfMove;
	
	@FXML 
	public Text tCaption, tDate;

	/**
	 * ArrayList that stores the instance of a photo
	 */
	public static ArrayList<Photo> photoList = new ArrayList<>();
	
	/**
	 * An observable list that helps display the list of photos
	 */
	public ObservableList<Photo> observableList;	
	
	/**
	 * A Superuser instance that helps maintain the state of the program
	 */
	public static Superuser adminuser = Photos.driver;
	
	/**
	 * A User object that maintains current user
	 */
	public static User user;
	
	/**
	 * An album list that helps with moving an copying albums
	 */
	public static ArrayList<Album> albumList;
	
	/**
	 * Used to store the albums of a user
	 */
	public static Album album; 
	
	/**
	 * On scene start by refreshing the visible photo list with the thumbnails of those photos
	 * @param app_stage
	 * Takes in the previous Stage to help keep track of current albums
	 */
	public void start(Stage app_stage) {
		
		app_stage.setTitle(adminuser.getCurrent().getCurrentAlbum().getAlbumName() + " Album Page");
		displayArea.setFitHeight(175);
		displayArea.setFitWidth(300);
		displayArea.setPreserveRatio(false);
		System.out.println("User Page");
		update();
		if (adminuser.getCurrent().getCurrentAlbum().getPhotos().size() == 0) mDelete.setVisible(false);
		
		if (!photoList.isEmpty()) listview.getSelectionModel().select(0); // select first user
		
		if (photoList.size() > 0) {
			tCaption.setText("Caption: " + photoList.get(0).caption);
			tDate.setText("Date: " + photoList.get(0).date);
			displayThumbnail();
		}
		listview.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
			displayThumbnail();
			updateCaption();
		});
	}
	
	/**
	 * move is a void method that moves a photo from one album to another album
	 * @throws IOException
	 */
	public void move() throws IOException {
		String source = tfMove.getText().trim(); // album to move
		boolean contains = false;
		int albumIndex = 0;
		for (int x = 0; x < albumList.size(); x++) {
			Album temp = albumList.get(x);
			if (temp.getName().equals(source)) {
				contains = true;
				albumIndex = x;
			}
		}

		// Move implementation
		if (!source.isEmpty() && contains) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirm Logout");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure you want to move this photo to " + source + "?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) { 
				Album dest = albumList.get(albumIndex);
				Photo photo = listview.getSelectionModel().getSelectedItem();
				dest.addPhoto(photo);
				album.deletePhoto(listview.getSelectionModel().getSelectedIndex());
				
				dest.save(dest);
				album.save(album);
				update();
			} else {
				return;
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Move Photo Error");
			alert.setHeaderText("Album not found or does not exist.");
			alert.showAndWait();
			Optional<ButtonType> buttonClicked=alert.showAndWait();
			if (buttonClicked.get()==ButtonType.OK) {
				alert.close();
			} else {
				alert.close();
			}
			return;
		}
		System.out.println("move");
	}
	
	/**
	 * copy is a void method that copyies a photo into another album
	 * @throws IOException
	 */
	public void copy() throws IOException {
		String copyAlbum = tfCopy.getText().trim();
		boolean contains = false;
		int albumIndex = 0;
		for (int x = 0; x < albumList.size(); x++) {
			Album temp = albumList.get(x);
			if (temp.getName().equals(copyAlbum)) {
				contains = true;
				albumIndex = x;
			}
		}

		// Copy implementation
		if (!copyAlbum.isEmpty() && contains) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirm Logout");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure you want to copy this photo to " + copyAlbum + "?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) { 
				Album newAlbum = albumList.get(albumIndex);
				Photo photo = listview.getSelectionModel().getSelectedItem();
				newAlbum.addPhoto(photo);
				
				newAlbum.save(newAlbum);
			} else {
				return;
			}
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Copy Photo Error");
			alert.setHeaderText("Album not found or does not exist.");
			alert.showAndWait();
			Optional<ButtonType> buttonClicked = alert.showAndWait();
			if (buttonClicked.get() == ButtonType.OK) {
				alert.close();
			} else {
				alert.close();
			}
			return;
		}
		System.out.println("move");
	}
	
	/**
	 * displayThumbnail is a void method that displays a small image (thumbnail) of the current photo
	 */
	public void displayThumbnail() {
		Photo photo = listview.getSelectionModel().getSelectedItem();
		File file;
		if (photo != null) {
			
			file = photo.getPic();
			if ((adminuser.getCurrent().getUsername().equals("stock")) && (photo.isStock)) {
				String str = file.getAbsolutePath();
				int stkphoto = str.indexOf("stockphotos");
				String newfilepath = str.substring(stkphoto, str.length());
				File img = new File(newfilepath);
				Image image = new Image(img.toURI().toString());
				displayArea.setImage(image);
			} else {
				Image image = new Image(file.toURI().toString());
				displayArea.setImage(image);
			}	 
		} else {
			displayArea.setImage(null);
		}
		return;
	}
	
	/**
	 * updateCaption is a void method that allows the user to update the caption of a photo of their choosing
	 */
	public void updateCaption() {
		Photo photo = listview.getSelectionModel().getSelectedItem();
		if ((photoList.size() > 0) && (photo != null)) {
			tCaption.setText("Caption: " + photo.caption);
			tDate.setText("Date: " + photo.date);
		} else {
			tCaption.setText("Caption: ");
			tDate.setText("Date: ");
		}
	}
	
	/**
	 * addPhoto is a void method that adds a photo to the current album (visible in photo list)
	 * @throws IOException
	 */
	public void addPhoto() throws IOException {
		FileChooser filechooser = new FileChooser();
		// photo file types
		FileChooser.ExtensionFilter filterFiles = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif");
		filechooser.getExtensionFilters().add(filterFiles);
		File img = filechooser.showOpenDialog(null);
		
		if (img == null) {
			return;
		} else {
		    String src = img.getAbsolutePath();
		    Photo newPhoto;
		    if (adminuser.getCurrent().getUsername().equals("stock")) {
		        int index;
			    if (src.contains("stockphotos")) {
			        index = src.indexOf("stockphotos");
					String dest = src.substring(index, src.length());
					Photo newPhoto2 = new Photo(img, dest);
					newPhoto2.isStock = true;
					album.addPhoto(newPhoto2);
				} else {
				    newPhoto = new Photo(img, src);	
					album.addPhoto(newPhoto);
				}
			} else {
			    newPhoto = new Photo(img, src);	
				album.addPhoto(newPhoto);
			}
			update();
		}
		if (adminuser.getCurrent().getCurrentAlbum().getPhotos().size() > 0) mDelete.setVisible(true);
		
		if (!photoList.isEmpty()) listview.getSelectionModel().select(0); // select first user	
		Album.save(album);
	}
	
	/**
	 * deletePhoto is a void method that deletes the photo from the current album
	 * @throws IOException
	 */
	public void deletePhoto() throws IOException {

		int index = listview.getSelectionModel().getSelectedIndex();
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm Delete");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure you want to delete this photo?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			album.deletePhoto(index);
			update();
			   
			if (adminuser.getCurrent().getCurrentAlbum().getPhotos().size() == 0) {
				mDelete.setVisible(false);
		    } else {  
		    	int lastuserindex = album.getPhotos().size();
				if (album.getPhotos().size() == 1) {
					listview.getSelectionModel().select(0);
				} else if (index == lastuserindex) {
					listview.getSelectionModel().select(lastuserindex-1);
				} else { 
					listview.getSelectionModel().select(index);
				}
			}
			Album.save(album);
		} else {
			return;
		}
		return;
	}
	
	/**
	 * update is a void method that updates the visible list of photos anytime a photo is added or deleteed
	 */
	public void update() {
		photoList.clear();
		for (int i = 0; i < album.getPhotos().size(); i++) {
			photoList.add(album.getPhotos().get(i));
		}

		observableList = FXCollections.observableArrayList(photoList);
		listview.setItems(observableList);
		listview.refresh();
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
	 * display is a void method that showcases the photo in a better perspective, blows it up making it more visible
	 * Additionally, contains properties of the photo such as captions and list of tags (more can be added or removed)
	 * @param event
	 * @throws IOException
	 */
	public void display(ActionEvent event) throws IOException {
		if (photoList.size() > 0) {
			boolean checked = false;
			for (int x = 0; x < photoList.size(); x++) {
				if (listview.getSelectionModel().isSelected(x)) {
					checked = true;
				}
			}

			if (checked) {
				
				int pIndex = listview.getSelectionModel().getSelectedIndex();
				Photo currPhoto = adminuser.getCurrent().getCurrentAlbum().getPhotos().get(pIndex);
				adminuser.getCurrent().getCurrentAlbum().setCurrentPhoto(currPhoto);
				
				PhotoViewController.photo = listview.getSelectionModel().getSelectedItem();
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/PhotoView.fxml"));
				Parent sceneManager = (Parent) fxmlLoader.load();
				PhotoViewController PhotoViewController = fxmlLoader.getController();
				Scene adminScene = new Scene(sceneManager);
				Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				PhotoViewController.start(appStage);
				appStage.setScene(adminScene);
				appStage.show();	
			}
		}
	}
	
	/**
	 * slideshow is a void method that displays all the photos in the album in a different view (even better perspective)
	 * @param event
	 * @throws IOException
	 */
	public void slideshow(ActionEvent event) throws IOException {
		if (photoList.size() == 0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Slideshow Error");
			alert.setHeaderText("No Photos to Display.");
			alert.showAndWait();
			Optional<ButtonType> buttonClicked = alert.showAndWait();
			if (buttonClicked.get() == ButtonType.OK) {
				alert.close();
			} else {
				alert.close();
			}
		} else {
			SlideshowController.album = photoList;
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Slideshow.fxml"));
			Parent sceneManager = (Parent) fxmlLoader.load();
			SlideshowController slideshowController = fxmlLoader.getController();
			Scene adminScene = new Scene(sceneManager);
			Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			slideshowController.start();
			appStage.setScene(adminScene);
			appStage.show();		
		}
	}
	
	
	/**
	 * back is a void method that redirects the user to the previous page --> the Album page
	 * @param event
	 * @throws IOException
	 */
	public void back(ActionEvent event) throws IOException {
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
	public void logOut(ActionEvent event) throws IOException {
		logMeOut(event);
	}

}