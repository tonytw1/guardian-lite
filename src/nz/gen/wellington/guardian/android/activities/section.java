package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.ArticleImageDecorator;
import nz.gen.wellington.guardian.android.activities.ui.ListArticleAdapter;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Article;
import nz.gen.wellington.guardian.android.model.Section;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class section extends Activity {
	
	ListAdapter adapter;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    	final Section section = (Section) this.getIntent().getExtras().get("section");
		this.setTitle(section.getName());
    	List<Article> articles = ArticleDAOFactory.getDao(this).getSectionItems(section);
    	populateNewsitemList(articles); 	
	}

	
	private void populateNewsitemList(List<Article> articles) {
		if (articles != null) {			
			List<Article> newsitems = articles;	
			ListView listView = (ListView) findViewById(R.id.ArticlesListView);    		   
			adapter = new ListArticleAdapter(this, ArticleImageDecorator.decorateNewsitemsWithThumbnails(newsitems));		   
			listView.setAdapter(adapter);
		} else {
			Toast.makeText(this, "Could not load section articles", Toast.LENGTH_SHORT).show();
		}
	}

}