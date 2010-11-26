package nz.gen.wellington.guardian.android.activities.ui;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.SectionArticleSet;
import nz.gen.wellington.guardian.android.model.TagArticleSet;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

public class ClickerPopulatingService {
		
	public static void populateClicker(ArticleSet articleSet, View tagView, boolean contentIsAvailable) {
		TextView titleText = (TextView) tagView.findViewById(R.id.TagName);
		if (contentIsAvailable) {
			
			if (articleSet instanceof SectionArticleSet) {
				SectionClicker clicker = new SectionClicker(((SectionArticleSet) articleSet).getSection());
				tagView.setOnClickListener(clicker);

			} else if (contentIsAvailable) {
				ListKeywordClicker clicker = new ListKeywordClicker(((TagArticleSet) articleSet).getTag());
				tagView.setOnClickListener(clicker);
			}
			
		} else {
			titleText.setTextColor(Color.DKGRAY);
		}		
	}
	
}
