package nz.gen.wellington.guardian.android.activities.ui;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.model.Author;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListAuthorAdapter extends BaseAdapter {
	
	private List<Author> authors;
	private LayoutInflater mInflater;
	
	
    public ListAuthorAdapter(Context context, List<Author> authors) {
		super();
		mInflater = LayoutInflater.from(context);
		this.authors = authors;
	}
    
        
    public View getView(int position, View convertView, ViewGroup parent) {    	
    	View view;
    	
    	if (convertView == null) {
    		view = mInflater.inflate(R.layout.authorslist, null);    		
    		
    	} else {
    		view = convertView;
    	}
    	
    	TextView titleText = (TextView) view.findViewById(R.id.AuthorName);
    	titleText.setText(authors.get(position).getName());    	    	
    	ListAuthorClicker urlListener = new ListAuthorClicker(authors.get(position));
    	view.setOnClickListener(urlListener);
    	return view;
    }
       

	public int getCount() {
        return authors.size();
    }
    
    public Object getItem(int position) {
        return authors.get(position);
    }
    
    public long getItemId(int position) {
        return position;
    }

}
