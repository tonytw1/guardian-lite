package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;

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
	
		
	@Override
	// TODO this works but is this the correct way todo it.
	protected void onResume() {
		super.onResume();
		Thread loader = new Thread(new UpdateArticlesRunner(ArticleDAOFactory.getDao(this), ArticleDAOFactory.getImageDao(this), keyword, null));
		loader.start();
		Log.d("UpdateArticlesHandler", "Loader started");
	}

	
}
