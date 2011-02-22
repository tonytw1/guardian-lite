package nz.gen.wellington.guardian.android.factories;

import nz.gen.wellington.guardian.android.api.SectionDAO;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.model.Refinement;
import nz.gen.wellington.guardian.model.Section;
import nz.gen.wellington.guardian.model.Tag;
import android.content.Context;
import android.util.Log;

public class RefinementArticleSetFactory {
	
	private static final String TAG = "RefinementArticleSetFactory";
	
	private SectionDAO sectionDAO;
	private ArticleSetFactory articleSetFactory;

	public RefinementArticleSetFactory(Context context) {
		this.sectionDAO = SingletonFactory.getSectionDAO(context);
		this.articleSetFactory = SingletonFactory.getArticleSetFactory(context);
	}
	
	public ArticleSet getArticleSetForRefinement(Refinement refinement) {		
		if (refinement.getType() == null) {
			return null;
		}		
		Log.d(TAG, "Making article set for refinement: type='" + refinement.getType() + "' id='" + refinement.getId() + "'");
		
		final boolean isSectionBasedTagRefinement = refinement.getType().equals("keyword") || refinement.getType().equals("blog") || refinement.getType().equals("series");
		if (isSectionBasedTagRefinement) { 	
			final String sectionId = refinement.getId().split("/")[0];
			Section section = sectionDAO.getSectionById(sectionId);			
			final Tag refinementTag = new Tag(refinement.getDisplayName(), refinement.getId(), section, refinement.getType());		
			return articleSetFactory.getArticleSetForTag(refinementTag);
			
		} else if (refinement.getType().equals("contributor")) {
			final Tag refinementTag = new Tag(refinement.getDisplayName(), refinement.getId(), null, refinement.getType());		
			return articleSetFactory.getArticleSetForTag(refinementTag);
			
		} else if (refinement.getType().equals("date")) {
			/*
			 *  <refinement count="6" 
			 *  	refined-url="http://content.guardianapis.com/search?callback=jsonp1298191201356&format=xml&from-date=2011-02-20&order-by=newest&section=money&show-refinements=all&to-date=2011-02-20"  
			 *  	display-name="Today" id="date/today" api-ur
			 *  <refinement count="7" 
			 *  	refined-url="http://content.guardianapis.com/search?callback=jsonp1298191201357&format=xml&from-date=2011-02-20&order-by=newest&show-refinements=all&tag=money/money&to-date=2011-02-20"  
			 *  	display-name="Today" id="date/today" api-url="http://content.guardianapis.com/search?from-date=2011-02-20&to-date=2011-02-20"></refinement>
			 */
			
			
			//final Tag refinementTag = new Tag(refinement.getDisplayName(), refinement.getId(), section, refinement.getType());
			// TODO regex checking and extraction of these fields.
			//final String fromDate = refinement.getRefinedUrl().split("from-date=")[1].substring(0, 10);			
			//final String toDate = refinement.getRefinedUrl().split("to-date=")[1].substring(0, 10);

			//return getArticleSetForTag(refinementTag, refinement.getDisplayName(), fromDate, toDate);
		}
		
		return null;
	}
	
}
