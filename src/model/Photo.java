package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

/**
 * Allows users to manipulate photos
 * @author Spandan Parikh (snp131) and Akshat Adlakha (aa2040)
 *
 */
public class Photo implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String storeDir = "dat";
	public static final String storeFile = "users.dat";
	
	/**
	 * Photo name
	 */
	public String photoName;
	
	/**
	 * file of the picture
	 */
	public File pic;
	
	/**
	 * List of tags
	 */
	public ArrayList<Tag> tagList;
	
	/**
	 * Photo caption
	 */
	public String caption;
	
	/**
	 * Filepath of photo
	 */
	public String filepath;
	
	/**
	 * Calendar instance
	 */
	public Calendar cal;
	
	/**
	 * Current date
	 */
	public Date date;
	
	/**
	 * Check if stock photo
	 */
	public boolean isStock = false;
	
	/**
	 * Constructor for Photo, initializes photoname and the taglist and the instance of calender (cal)
	 * @param pic this is the file
	 * @param photoname this is the name of the photo
	 */
	public Photo(File pic, String photoName) {
		this.photoName = photoName; 
		if (pic != null) this.pic = new File(photoName);
		else this.pic = pic;
		this.tagList = new ArrayList<Tag>();
		cal = new GregorianCalendar();
		cal.set(Calendar.MILLISECOND, 0);
		this.date = cal.getTime();
	}
    
    /**
	 * setFilePath is a void method that sets the file path
	 * @param fp
	 */
	public void setFilePath(String fp) {
		this.filepath = fp;
	}
	
	/**
	 * getFilePath is a String type that returns the file path of the photo 
	 * @return file path of photo
	 */
	public String getFilePath() {
		return filepath;
	}
	
	/**
	 * setPic is a void method that sets the picture
	 * @param pic
	 */
	public void setPic(File pic) {
		this.pic = pic;
	}
	
	/**
	 * getPic gets the picture
	 * @return picture
	 */
	public File getPic() {
		return this.pic;
	}
    
    /**
	 * getName gets the name of the photo
	 * @return name of photo
	 */
	public String getName() {
		return photoName;
	}
    
    /**
	 * setCaption is a void method that sets the caption for a photo, used as an action event in controller
	 * @param caption
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}
    
    /**
	 * getCaption gets the caption of the photo 
	 * @return caption of photo
	 */
	public String getCaption() {
		return caption;
	}
    
    /**
	 * getDate gets the date 
	 * @return Dates
	 */
	public Date getDate() {
		return date;
	}

	
	/**
	 * getTagList gets the tag list
	 * @return list of tags
	 */
	public ArrayList<Tag> getTagList(){
		return tagList;
	}
    
    /**
	 * addTag is a void method that adds tags to a photo, used as an action event in controller
	 * @param name name of tag
	 * @param value value of tag
	 */
	public void addTag(String name, String value) {
		tagList.add(new Tag(name, value));
	}
    
    /**
	 * deleteTag is a void method that deletes tag from the list
	 * @param name name of tag
	 * @param value value of tag
	 */
	public void deleteTag(String name, String value) {
		for(int i = 0; i < tagList.size(); i++) {
			Tag cur = tagList.get(i);
			if (cur.name.toLowerCase().equals(name.toLowerCase()) && cur.value.toLowerCase().equals(value.toLowerCase())) tagList.remove(i);
		}
	}
	
	/**
	 * tagExists is a boolean method that checks if a tag exists within the tag list
	 * @param name
	 * @param value
	 * @return
	 */
	public boolean tagExists(String name, String value) {
		for(int i = 0; i < tagList.size(); i++) {
			Tag cur = tagList.get(i);
			if (cur.name.toLowerCase().equals(name.toLowerCase()) && cur.value.toLowerCase().equals(value.toLowerCase())) return true;
		}
		return false;	
	}
    
    /**
	 * toString is a string type that prints out the name of the photo
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
	public static void save(Photo pdApp) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeDir + File.separator + storeFile));
		oos.writeObject(pdApp);
		oos.close();
	}
	
	/**
	 * load is a static method that loads user list from the dat file
	 * @return userList this is the list of users
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