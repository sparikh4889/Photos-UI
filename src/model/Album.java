package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * Album class that users can add pictures to
 * @author Spandan Parikh (snp131) and Akshat Adlakha (aa2040)
 * 
 */
public class Album implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public static final String storeDir = "dat";
	public static final String storeFile = "users.dat";
	
	/**
	 * Name of the album
	 */
	public String albumName;
	
	/**
	 * List of photos stored in the current albums
	 */
	public ArrayList<Photo> photosList;
	
	/**
	 * Number of photos in a album; initially set to 0 as album is empty
	 */
	public int photoCount = 0;
	
	/**
	 * Current photo in the album
	 */
	public Photo currPhoto;
	
	/**
	 * Constructor for Album, initilalizes with album name and a list of photos
	 * @param albumName
	 */
	public Album(String albumName) {
		this.albumName = albumName; 
		photosList = new ArrayList<Photo>();
	}
    
    /**
	 * getAlbumName is of type string it gets the albumName
	 * @return albumName, this is the album name
	 */
	public String getAlbumName() {
		return albumName;
	}

	/**
	 * setAlbumName is a void method that sets the album name
	 * @param albumName
	 */
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
    	/**
	 * getName is of type String that gets the album name
	 * @return name of the album
	 */
	public String getName() {
		return this.albumName;
	}
	
	/**
	 * rename is a void method that renames a particular album
	 * @param name this is the name of the album that will be changed to
	 */
	public void rename(String name) {
		this.albumName = name;
	}
    
    /**
	 *  getPhotos gets the list of photos
	 * @return list of photos 
	 */
	public ArrayList<Photo> getPhotos() {
		return photosList;
	}
	
	/**
	 * getCurrentPhoto gets the current photo
	 * @return current photo
	 */
	public Photo getCurrentPhoto() {
		return currPhoto;
	}
	
	/**
	 * setCurrentPhoto is a void method taht sets current photo
	 * @param currentPhoto
	 */
	public void setCurrentPhoto(Photo currPhoto) {
		this.currPhoto = currPhoto;
	}
	
	/**
	 * addPhoto is a void method that adds a photo to an album
	 * @param photo its a photo
	 */
	public void addPhoto(Photo photo) {
		photosList.add(photo);
		photoCount++;
	}
	
	/**
	 * deletePhoto is a void method that deletes a specific photo referenced by the index
	 * @param index which photo within the photos list
	 */
	public void deletePhoto(int index) {
		photosList.remove(index);
		photoCount--;
	}
	
	/**
	 * exits is a boolean method that checks if a photo exists in an album
	 * @param fp, is the photo's file path
	 * @return true if it exists, false otherwise
	 */
	public boolean exists(String fp) {
		if ((photosList.size() > 0) && (!fp.isEmpty())) {
			for(Photo photos : photosList) {
				if (photos.getFilePath().equals(fp)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * getFirstDate has a return type of string, it returns when the photo was added
	 * @return dateStr, the date and time when the photo added
	 */
	public String getFirstDate() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("E, M-d-y 'at' h:m:s a");
		Date date = null; 
		String dateStr = "No Date";
		if (!photosList.isEmpty()) {
			date = this.getPhotos().get(0).date;
			for (Photo photo: photosList) {
				if (photo.date.before(date)) {
					date = photo.date;
				}
			}
			dateStr = dateFormatter.format(date);
		}
		
		return dateStr;
	}
	
	/**
	 * getLastDate has a return type of String and it returns the date when the photo was altered
	 * @return dateStr, is the date of last altered
	 */
	public String getLastDate() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("E, M-d-y 'at' h:m:s a");
		Date date = null; 
		String dateStr = "No Date";
		if (!photosList.isEmpty()) {
			date = this.getPhotos().get(0).date;
			for (Photo photo: photosList) {
				if (photo.date.after(date)) {
					date = photo.date;
				}
			}
			dateStr = dateFormatter.format(date);
		}
		
		return dateStr;
	}
	
	/**
	 * toString has return type String and prints name
	 */
	@Override
	public String toString() {
		return getName();
	}
	
	/**
	 * save is a static void method that save's the state to the .dat file
	 * @param pdApp
	 * @throws IOException
	 */
	public static void save(Album pdApp) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeDir + File.separator + storeFile));
		oos.writeObject(pdApp);
		oos.close();
	}
	
	/**
	 * load is a static method that loads data (list of users) from dat file
	 * @return list of users
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

