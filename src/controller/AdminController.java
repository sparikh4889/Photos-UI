package controller;

import java.io.IOException;

import java.util.*;

import application.Photos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import model.*;


/**
 * Controller for the Admin Page 
 * @author Akshat Adlakha (aa2040) and Spandan Parikh (snp131)
 * 
 */
public class AdminController implements LogoutController {
	
	@FXML public ListView<String> listview;
	@FXML public Button mAdd, mDelete, mLogOut;
	@FXML public TextField tfUsername;
	
	/**
	 * An ArrayList of strings that stores the added users
	 */
	public static ArrayList<String> userList = new ArrayList<>();
	
	/**
	 * An observable list that helps display the list of users
	 */
	public ObservableList<String> observableList;
	
	/**
	 * An instance of the admin that is created to help keep track of current values
	 */
	public static Superuser adminUser = Photos.driver;
	
	/**
	 * On scene start, the listview is updated with the list of current users
	 */
	public void start() {
		update();
		if (!userList.isEmpty()) listview.getSelectionModel().select(0);
	}
	
	/**
	 * logOut is a void method that logs the current user out
	 * @param event
	 * @throws IOException
	 */
	public void logOut(ActionEvent event) throws IOException {
		logMeOut(event);
	}
	
	/**
	 * addUser is a void method that adds a user to the user list
	 * @param event
	 * @throws IOException
	 */
	public void addUser(ActionEvent event) throws IOException {
		String username = tfUsername.getText().trim();
		
		if ((username.isEmpty()) || (username == null)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Admin Error");
			alert.setContentText("Empty Field: Please enter a username.");
			alert.showAndWait();
			return;
		} else if (adminUser.exists(username)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Admin Error.");
			alert.setContentText("Username already exists. Try entering a new username!");
			alert.showAndWait();
			return;
		} else if (username.equals("admin")) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Admin Error");
			alert.setContentText("Cannot add 'admin' to Users.");
			alert.showAndWait();
			return;
		} else {
			adminUser.addUser(username);
			update();
			tfUsername.clear();
		}
		Superuser.save(adminUser);	
	}
	
	/**
	 * deleteUser is a void method that deletes a user from the user list
	 * @param event
	 * @throws IOException
	 */
	public void deleteUser(ActionEvent event) throws IOException {

		int index = listview.getSelectionModel().getSelectedIndex();
		int adminList = userList.indexOf("Admin");
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm Delete");
		alert.setHeaderText(null);
		alert.setContentText("Are you sure you want to delete this User?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
		    adminUser.deleteUser(index);
			update();
			Superuser.save(adminUser);
			   
			if (adminUser.getUsers().size() == 1) {
			    mDelete.setVisible(false);
		    } else {
		        int lastuserindex = adminUser.getUsers().size();
				if (adminUser.getUsers().size() == 1) {
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
	 * update is a void method that updates the visibile user list everytime a user is added or deleted
	 */
	public void update() {
		userList.clear();
		for (int i = 0; i < adminUser.getUsers().size(); i++) userList.add(adminUser.getUsers().get(i).getUsername());
		
		listview.refresh();
		observableList = FXCollections.observableArrayList(userList);
		listview.setItems(observableList);
		listview.refresh();
	}
}
