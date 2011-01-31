package nz.gen.wellington.guardian.android.model;

public class TagCombinerArticleSet extends AbstractArticleSet implements ArticleSet {

	private static final long serialVersionUID = 1L;
	private Tag leftTag;
	private Tag rightTag;
		
	public TagCombinerArticleSet(Tag leftTag, Tag rightTag, int pageSize) {
		super(pageSize);
		this.leftTag = leftTag;
		this.rightTag = rightTag;
	}

	@Override
	public String getName() {
		return leftTag.getName() + " + " + rightTag.getName();
	}
	
	@Override
	public Section getSection() {
		return leftTag.getSection();
	}
	
	@Deprecated
	public String getHeadingColour() {
		if (leftTag.getSection() != null) {
			return leftTag.getSection().getColour();
		}
		return null;
	}

	public Tag getLeftTag() {
		return leftTag;
	}

	public Tag getRightTag() {
		return rightTag;
	}
	
}
