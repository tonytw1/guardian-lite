package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;

public class Author implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private String apiUrl;
	
	
	public Author(String name, String apiUrl) {
		this.name = name;
		this.apiUrl = apiUrl;
	}

	public String getName() {
		return name;
	}

	public String getApiUrl() {
		return apiUrl;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
