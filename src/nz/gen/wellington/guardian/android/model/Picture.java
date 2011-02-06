package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;

public class Picture implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final String thumbnail;
	private final String file;
	private final String caption;


	public Picture(String thumbnail, String file, String caption) {
		this.thumbnail = thumbnail;
		this.file = file;
		this.caption = caption;
	}

	public String getFile() {
		return file;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public String getCaption() {
		return caption;
	}
	
}
