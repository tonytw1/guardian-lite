package nz.gen.wellington.guardian.android.model;

import java.util.List;

import org.joda.time.DateTime;

import android.graphics.Bitmap;

public class ImageDecoratedArticle extends Article {
	
	private static final long serialVersionUID = 1L;
	Article article;
	Bitmap thumbnail;

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

	public List<Author> getAuthors() {
		return article.getAuthors();
	}

	public String getDescription() {
		return article.getDescription();
	}

	public String getId() {
		return article.getId();
	}

	public List<Keyword> getKeywords() {
		return article.getKeywords();
	}

	public DateTime getPubDate() {
		return article.getPubDate();
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

	public void setThumbnail(Bitmap thumbnail) {
		this.thumbnail = thumbnail;
	}
	
}
