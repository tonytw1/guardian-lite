package nz.gen.wellington.guardian.android.activities.ui;

import nz.gen.wellington.guardian.android.activities.keyword;
import nz.gen.wellington.guardian.android.model.Keyword;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class ListKeywordClicker implements OnClickListener {

	private Keyword keyword;

	public ListKeywordClicker(Keyword keyword) {
		this.keyword = keyword;
	}

	public void onClick(View v) {
		Intent intent = new Intent(v.getContext(), keyword.class);
		intent.putExtra("keyword", keyword);
		v.getContext().startActivity(intent);
	}

}
