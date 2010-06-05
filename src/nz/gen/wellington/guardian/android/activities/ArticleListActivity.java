package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.ArticleImageDecorator;
import nz.gen.wellington.guardian.android.activities.ui.ListArticleAdapter;
import nz.gen.wellington.guardian.android.model.Article;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

public abstract class ArticleListActivity extends Activity {
		
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        

        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
	}
	
	protected void populateNewsitemList(List<Article> articles) {
		if (articles != null) {			
			List<Article> newsitems = articles;	
			ListView listView = (ListView) findViewById(R.id.ArticlesListView);    		   
			ListAdapter adapter = new ListArticleAdapter(this, ArticleImageDecorator.decorateNewsitemsWithThumbnails(newsitems, this));		   
			listView.setAdapter(adapter);
		}
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
