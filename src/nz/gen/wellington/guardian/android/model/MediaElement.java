package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;

public class MediaElement implements Serializable {
	
	private static final long serialVersionUID = 2L;
	
	private String type;
	private String file;
	private String thumbnail;
	private String caption;
	private int width;

	public MediaElement(String type, String file) {
		this.type = type;
		this.file = file;
	}

	public MediaElement(MediaElement mediaElement) {
		this.type = mediaElement.getType();
		this.file = mediaElement.getFile();
		this.thumbnail = mediaElement.getThumbnail();
		this.caption = mediaElement.getCaption();
		this.width = mediaElement.getWidth();
	}

	public boolean isPicture() {
		return type.equals("picture");
	}
	
	public String getType() {
		return type;
	}

	public String getFile() {
		return file;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
}
