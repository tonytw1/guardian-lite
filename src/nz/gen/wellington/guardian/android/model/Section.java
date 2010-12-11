package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;

public class Section implements Serializable {
	
	private static final long serialVersionUID = 2L;
	
	private String id;
	private String name;
	private String colour;
	
	public Section(String id, String name, String colour) {
		this.id = id;
		this.name = name;
		this.colour = colour;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String apiUrl) {
		this.id = apiUrl;
	}

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}
	
}
