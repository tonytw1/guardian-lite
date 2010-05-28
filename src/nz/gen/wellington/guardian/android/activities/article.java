package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.activities.ui.ListAuthorAdapter;
import nz.gen.wellington.guardian.android.activities.ui.ListKeywordAdapter;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.model.Article;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class article extends Activity {
	
	ListAdapter adapter;
	
	public article() {
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.article);
		
		Article article = (Article) this.getIntent().getExtras().get("article");		
		if (article != null) {
			populateArticle(article);
		} else {
        	Toast.makeText(this, "Could not load article", Toast.LENGTH_SHORT).show();
		}			
	}

		
	private void populateArticle(Article article) {	
        TextView headline = (TextView) findViewById(R.id.Headline);
        TextView pubDate = (TextView) findViewById(R.id.PubDate);
        
        TextView standfirst = (TextView) findViewById(R.id.Standfirst);
        TextView description = (TextView) findViewById(R.id.Description);
        
        headline.setText(article.getTitle());
        if (article.getPubDate() != null) {
        	pubDate.setText(article.getPubDate().toString("dd MMM yyyy HH:mm"));
        }
        standfirst.setText(article.getStandfirst());
        description.setText(article.getDescription());
        
        ImageDAO imageDAO = new ImageDAO();
    	ImageView imageView = (ImageView) findViewById(R.id.ArticleImage);
    	if (article.getThumbnailUrl() != null) {
    		Bitmap bitmap = imageDAO.getImage(article.getThumbnailUrl());
    		if (bitmap != null) {
    			imageView.setImageBitmap(bitmap);    			    			
    		}
    	}
        
		ListView authorListView = (ListView) findViewById(R.id.AuthorsList);    		   
		adapter = new ListAuthorAdapter(this, article.getAuthors());		   
		authorListView.setAdapter(adapter);
		
		ListView tagListView = (ListView) findViewById(R.id.TagList);
		adapter = new ListKeywordAdapter(this, article.getKeywords());		   
		tagListView.setAdapter(adapter);
	}

}
