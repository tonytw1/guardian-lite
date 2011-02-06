package nz.gen.wellington.guardian.android.activities.ui;

import nz.gen.wellington.guardian.android.activities.picture;
import nz.gen.wellington.guardian.android.model.Picture;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class PictureClicker implements OnClickListener {

	private Picture picture;

	public PictureClicker(Picture picture) {
		this.picture = picture;
	}
	
	public void onClick(View view) {
		Intent intent = new Intent(view.getContext(), picture.class);
		intent.putExtra("picture", picture);
		view.getContext().startActivity(intent);	
	}

}
