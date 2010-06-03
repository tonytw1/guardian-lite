package nz.gen.wellington.guardian.android.activities.ui;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.model.Tag;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListKeywordAdapter extends BaseAdapter {
	
	private List<Tag> keywords;
	private LayoutInflater mInflater;
	
	
    public ListKeywordAdapter(Context context, List<Tag> keywords) {
		super();
		mInflater = LayoutInflater.from(context);
		this.keywords = keywords;
	}
    
    
    
  public View getView(int position, View convertView, ViewGroup parent) {    	
    	View view;    	
    	if (convertView == null) {
    		view = mInflater.inflate(R.layout.authorslist, null);    		
    	} else {
    		view = convertView;
    	}
    	
    	TextView titleText = (TextView) view.findViewById(R.id.AuthorName);
    	titleText.setText(keywords.get(position).getName());    	    	
    	ListKeywordClicker urlListener = new ListKeywordClicker(keywords.get(position));
    	view.setOnClickListener(urlListener);
    	return view;
    }
       

	public int getCount() {
        return keywords.size();
    }
    
    public Object getItem(int position) {
        return keywords.get(position);
    }
    
    public long getItemId(int position) {
        return position;
    }

}
