package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;

public class Section implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	String name;
	String apiUrl;
	
	public Section(String name, String apiUrl) {
		this.name = name;
		this.apiUrl = apiUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}
	
}
