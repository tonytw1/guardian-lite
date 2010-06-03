package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;

public class Section implements Serializable {
	
	private static final long serialVersionUID = 2L;
	
	String id;
	String name;
	
	public Section(String name, String id) {
		this.name = name;
		this.id = id;
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
	
}
