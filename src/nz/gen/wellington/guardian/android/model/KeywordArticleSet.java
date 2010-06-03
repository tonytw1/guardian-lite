package nz.gen.wellington.guardian.android.model;

public class KeywordArticleSet implements ArticleSet {
	
	private static final long serialVersionUID = 1L;
	private Tag keyword;

	public KeywordArticleSet(Tag keyword) {
		this.keyword = keyword;
	}

	@Override
	public String getName() {
		return keyword.getName();
	}
	
	@Override
	public String getApiUrl() {
		return keyword.getId();
	}


}
