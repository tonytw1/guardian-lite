package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;

public class main extends ArticleListActivity {
	
	ListAdapter adapter;
	
	public main() {
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        setHeading("Most recent");
    	setHeadingColour("#0061A6");    	
    	updateArticlesHandler = new UpdateArticlesHandler(this);
	}
	
	
	@Override
	// TODO this works but is this the correct way todo it.
	protected void onResume() {
		super.onResume();
		Thread loader = new Thread(new UpdateArticlesRunner(ArticleDAOFactory.getDao(this), ArticleDAOFactory.getImageDao(this), null, null));
		loader.start();
		Log.d("UpdateArticlesHandler", "Loader started");
	}

	
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, 1, 0, "Sync");
	    menu.add(0, 2, 0, "Sections");
	    menu.add(0, 3, 0, "Settings");
	    return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {	   
	    case 1: 	    	
	    	swichToSync();
	        return true;
	    case 2: 	    	
	    	switchToSections();
	        return true;	 
	    case 3:
	    	switchToPreferences();
	    	return true;
	    }
	    return false;
	}


	private void swichToSync() {
		Intent intent = new Intent(this, sync.class);
		this.startActivity(intent);	
	}
	
	private void switchToSections() {
		Intent intent = new Intent(this, sections.class);
		this.startActivity(intent);		
	}
	
	private void switchToPreferences() {
		Intent intent = new Intent(this, perferences.class);
		this.startActivity(intent);	
	}
	
}