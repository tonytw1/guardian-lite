package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Section;
import android.os.Bundle;
import android.util.Log;

public class section extends ArticleListActivity {
	
	private Section section;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        section = (Section) this.getIntent().getExtras().get("section");
    	setHeading(section.getName());
    	setHeadingColour(section.getColour());
    	updateArticlesHandler = new UpdateArticlesHandler(this);
	}

	
	protected void onStart() {
		super.onStart();
		updateArticlesRunner = new UpdateArticlesRunner(ArticleDAOFactory.getDao(this), ArticleDAOFactory.getImageDao(this), null, section);
		Thread loader = new Thread(updateArticlesRunner);
		loader.start();
		Log.d("UpdateArticlesHandler", "Loader started");
	}

}