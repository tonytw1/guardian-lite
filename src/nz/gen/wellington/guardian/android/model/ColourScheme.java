package nz.gen.wellington.guardian.android.model;

import android.graphics.Color;

public abstract class ColourScheme {

	public abstract Integer getBackground();
	public abstract Integer getBodytext();
	public abstract Integer getHeadline();
		
	public final int AVAILABLE_TAG = Color.WHITE;
	public final int UNAVAILABLE_TAG = Color.DKGRAY;
	public final int STATUS = Color.LTGRAY;
	
}
