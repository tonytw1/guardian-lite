package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.Picture;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class picture extends Activity {

	private ImageDAO imageDAO;
	private Picture picture;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.picture);
		
		imageDAO = SingletonFactory.getImageDao(this.getApplicationContext());
			
		this.picture = (Picture) this.getIntent().getExtras().get("picture");		
		if (picture != null) {
			ImageView image = (ImageView) findViewById(R.id.ArticleImage);
			image.setVisibility(View.VISIBLE);
			image.setScaleType(ScaleType.FIT_START);
			image.setImageBitmap(imageDAO.getImage(picture.getFile()));
			populateCaption(picture.getCaption());

		} else {
        	Toast.makeText(this, "Could not load picture", Toast.LENGTH_SHORT).show();
		}		
	}
	
	// TODO duplication
	private void populateCaption(String caption) {
		if (caption != null && !caption.trim().equals("")) {
			TextView captionView = (TextView) findViewById(R.id.Caption);
			captionView.setVisibility(View.VISIBLE);
			captionView.setText(caption);
		}
	}
}
