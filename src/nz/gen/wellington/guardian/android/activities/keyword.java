package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

public class keyword extends ArticleListActivity {

	ListAdapter adapter;
	Tag keyword;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		keyword = (Tag) this.getIntent().getExtras().get("keyword");		
		if (keyword.getSection() != null) {
			setHeading(keyword.getSection().getName() + " - " + keyword.getName());
			setHeadingColour(keyword.getSection().getColour());
		} else {
			setHeading(keyword.getName());
		}	
		updateArticlesHandler = new UpdateArticlesHandler(this);
	}
	
	
	protected void onStart() {
		super.onStart();
		ListView listView = (ListView) findViewById(R.id.ArticlesListView);
		if (listView.getAdapter() == null) {			
			updateArticlesRunner = new UpdateArticlesRunner(ArticleDAOFactory.getDao(this), ArticleDAOFactory.getImageDao(this), keyword, null, this);
			Thread loader = new Thread(updateArticlesRunner);
			loader.start();
			Log.d("UpdateArticlesHandler", "Loader started");
		}
	}
		
}
