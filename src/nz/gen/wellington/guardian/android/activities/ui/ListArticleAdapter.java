package nz.gen.wellington.guardian.android.activities.ui;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.model.ImageDecoratedArticle;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListArticleAdapter extends BaseAdapter {
	
	private List<ImageDecoratedArticle> articles;
	private LayoutInflater mInflater;
	
	
    public ListArticleAdapter(Context context, List<ImageDecoratedArticle> articles) {
		super();
		mInflater = LayoutInflater.from(context);
		this.articles = articles;
	}
    
        
    public View getView(int position, View convertView, ViewGroup parent) {    	
    	View view;
    	
    	if (convertView == null) {
    		view = mInflater.inflate(R.layout.list, null);    		    		
    	} else {
    		view = convertView;
    	}
    	
    	TextView titleText = (TextView) view.findViewById(R.id.TextView01);
    	ImageDecoratedArticle article = articles.get(position);
		titleText.setText(article.getTitle());
       	
    	TextView pubDateText = (TextView) view.findViewById(R.id.TextView02);
    	if (article.getPubDate() != null) {
    		pubDateText.setText(article.getPubDate().toString("dd MMM yyyy HH:mm"));
    	}
    	
    	ImageView imageView = (ImageView) view.findViewById(R.id.TrailImage);
    	getArticleThumbnail(article, imageView);
    	
    	ArticleClicker urlListener = new ArticleClicker(article.getArticle());
    	view.setOnClickListener(urlListener);
    	return view;
    }


	private void getArticleThumbnail(ImageDecoratedArticle article, ImageView imageView) {
    	if (article.getThumbnail() != null) {
    		imageView.setImageBitmap(article.getThumbnail());
    		return;    			
    	}    	
    	imageView.setImageResource(R.drawable.icon); 		
	}
       

	public int getCount() {
        return articles.size();
    }
    
    public Object getItem(int position) {
        return articles.get(position);
    }
    
    public long getItemId(int position) {
        return position;
    }
    
}
