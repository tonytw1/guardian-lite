package nz.gen.wellington.guardian.android.tagging;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.guardian.model.Article;
import nz.gen.wellington.guardian.model.Tag;

public class TagShufflingService {

	public List<Tag> shuffleContributorsToTheFrontAndAddSectionTagIfNotAlreadyPresent(Article article) {
		List<Tag> shuffledTags = new ArrayList<Tag>(article.getTags());
		
		addArticleSectionIfSectionTagNotAlreadyPresent(article, shuffledTags);
		
		final List<Tag> contributors = article.getContributorTags();
		shuffledTags.removeAll(contributors);
		shuffledTags.addAll(0, contributors);
		return shuffledTags;
	}
	
	private void addArticleSectionIfSectionTagNotAlreadyPresent(Article article, List<Tag> shuffledTags) {
		if (!article.hasSectionTag() && article.getSection() != null) {
			shuffledTags.add(0, article.getSection().getTag());			
		}
	}
}
