package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class about extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.about_dialog);
		
		ImageView heading = (ImageView) findViewById(R.id.KingsPlace);
		populateImage(heading);
		
		TextView description = (TextView) findViewById(R.id.Description);		
		description.setText("This unofficial application was developed by Tony McCrae of Eel Pie Consulting Limited.\n\n" +
				"Articles are retreived from the Guardian's RSS feeds. Tag information is supplied by the Guardian Content API.\n\n" +
				"For more information see:\nhttp://eelpieconsulting.co.uk/guardianlite\n\n" +
				"Application © 2010 Eel Pie Consulting Limited\n\n" +
				"Content © 2010 Guardian News and Media Limited");
		
		ImageView image = (ImageView) findViewById(R.id.GuardianLogo);
		image.setImageResource(R.drawable.poweredbyguardian);
		
	}

	private void populateImage(ImageView heading) {
		if (isDaylightInLondon()) {
			heading.setImageResource(R.drawable.kingsplace);
		} else {
			heading.setImageResource(R.drawable.kingsplace_night);

		}
	}

	private boolean isDaylightInLondon() {
		// TODO implement as an easter egg
		return false;
	}
	
}
