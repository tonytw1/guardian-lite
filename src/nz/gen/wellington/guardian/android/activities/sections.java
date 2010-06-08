package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.ListSectionsAdapter;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Section;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class sections extends Activity {
	
	ListAdapter adapter;
	
	public sections() {
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sections);
        
        setHeading("Sections");
    	setHeadingColour("#0061A6");
        
        List<Section> sections = ArticleDAOFactory.getDao(this).getSections();
        if (sections != null) {
        	ListView listView = (ListView) findViewById(R.id.SectionsListView);    		   
        	adapter = new ListSectionsAdapter(this, sections);
        	listView.setAdapter(adapter);
        	
        } else {
        	Toast.makeText(this, "Could not load sections", Toast.LENGTH_SHORT).show();
        }
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