package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;

public class Tag implements Serializable {
	
	private static final long serialVersionUID = 2L;
	private String name;
	private String id;

	public Tag(String name, String id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
