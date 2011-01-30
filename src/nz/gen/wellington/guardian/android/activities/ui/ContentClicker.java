package nz.gen.wellington.guardian.android.activities.ui;

import nz.gen.wellington.guardian.android.activities.article;
import nz.gen.wellington.guardian.android.activities.gallery;
import nz.gen.wellington.guardian.android.model.Article;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class ContentClicker implements OnClickListener {

	private Article content;

	public ContentClicker(Article content) {
		this.content = content;
	}

	public void onClick(View view) {
		Intent intent = getIntentForContentsType(view.getContext(), content);
		intent.putExtra("article", content);
		view.getContext().startActivity(intent);		
	}

	private Intent getIntentForContentsType(Context context, Article article) {
		if (article.isGallery()) {
			return new Intent(context, gallery.class);
		}
		return new Intent(context, article.class);
	}

}
