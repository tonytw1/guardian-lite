package nz.gen.wellington.guardian.android.activities.ui;

import nz.gen.wellington.guardian.android.activities.article;
import nz.gen.wellington.guardian.android.model.Article;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class ArticleClicker implements OnClickListener {

	private Article newsitem;

	public ArticleClicker(Article newsitem) {
		this.newsitem = newsitem;
	}

	public void onClick(View v) {
		Intent intent = new Intent(v.getContext(), article.class);
		intent.putExtra("article", newsitem);
		v.getContext().startActivity(intent);		
	}

}
