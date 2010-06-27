package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.TagListPopulatingService;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.network.NetworkStatusService;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

public class sections extends Activity {
	
	ListAdapter adapter;
	private ArticleDAO articleDAO;

	public sections() {
		articleDAO = ArticleDAOFactory.getDao(this.getApplicationContext());
	}
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onStart();		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sections);
		
		setHeading("Sections");
		setHeadingColour("#0061A6");
		
		LinearLayout mainPane = (LinearLayout) findViewById(R.id.MainPane);
		mainPane.removeAllViews();
		populateSections();        
	}
	
		
	private void populateSections() {
		List<Section> favouriteSections = articleDAO.getSections();
		
		LayoutInflater inflater = LayoutInflater.from(this);		
		LinearLayout authorList = (LinearLayout) findViewById(R.id.MainPane);
		
		boolean connectionIsAvailable = new NetworkStatusService(this.getApplicationContext()).isConnectionAvailable();
		TagListPopulatingService.populateSections(inflater, connectionIsAvailable, authorList, favouriteSections, this.getApplicationContext());
	}
	
	
	// TODO duplication
	protected void setHeading(String headingText) {
		TextView heading = (TextView) findViewById(R.id.Heading);
		heading.setText(headingText);		
	}
	// TODO duplication
	protected void setHeadingColour(String colour) {
		LinearLayout heading = (LinearLayout) findViewById(R.id.HeadingLayout);
		heading.setBackgroundColor(Color.parseColor(colour));
	}

}