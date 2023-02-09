package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * @author Spandan Parikh (snp131) and Akshat Adlakha (aa2040)
 *
 */
public class User implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Current username
	 */
	public String username;
	
	/**
	 * List of albums according to users
	 */
	public ArrayList<Album> albums;
	
	/**
	 * Current Album
	 */
	public Album currentAlbum;
	public static final String storeDir = "dat";
	public static final String storeFile = "users.dat";

	/**
	 * Constructor User this initialzies the User with the given username and sets a list of albums of that user
	 * @param username this is the current username
	 */
	public User(String username) {
		this.username = username;
		albums = new ArrayList<Album>();
	}
	
	/**
	 * setUsername is a void method that sets username
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * getUsername is of type String that gets the username of the user  
	 * @return username the current user's username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * getAlbums gets the list of albums  
	 * @return a list of albums
	 */
	public ArrayList<Album> getAlbums() {
		return albums;
	}
	
	/**
	 * setAlbums is a void method that sets album lsit
	 * @param albums this is the list of album names
	 */
	public void setAlbums(ArrayList<Album> albums) {
		this.albums = albums;
	}
	
	/**
	 * getAlbum has a Album return type as it gets an album at a particular index
	 * @param index
	 * @return album in albums list
	 */
	public Album getAlbum(int index) {
		return albums.get(index);
	}
	
	/**
	 * setCurrentAlbum is a void method that sets the current album
	 * @param currentAlbum
	 */
	public void setCurrentAlbum(Album currentAlbum) {
		this.currentAlbum = currentAlbum;
	}
	
	/**
	 * getCurrentAlbum has a Album return type as it gets an album   
	 * @return current album
	 */
	public Album getCurrentAlbum() {
		return currentAlbum;
	}
	
	/**
	 * addAlbum is a void method that adds an album to the user's album list
	 * @param album
	 */
	public void addAlbum(Album album) {
		albums.add(album);
	}
	
	/**
	 * deleteAlbum is a void method that deletes an album
	 * @param index
	 */
	public void deleteAlbum(int index) {
		albums.remove(index);
	}
	
	/**
	 * printAlbums is a void method that iterates through the album list and 
	 * prints a list of albums
	 */
	public void printAlbums() {
		for (Album album : albums) {
			System.out.println(album.albumName);
		}
	}
	
	/**
	 * exists is a boolean method that check whether the album exists used as a flag for duplicate albums
	 * @param albumname this is the name of the album
	 * @return true if it exits already and false otherwise
	 */
	public boolean exists(Album albumname) {
		for(Album album : albums) {
			if (album.getName().equals(albumname.albumName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * getAndTaggedPhotos is a helper method for search by and (conjuctive search)
	 * @param taggedlist
	 * @return
	 */
	public ArrayList<Photo> getAndTaggedPhotos(ArrayList<Tag> taggedlist){
		System.out.println("Gettin And Photos");
		ArrayList<Photo> photolist = new ArrayList<Photo>();
		// ensuring no duplicates
		HashSet<Photo> check = new HashSet<Photo>();
		
		System.out.println(taggedlist);
			for(Album album : albums) {
				for(Photo photo : album.getPhotos()) {
					System.out.print(photo.getTagList());
					if (photo.getTagList().containsAll(taggedlist)) {
//						photolist.add(photo);
						check.add(photo);
					}
				}
				
		}
		photolist.addAll(check);
		return photolist;
	}
	
	/**
	 * getOrTaggedPhotos is used as a helper method to perform disjunctive (or) search for photos
	 * @param taggedlist this is a list of Tags
	 * @return a list of Photos
	 */
	public ArrayList<Photo> getOrTaggedPhotos(ArrayList<Tag> taggedlist){
		ArrayList<Photo> photolist = new ArrayList<Photo>();
		// ensuring no duplicates
		HashSet<Photo> check = new HashSet<Photo>();
		for(Tag tag : taggedlist) {
			for(Album album : albums) {
				for(Photo photo : album.getPhotos()) {
					if (photo.tagExists(tag.name, tag.value)) {
						check.add(photo);
					}
				}
				
			}
		}
		photolist.addAll(check);
		return photolist;
	}	
	
	/**
	 * Compares dates and returns a list of photos within the dated range
	 * @param fromDate start date
	 * @param toDate end date
	 * @return An ArrayList of type photo containing the list of photos within a certain range
	 */
	public ArrayList<Photo> getPhotosInRange(LocalDate fromDate, LocalDate toDate){
		ArrayList<Photo> inrange = new ArrayList<Photo>();
		Calendar startdate = Calendar.getInstance();
		startdate.set(fromDate.getYear(), fromDate.getMonthValue(), fromDate.getDayOfMonth());
		
		Calendar enddate = Calendar.getInstance();
		enddate.set(toDate.getYear(), toDate.getMonthValue(), toDate.getDayOfMonth());
		
		for(Album album : albums) {
			for(Photo photo : album.getPhotos()) {
				Date date = photo.getDate();
				Calendar pDate = Calendar.getInstance();
				pDate.setTime(date);
				
				Calendar today = Calendar.getInstance();
				
				int year = pDate.get(Calendar.YEAR);
				int month = pDate.get(Calendar.MONTH)+1;
				int dateOfMonth = pDate.get(Calendar.DAY_OF_MONTH);
				
				today.set(year, month, dateOfMonth);
				if (((today.compareTo(startdate) > 0) && (today.compareTo(enddate) < 0)) || (today.equals(startdate)) || (today.equals(enddate))) {
					inrange.add(photo);
				}
			}
		}
		return inrange;
	}
	
	/**
	 * save is a static void method that save's the state to the .dat file
	 * @param pdApp
	 * @throws IOException
	 */
	public static void save(User pdApp) throws IOException {
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
	public static User load() throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeDir + File.separator + storeFile));
		User userList = (User) ois.readObject();
		ois.close();
		return userList;
	}
}