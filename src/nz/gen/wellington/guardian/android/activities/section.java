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

	
	@Override
	// TODO this works but is this the correct way todo it.
	protected void onResume() {
		super.onResume();
		Thread loader = new Thread(new UpdateArticlesRunner(ArticleDAOFactory.getDao(this), ArticleDAOFactory.getImageDao(this), null, section));
		loader.start();
		Log.d("UpdateArticlesHandler", "Loader started");
	}
	

}