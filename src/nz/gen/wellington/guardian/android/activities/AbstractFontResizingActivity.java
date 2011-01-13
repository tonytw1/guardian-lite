package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.model.ColourScheme;
import android.app.Activity;
import android.view.View;

public abstract class AbstractFontResizingActivity extends Activity implements FontResizingActivity {

	@Override
	public void setFontSize(int baseSize) {
		setBackgroundColour();				
	}

	private void setBackgroundColour() {
		View view =  findViewById(R.id.Main);
		if (view != null && ColourScheme.BACKGROUND != null) {
			view.setBackgroundColor(ColourScheme.BACKGROUND);
		}
	}

}
