/*	Guardian Lite - an Android reader for the Guardian newspaper.
 *	Copyright (C) 2011  Eel Pie Consulting Limited
 *
 *	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.	*/

package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nz.gen.wellington.guardian.android.utils.DateTimeHelper;

public class Article implements Serializable {

	private static final int ARTICLE_MAIN_PICTURE_WIDTH = 460;

	private static final long serialVersionUID = 10L;
	
	private String id;
	private String title;
	private String byline;
	private Date pubDate;
	private String standfirst;
	private String description;
	private String thumbnail;
	
	private Section section;
	
	private List<Tag> tags;
	private String webUrl;
	private String shortUrl;
	
	private boolean isRedistributionAllowed;
	
	private List<MediaElement> mediaElements;
 	
	public Article() {
		tags = new ArrayList<Tag>();
		mediaElements = new ArrayList<MediaElement>();
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
	
	public List<Tag> getTags() {
		return tags;
	}

	public void addTag(Tag tag) {
		tags.add(tag);
	}
	
	public String getMainImageUrl() {
		if (getMainPictureMediaElement() != null) {
			return getMainPictureMediaElement().getFile();
		}
		return null;
	}
	
	public String getCaption() {
		if (getMainPictureMediaElement() != null) {
			return getMainPictureMediaElement().getCaption();
		}
		return null;
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
	
	public boolean isRedistributionAllowed() {
		return isRedistributionAllowed;
	}

	public void setRedistributionAllowed(boolean isRedistributionAllowed) {
		this.isRedistributionAllowed = isRedistributionAllowed;
	}
	
	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}
		
	public String getTrailImageCallBackLabelForArticle() {
		return id != null ? id : title;
	}
	
	public void addMediaElement(MediaElement mediaElement) {
		mediaElements.add(mediaElement);
	}
	
	public List<MediaElement> getMediaElements() {
		return mediaElements;
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

	public boolean isGallery() {
		Tag contentTag = this.getContentTag();
		return contentTag != null && contentTag.getId().equals("type/gallery");
	}

	private Tag getContentTag() {
		for (Tag tag : this.tags) {
			if (tag.isContentTypeTag()) {
				return tag;
			}
		}
		return null;
	}
	
	public boolean isTagged() {
		if (tags.isEmpty()) {
			return false;
		}
		for (Tag tag : tags) {
			if (!tag.isContentTypeTag()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasSectionTag() {
		for (Tag tag : tags) {
			if (tag.isSectionKeyword()) {
				return true;
			}
		}
		return false;
	}

	public String getThumbnailUrl() {	// TODO rename to thumbnail to match content api
		return thumbnail;
	}

	public void setThumbnailUrl(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	private MediaElement getMainPictureMediaElement() {
		for (MediaElement mediaElement : mediaElements) {
			if (mediaElement.getType().equals("picture") && mediaElement.getWidth() == ARTICLE_MAIN_PICTURE_WIDTH) {
				return mediaElement;
			}
		}
		return null;
	}
	
}
