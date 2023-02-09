package model;

import java.io.Serializable;

/**
 * 
 * This is the Tag class for users to tag photos in order to search or group tehm
 * @author Spandan Parikh (snp131) and Akshat Adlakha (aa2040)
 *
 */
public class Tag implements Serializable {
	public String name;
	public String value;
	
	public Tag(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Compares tag values
	 */
	@Override
	public boolean equals(Object o) {
		if ((o == null) || (!( o instanceof Tag) )) {
			return false;
		}
		Tag tag = (Tag) o;
		return tag.name.equals(this.name) && tag.value.equals(this.value);
	}
}