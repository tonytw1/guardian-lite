package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.List;

public interface ArticleSet extends Serializable {
	
	public String getName();	
	public String getShortName();
	public List<String> getPermittedRefinements();	
	public int getPageSize();
	public String getSourceUrl();
	void setSourceUrl(String urlForArticleSet);
	boolean isEmpty();
	boolean isFeatureTrailAllowed();
	public Section getSection();
	
}
