package nz.gen.wellington.guardian.android.activities.ui;

import nz.gen.wellington.guardian.android.model.ArticleSet;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class ArticleSetClicker implements OnClickListener {

	private ArticleSet articleSet;
	private Class<? extends Activity> target;

	public ArticleSetClicker(ArticleSet articleSet, Class<? extends Activity > target) {
		this.articleSet = articleSet;
		this.target = target;
	}

	public void onClick(View v) {
		Intent intent = new Intent(v.getContext(), target);
		intent.putExtra("articleset", articleSet);
		v.getContext().startActivity(intent);
	}

}
