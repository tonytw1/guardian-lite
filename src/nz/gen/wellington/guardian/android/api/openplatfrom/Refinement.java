package nz.gen.wellington.guardian.android.api.openplatfrom;

import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;

public class Refinement {

	private Tag tag;
	private String date;

	public Refinement(Tag tag) {
		this.tag = tag;
	}

	public Refinement(Section section) {
		// TODO Auto-generated constructor stub
	}

	public Refinement(String date) {
		this.date = date;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
}
