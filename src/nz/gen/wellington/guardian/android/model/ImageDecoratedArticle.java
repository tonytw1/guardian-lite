package nz.gen.wellington.guardian.android.model;

import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;

public class ImageDecoratedArticle extends Article {
	
	private static final long serialVersionUID = 2L;
	private Article article;
	private Bitmap thumbnail;

	public ImageDecoratedArticle(Article article, Bitmap thumbnail) {
		this.article = article;
		this.thumbnail = thumbnail;
	}
	
	public Article getArticle() {
		return article;
	}
	
	public String getAuthorNames() {
		return article.getAuthorNames();
	}

	public List<Tag> getAuthors() {
		return article.getAuthors();
	}

	public String getDescription() {
		return article.getDescription();
	}

	public String getId() {
		return article.getId();
	}

	public List<Tag> getTags() {
		return article.getTags();
	}

	public Date getPubDate() {
		return article.getPubDate();
	}
	
	public String getPubDateString() {
		return article.getPubDateString();
	}

	public String getStandfirst() {
		return article.getStandfirst();
	}

	public String getThumbnailUrl() {
		return article.getThumbnailUrl();
	}

	public String getTitle() {
		return article.getTitle();
	}

	
	public Bitmap getThumbnail() {
		return thumbnail;
	}

	public String getByline() {
		return article.getByline();		
	}

	public Section getSection() {
		return article.getSection();
	}
	
}
