package nz.gen.wellington.guardian.android.api.openplatfrom;

import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;

public class Refinement {

	private String displayName;
	private Tag tag;
	private String fromDate;
	private String toDate;

	public Refinement(Tag tag) {
		this.tag = tag;
	}

	public Refinement(Section section) {
		// TODO Auto-generated constructor stub
	}

	public Refinement(String displayName, String fromDate, String toDate) {
		this.displayName = displayName;
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public String getFromDate() {
		return fromDate;
	}

	public String getToDate() {
		return toDate;
	}
	
}
