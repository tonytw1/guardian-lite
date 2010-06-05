package nz.gen.wellington.guardian.android.activities.ui;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.model.Section;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListSectionsAdapter extends BaseAdapter {
	
	private List<Section> sections;
	private LayoutInflater mInflater;
	
	
    public ListSectionsAdapter(Context context, List<Section> sections) {
		super();
		mInflater = LayoutInflater.from(context);
		this.sections = sections;
	}
    
        
    public View getView(int position, View convertView, ViewGroup parent) {    	
    	View view;
    	
    	if (convertView == null) {
    		view = mInflater.inflate(R.layout.sectionslist, null);    		   		
    	} else {
    		view = convertView;
    	}
    	
    	TextView titleText = (TextView) view.findViewById(R.id.SectionName);
    	titleText.setText(sections.get(position).getName());
    	
    	ImageView imageView = (ImageView) view.findViewById(R.id.SectionImage);
    	imageView.setImageResource(R.drawable.icon);
    	    	
    	SectionClicker sectionClickListener = new SectionClicker(sections.get(position));
    	view.setOnClickListener(sectionClickListener);
    	return view;
    }
       

	public int getCount() {
        return sections.size();
    }
    
    public Object getItem(int position) {
        return sections.get(position);
    }
    
    public long getItemId(int position) {
        return position;
    }

}
