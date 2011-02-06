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

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.contentupdate.ContentUpdateService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class notification extends AbstractFontResizingActivity implements OnClickListener, FontResizingActivity {
	
	private NotificationManager notificationManager;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      
    	requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.notification);
		
        Button ok = (Button) findViewById(R.id.Ok);
        if (ok != null) {
        	ok.setOnClickListener(this);
        }
        
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		Intent callingIntent = this.getIntent();
		String report = (String) callingIntent.getExtras().getString("report");
		
		TextView heading = (TextView) findViewById(R.id.Report);
		heading.setText(report);
		notificationManager.cancel(ContentUpdateService.UPDATE_COMPLETE_NOTIFICATION_ID);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	public void onClick(View src) {		
		switch (src.getId()) {

		case R.id.Ok:			
			Intent intent = new Intent(this, main.class);
			this.startActivity(intent);
		}		
	}
	
}