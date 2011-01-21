package nz.gen.wellington.guardian.android.model;

import android.graphics.Color;

public class BlackOnWhiteColourScheme extends ColourScheme {
	
	@Override
	public Integer getBackground() {
		return Color.WHITE;
	}

	@Override
	public Integer getBodytext() {
		return Color.DKGRAY;
	}

	@Override
	public Integer getHeadline() {
		return Color.BLACK;
	}
	
	@Override
	public Integer getAvailableTag() {
		return Color.DKGRAY;
	}

	@Override
	public Integer getUnavailableTag() {
		return Color.WHITE;
	}
	
	@Override
	public Integer getStatus() {
		return Color.LTGRAY;
	}

	
}
