package nz.gen.wellington.guardian.android.model;

import java.io.Serializable;
import java.util.List;

public interface ArticleSet extends Serializable {
	
	public String getName();
	
	public List<String> getPermittedRefinements();
	
	public int getPageSize();
	
}
