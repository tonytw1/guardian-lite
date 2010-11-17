package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.List;

public interface ArticleSet extends Serializable {
	
	public String getName();
	
	@Deprecated
	public String getApiUrl();
	
	public List<String> getPermittedRefinements();
	
}
