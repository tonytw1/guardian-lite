package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class MenuedActivity extends Activity {
	
	protected static final int HOME = 1000;
	protected static final int FAVOURITES = 1001;
	protected final static int REFRESH = 1002;
	protected static final int SECTIONS = 1003;
	protected static final int ABOUT = 1004;
	protected static final int SYNC = 1005;
	protected static final int SETTING = 1006;
	protected static final int SAVE_REMOVE_ARTICLE = 1007;
	protected static final int BROWSER = 1008;
	protected static final int SHARE = 1009;
	protected static final int SAVED = 1010;
	protected static final int REMOVE_ALL_SAVED = 1011;
	protected static final int ADD_REMOVE_FAVOURITE = 1012;
	protected static final int SEARCH_TAGS = 1013;
	
	protected final void swichToSync() {
		Intent intent = new Intent(this, sync.class);
		this.startActivity(intent);	
	}
	
	protected final void switchToSections() {
		Intent intent = new Intent(this, sections.class);
		this.startActivity(intent);		
	}
	
	protected final void switchToFavourites() {
		Intent intent = new Intent(this, favourites.class);
		this.startActivity(intent);		
	}
	
	protected final void switchToSettings() {
		Intent intent = new Intent(this, preferences.class);
		this.startActivity(intent);	
	}
	
	
	protected final void switchToAbout() {
		Intent intent = new Intent(this, about.class);
		this.startActivity(intent);
	}
	
	protected void switchToSavedArticles() {
		Intent intent = new Intent(this, savedArticles.class);
		this.startActivity(intent);
	}
	
	protected void switchToMain() {
		Intent intent = new Intent(this, main.class);
		this.startActivity(intent);	
	}
	
	protected void switchToTagSearch() {
		Intent intent = new Intent(this, tagsearch.class);
		this.startActivity(intent);
	}
	
	protected final void setHeading(String headingText) {
		TextView heading = (TextView) findViewById(R.id.Heading);
		heading.setText(headingText);		
	}
	
	protected final void hideHeading() {
		LinearLayout heading = (LinearLayout) findViewById(R.id.HeadingLayout);
		heading.setVisibility(View.GONE);
	}
	
	protected final void setHeadingColour(String colour) {
		LinearLayout heading = (LinearLayout) findViewById(R.id.HeadingLayout);
		heading.setBackgroundColor(Color.parseColor(colour));
	}
		
}
