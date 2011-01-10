package nz.gen.wellington.guardian.android.activities.ui;

import nz.gen.wellington.guardian.android.activities.search;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class SearchTermClicker implements OnClickListener {

	private String searchTerm;

	public SearchTermClicker(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public void onClick(View v) {
		Intent intent = new Intent(v.getContext(), search.class);
		intent.putExtra("searchterm", searchTerm);
		v.getContext().startActivity(intent);
	}

}
