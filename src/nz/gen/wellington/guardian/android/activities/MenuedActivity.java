package nz.gen.wellington.guardian.android.activities;

import android.app.Activity;
import android.content.Intent;

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
	
	
}
