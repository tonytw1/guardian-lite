package nz.gen.wellington.guardian.android.activities.ui;

import nz.gen.wellington.guardian.android.activities.author;
import nz.gen.wellington.guardian.android.model.Author;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class ListAuthorClicker implements OnClickListener {

	private Author author;

	public ListAuthorClicker(Author author) {
		this.author = author;
	}

	public void onClick(View v) {
		Intent intent = new Intent(v.getContext(), author.class);
		intent.putExtra("author", author);
		v.getContext().startActivity(intent);
	}

}
