package nz.gen.wellington.guardian.android.model;

import android.graphics.Color;

public abstract class ColourScheme {

	public abstract Integer getBackground();
	public abstract Integer getBodytext();
	public abstract Integer getHeadline();
	
	public abstract Integer getAvailableTag();
	public abstract Integer getUnavailableTag();
	public abstract Integer getStatus();

	public int getAvailableTagOnSeperator() {
		return Color.WHITE;
	}
	public int getUnavailableTagOnSeperator() {
		return Color.DKGRAY;
	}
	
}
