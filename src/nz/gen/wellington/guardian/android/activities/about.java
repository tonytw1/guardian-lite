package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.TopStoriesArticleSet;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class about extends ArticleListActivity {
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateArticlesHandler = new UpdateArticlesHandler(this, getArticleSet());
        
		setContentView(R.layout.about);
		setHeading("Guardian Lite - About");
		
		populateImage();
		
		TextView description = (TextView) findViewById(R.id.About);		
		description.setText("This unofficial application was developed by Tony McCrae of Eel Pie Consulting Limited.\n\n" +
				"Articles are retreived from the Guardian's RSS feeds. Tag information is supplied by the Guardian Content API.\n\n" +
				"For more information see:\nhttp://eelpieconsulting.co.uk/guardianlite\n\n" +
				"Application © 2010 Eel Pie Consulting Limited\n\n" +
				"Content © 2010 Guardian News and Media Limited");
		
		ImageView poweredByTheGuardian = (ImageView) findViewById(R.id.PoweredByTheGuardian);
		poweredByTheGuardian.setImageResource(R.drawable.poweredbyguardian);    	   	
	}
	
	
	private void populateImage() {
		ImageView slashImage = (ImageView) findViewById(R.id.SplashImage);
		slashImage.setImageResource(R.drawable.kingsplace_night);
		if (isDaylightInLondon()) {
			slashImage.setImageResource(R.drawable.kingsplace);
		} else {
			slashImage.setImageResource(R.drawable.kingsplace_night);
		}
	}

	private boolean isDaylightInLondon() {
		// TODO implement as an easter egg
		return false;
	}

	@Override
	protected ArticleSet getArticleSet() {
		return new TopStoriesArticleSet();
	}

	@Override
	protected String getRefinementDescription(String refinementType) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
