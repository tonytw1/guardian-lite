package nz.gen.wellington.guardian.android.activities.ui;

import nz.gen.wellington.guardian.android.activities.tag;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class ArticleSetClicker implements OnClickListener {

	private ArticleSet articleSet;

	public ArticleSetClicker(ArticleSet articleSet) {
		this.articleSet = articleSet;
	}

	public void onClick(View v) {
		Intent intent = new Intent(v.getContext(), tag.class);
		intent.putExtra("articleset", articleSet);
		v.getContext().startActivity(intent);
	}

}
