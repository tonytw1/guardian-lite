/*	Guardian Lite - an Android reader for the Guardian newspaper.
 *	Copyright (C) 2011  Eel Pie Consulting Limited
 *
 *	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.	*/

package nz.gen.wellington.guardian.android.activities.ui;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.SectionColourMap;
import nz.gen.wellington.guardian.android.model.TagArticleSet;
import nz.gen.wellington.guardian.android.model.colourscheme.ColourScheme;
import nz.gen.wellington.guardian.model.Article;
import nz.gen.wellington.guardian.model.Section;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
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
	private ImageStretchingService imageStretchingService;

	public ArticleListActivityViewPopulator(Context context) {
		this.context = context;
		this.imageDAO = SingletonFactory.getImageDao(context);
		this.tagListPopulatingService = SingletonFactory.getTagListPopulator(context);
		imageStretchingService = new ImageStretchingService();
	}
			
	public View populateArticleListView(Article article, ColourScheme colourScheme, float baseFontSize, String trailImageUrl, boolean shouldUseFeatureTrail, boolean first, LayoutInflater mInflater, boolean isTrailImageAvailableLocally) {
		View view = chooseTrailView(mInflater, shouldUseFeatureTrail, first);
		
		TextView titleText = (TextView) view.findViewById(R.id.Headline);
		TextView pubDateText = (TextView) view.findViewById(R.id.Pubdate);
		TextView standfirst = (TextView) view.findViewById(R.id.Standfirst);
		TextView caption = (TextView) view.findViewById(R.id.Caption);
		
		titleText.setTextColor(colourScheme.getHeadline());
		pubDateText.setTextColor(colourScheme.getBodytext());			
		standfirst.setTextColor(colourScheme.getBodytext());
		
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseFontSize);
		pubDateText.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseFontSize -2);
		standfirst.setTextSize(TypedValue.COMPLEX_UNIT_PT, new Float(baseFontSize - 0.75));

		if (caption != null) {
			caption.setTextColor(colourScheme.getBodytext());
		}
		titleText.setText(article.getHeadline());			
		if (article.getPubDate() != null) {
			pubDateText.setText(DateFormatter.formatAsWebPublicationDate(article.getPubDate()));
		}
		
		if (article.getStandfirst() != null) {
			standfirst.setText(article.getStandfirst());
		}
					
		if (caption != null && article.getCaption() != null) {
			caption.setText(article.getCaption());
			caption.setVisibility(View.VISIBLE);
		}
		
		if (trailImageUrl != null && isTrailImageAvailableLocally) {
			populateTrailImage(trailImageUrl, view);
		}
		
		view.setOnClickListener(new ContentClicker(article));
		return view;
	}
	
	
	private View chooseTrailView(LayoutInflater mInflater, boolean shouldUseFeatureTrail, boolean hideDivider) {
		View view;
		if (shouldUseFeatureTrail) {
			view = mInflater.inflate(R.layout.featurelist, null);
		} else {
			view = mInflater.inflate(R.layout.list, null);
		}
		if (hideDivider) {
			View divider = view.findViewById(R.id.Divider);
			divider.setVisibility(View.GONE);
		}
		return view;
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
			ImageView trailImageView = (ImageView) trailView.findViewById(R.id.TrailImage);			
			Bitmap image = imageDAO.getImage(url);
			if (image != null) {
				scaleAndPopulateTrailImage(trailView, trailImageView, image);
			}
		}
	}

	
	public void populateRefinementType(LinearLayout mainpane, LayoutInflater inflater, String description, List<ArticleSet> refinementArticleSets, ColourScheme colourScheme, int fontSize) {
		View refinementsHeadingView = inflater.inflate(R.layout.refinements, null);			
		TextView descriptionView = (TextView) refinementsHeadingView.findViewById(R.id.RefinementsDescription);
		descriptionView.setText(description);
		descriptionView.setTextSize(TypedValue.COMPLEX_UNIT_PT, fontSize-1);
		descriptionView.setTextColor(colourScheme.getBodytext());
		descriptionView.setPadding(2, 3, 2, 3);
		mainpane.addView(refinementsHeadingView);
		
		// TODO move to a layout
		LinearLayout tagGroup = new LinearLayout(context);
		tagGroup.setOrientation(LinearLayout.VERTICAL);
		tagGroup.setPadding(2, 0, 2, 0);
		
		tagListPopulatingService.populateTags(inflater, true, tagGroup, refinementArticleSets, colourScheme, fontSize);
		mainpane.addView(tagGroup);
	}
	
	
	public void addSeperator(LayoutInflater mInflater, LinearLayout mainpane, ArticleSet articleSetForSection, boolean contentIsAvailable, ColourScheme colourScheme, int fontSize) {
		View seperator = mInflater.inflate(R.layout.seperator, null);
		
		final Section section = ((TagArticleSet) articleSetForSection).getSection();
		seperator.setBackgroundColor(Color.parseColor(SectionColourMap.getColourForSection(section.getId())));		

		TextView heading = (TextView) seperator.findViewById(R.id.TagName);
		heading.setText(section.getName());
		heading.setTextSize(TypedValue.COMPLEX_UNIT_PT, fontSize);
			
		ClickerPopulatingService.populateTagClicker(articleSetForSection, seperator, contentIsAvailable, colourScheme.getAvailableTagOnSeperator(), colourScheme.getUnavailableTagOnSeperator());
		mainpane.addView(seperator);		
	}
	
		
	private void scaleAndPopulateTrailImage(View trailView, ImageView trailImage, Bitmap image) {
		boolean isFeatureTrail = trailView.getId() == R.layout.featurelist;	// TODO may not be working
		if (isFeatureTrail) {
			int featureTrailImageWidth = trailImage.getWidth();	// TODO getWidth returns 0 for inflated views?
			trailImage.setImageBitmap(imageStretchingService.stretchImageToFillView(image, featureTrailImageWidth));					
		} else {
			trailImage.setImageBitmap(image);
		}
		trailImage.setVisibility(View.VISIBLE);
	}
	
}
