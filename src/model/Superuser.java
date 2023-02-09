package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

import application.Photos;

/**
 * The admin class that controls functions for adding users
 * also manages the photo albums
 * @author Spandan Parikh (snp131) and Akshat Adlakha (aa2040)
 *
 */
public class Superuser implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String storeDir = "dat";
	public static final String storeFile = "users.dat";
	
	/**
	 * List of users
	 */
	public ArrayList<User> users;
	
	/**
	 * Current user
	 */
	public User currentUser;
	
	/**
	 * boolean to check if user is looged in or not
	 */
	public boolean loggedIn;

	/**
	 * Constructor for Superuser, initilzes a list of users and adds in admin
	 */
	public Superuser() {
		users = new ArrayList<User>();
		users.add(new User("admin"));
		this.currentUser = null;
		this.loggedIn = false;
	}
    
    /**
	 * setUsers is a void method that sets the user list
	 * @param users
	 */
	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}
    
    /**
	 * getUsers gets a list of users 
	 * @return list of users
	 */
	public ArrayList<User> getUsers(){
		return users;
	}
    
    /**
	 * getUser gets a user given its username
	 * @param username username
	 * @return User instance
	 */
	public User getUser(String username) {
		for(User user : users) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		
		return null;
	}
    
    /**
	 * setCurrent is a void method that sets the current user
	 * @param current
	 */
	public void setCurrent(User currentUser) {
		this.currentUser = currentUser;
	}
    
    /**
	 * getCurrent is of an user instance that gets the current user
	 * @return current user
	 */
	public User getCurrent() {
		return currentUser;
	}
    
    /**
	 * getUserIndex is of type int, it gets the current user's index
	 * @return index number of the user
	 */
	public int getUserIndex() {
		int index = 0;
		for(User user : users) {
			if (user.getUsername().equals(Photos.driver.getCurrent().getUsername())) {
				return index;
			}
			index++;
		}
		return -1;
	}
    
    /**
	 * exists is a boolean function that checks whether a username exists
	 * @param username
	 * @return true if exists and false otherwise
	 */
	public boolean exists(String username) {
		for(User user : users) {
			if (user.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * checkUser is a boolean method that checks if user exists
	 * @param user
	 * @return true if it exists and false otherwise
	 */
	public boolean checkUser(String user) {
		int index = 0;
		for(int i = 0; i < users.size(); i++) {
			if (users.get(i).getUsername().equals(user)) {
				index = i;
			}
		}
		
		if (index == -1) {
			return false;
		}
		this.setCurrent(users.get(index));
		this.loggedIn = true;
		return true;
	}
    
    /**
	 * addUser is a void method that adds a user to a list
	 * @param username this is the username of the user
	 */
	public void addUser(String username) {
		users.add(new User(username));
	}
    
    	// /**
	//  * deleteUser is a void method that deletes a user giben its username
	//  * @param username username
	//  */
	// public void deleteUser(String username) {
	// 	User temp = new User(username);
	// 	users.remove(temp);
	// }
	
	/**
	 * deleteUser is a void method that deletes a user at the index in the user list
	 * @param index int
	 */
	public void deleteUser(int index) {
		users.remove(index);
		System.out.println(users);
	}
    
    /**
	 * save is a static void method that save's the state to the .dat file
	 * @param pdApp
	 * @throws IOException
	 */
	public static void save(Superuser pdApp) throws IOException {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeDir + File.separator + storeFile));
			oos.writeObject(pdApp);
			oos.close();
	}
	
	/**
	 * load is a static method that loads data (list of users) from dat file
	 * @return userlist this is the list of users
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Superuser load() throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeDir + File.separator + storeFile));
		Superuser userList = (Superuser) ois.readObject();
		ois.close();
		return userList;
	}
}