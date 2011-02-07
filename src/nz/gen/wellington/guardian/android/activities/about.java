/*	Guardian Lite - an Android reader for the Guardian newspaper.
 *	Copyright (C) 2011  Eel Pie Consulting Limited
 *
 *	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.	*/

package nz.gen.wellington.guardian.android.activities;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.factories.ArticleSetFactory;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class about extends ArticleListActivity implements FontResizingActivity {
		
	private ArticleSetFactory articleSetFactory;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		articleSetFactory = SingletonFactory.getArticleSetFactory(this.getApplicationContext());
		
		setContentView(R.layout.about);
		
		TextView description = (TextView) findViewById(R.id.About);
		
		setHeading("Guardian Lite - About");
		
		description.setText("This unofficial application was developed by Tony McCrae of Eel Pie Consulting Limited.\n\n" +
				"Articles are retreived from the Guardian's RSS feeds. Tag information is supplied by the Guardian Content API.\n\n" +
				"For more information see:\nhttp://github.com/tonytw1/guardian-lite\n\n" +
				"Application \u00A9 2010 Eel Pie Consulting Limited\n"
				);
				
		ImageView poweredByTheGuardian = (ImageView) findViewById(R.id.PoweredByTheGuardian);
		poweredByTheGuardian.setImageResource(R.drawable.poweredbyguardian);
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		populateSplashImage();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setFontSize();
	}
	
	@Override
	protected ArticleSet getArticleSet() {
		return articleSetFactory.getAboutArticleSet();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MenuedActivity.HOME, 0, "Home");
		MenuItem refreshOption = menu.add(0, MenuedActivity.REFRESH, 0, "Refresh");		
		enableMenuItemIfConnectionIsAvailable(refreshOption);
	    return true;
	}
	
	private void populateSplashImage() {
		ImageView slashImage = (ImageView) findViewById(R.id.SplashImage);
		if (isDaylightInLondon()) {
			slashImage.setImageResource(R.drawable.kingsplace);
		} else {
			slashImage.setImageResource(R.drawable.kingsplace_night);
		}
	}
	
	private boolean isDaylightInLondon() {
		Calendar londonCal = GregorianCalendar.getInstance(TimeZone.getTimeZone("Europe/London"));
		int londonHour = londonCal.get(Calendar.HOUR_OF_DAY);
		return londonHour > 6 && londonHour < 21;
	}
	
	public void setFontSize() {
		super.setFontSize();
		
		TextView about = (TextView) findViewById(R.id.About);
		TextView contentCredit = (TextView) findViewById(R.id.ContentCredit);
		
		about.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseFontSize);
		about.setTextColor(colourScheme.getBodytext());
		contentCredit.setTextSize(TypedValue.COMPLEX_UNIT_PT, baseFontSize);
		contentCredit.setTextColor(colourScheme.getBodytext());
	}
	
}
