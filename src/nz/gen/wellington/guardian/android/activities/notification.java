package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.contentupdate.ContentUpdateService;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class notification extends Activity implements OnClickListener {
	
	private NotificationManager notificationManager;

	Button ok;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.notification);
		
        ok = (Button) findViewById(R.id.Ok);        
        ok.setOnClickListener(this);
        
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