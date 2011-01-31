package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.List;

public interface ArticleSet extends Serializable {
	
	String getName();	
	List<String> getPermittedRefinements();	
	int getPageSize();
	String getSourceUrl();
	void setSourceUrl(String urlForArticleSet);
	boolean isEmpty();
	boolean isFeatureTrailAllowed();
	public Section getSection();
	String getHeadingColour();
	
}
