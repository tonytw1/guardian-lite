package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class MenuedActivity extends Activity {

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
	
	protected final void switchToPreferences() {
		Intent intent = new Intent(this, perferences.class);
		this.startActivity(intent);	
	}
	
	protected void switchToMain() {
		Intent intent = new Intent(this, main.class);
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
