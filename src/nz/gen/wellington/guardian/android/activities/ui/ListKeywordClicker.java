package nz.gen.wellington.guardian.android.activities.ui;

import nz.gen.wellington.guardian.android.activities.tag;
import nz.gen.wellington.guardian.android.model.Tag;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class ListKeywordClicker implements OnClickListener {

	private Tag keyword;

	public ListKeywordClicker(Tag keyword) {
		this.keyword = keyword;
	}

	public void onClick(View v) {
		Intent intent = new Intent(v.getContext(), tag.class);
		intent.putExtra("keyword", keyword);
		v.getContext().startActivity(intent);
	}

}
