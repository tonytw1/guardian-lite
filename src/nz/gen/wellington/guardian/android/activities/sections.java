package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.ListSectionsAdapter;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Section;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class sections extends Activity {
	
	ListAdapter adapter;
	
	public sections() {
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sections);
        
        List<Section> sections = ArticleDAOFactory.getDao(this).getSections();
        if (sections != null) {
        	ListView listView = (ListView) findViewById(R.id.SectionsListView);    		   
        	adapter = new ListSectionsAdapter(this, sections);
        	listView.setAdapter(adapter);
        	
        } else {
        	Toast.makeText(this, "Could not load sections", Toast.LENGTH_SHORT).show();
        }
	}

}