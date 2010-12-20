package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;

public class Tag implements Serializable {
	
	private static final long serialVersionUID = 2L;
	private String name;
	private String id;
	private Section section;

	public Tag(String name, String id, Section section) {
		this.name = name;
		this.id = id;
		this.section = section;
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

	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}

	public boolean isSectionKeyword() {
		if (section != null) {
			final String sectionTagId = section.getId() + "/" + section.getId();
			return id.equals(sectionTagId);			
		}
		return false;
	}
	
}
