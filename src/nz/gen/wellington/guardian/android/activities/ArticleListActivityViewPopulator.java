package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.ColourScheme;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ArticleListActivityViewPopulator {
	
	private Context context;
	private ImageDAO imageDAO;
	private TagListPopulatingService tagListPopulatingService;

	public ArticleListActivityViewPopulator(Context context) {
		this.context = context;
		this.imageDAO = SingletonFactory.getImageDao(context);
		this.tagListPopulatingService = SingletonFactory.getTagListPopulator(context);
	}

	public boolean populateTagDescription(LinearLayout mainpane, String descripton, int fontSize, ColourScheme colourScheme) {
		// TODO move to the layout file
		TextView descriptionView = new TextView(context);
		descriptionView.setId(R.id.Description);
		descriptionView.setText(descripton);
		descriptionView.setPadding(2, 3, 2, 15);
		mainpane.addView(descriptionView, 0);
		descriptionView.setTextSize(TypedValue.COMPLEX_UNIT_PT, fontSize);	// TODO duplicated setting code
		descriptionView.setLineSpacing(new Float(0), new Float(1.1));
		
		descriptionView.setTextColor(colourScheme.getBodytext());
		descriptionView.setPadding(2, 3, 2, 3);	
		return true;
	}
	
	
	public void populateTrailImage(final String url, View trailView) {
		if (imageDAO.isAvailableLocally(url)) {
			ImageView trialImage = (ImageView) trailView.findViewById(R.id.TrailImage);			
			Bitmap image = imageDAO.getImage(url);
			if (image != null) {
				trialImage.setImageBitmap(image);
				trialImage.setVisibility(View.VISIBLE);
			}
		}
	}
	
	
	// TODO could be pushed to a populator class
	public void populateRefinementType(LinearLayout mainpane, LayoutInflater inflater, String description, List<ArticleSet> refinementArticleSets, ColourScheme colourScheme) {
		View refinementsHeadingView = inflater.inflate(R.layout.refinements, null);			
		TextView descriptionView = (TextView) refinementsHeadingView.findViewById(R.id.RefinementsDescription);
		descriptionView.setText(description);
		descriptionView.setTextColor(colourScheme.getBodytext());
		descriptionView.setPadding(2, 3, 2, 3);
		mainpane.addView(refinementsHeadingView);
		
		// TODO move to a layout
		LinearLayout tagGroup = new LinearLayout(context);
		tagGroup.setOrientation(LinearLayout.VERTICAL);
		tagGroup.setPadding(2, 0, 2, 0);
		
		tagListPopulatingService.populateTags(inflater, true, tagGroup, refinementArticleSets, colourScheme);
		mainpane.addView(tagGroup);
	}
	
}
