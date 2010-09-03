package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nz.gen.wellington.guardian.android.dates.DateTimeHelper;

public class Article implements Serializable {

	private static final long serialVersionUID = 6L;
	
	String id;
	String title;
	String byline;
	Date pubDate;
	String standfirst;
	String description;
	
	Section section;
	List<Tag> authors;
	List<Tag> keywords;
	String thumbnailUrl;
	String mainImageUrl;
	String caption;
	
	public Article() {
		authors = new ArrayList<Tag>();
		keywords = new ArrayList<Tag>();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public String getStandfirst() {
		return standfirst;
	}

	public void setStandfirst(String standfirst) {
		this.standfirst = standfirst;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date date) {
		this.pubDate = date;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Tag> getAuthors() {
		return authors;
	}
	
	public void addAuthor(Tag author) {
		authors.add(author);
	}
	
	public String getAuthorNames() {
		return authors.toString();
	}
	
	public List<Tag> getKeywords() {
		return keywords;
	}

	public void addKeyword(Tag keyword) {
		keywords.add(keyword);
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getMainImageUrl() {
		return mainImageUrl;
	}
	
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public void setMainImageUrl(String mainImageUrl) {
		this.mainImageUrl = mainImageUrl;
	}

	public String getPubDateString() {
		if (this.pubDate != null) {
			return DateTimeHelper.format(pubDate, "EEEE d MMMM yyyy HH.mm");
		}
		return null;
	}

	public String getByline() {
		return byline;
	}

	public void setByline(String byline) {
		this.byline = byline;
	}

	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Article other = (Article) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
