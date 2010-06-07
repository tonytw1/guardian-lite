package nz.gen.wellington.guardian.android.activities;

import java.util.List;

import nz.gen.wellington.guardian.android.R;
import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.services.ContentUpdateService;
import nz.gen.wellington.guardian.android.services.TaskQueue;
import nz.gen.wellington.guardian.android.services.UpdateSectionArticlesTask;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class sync extends Activity implements OnClickListener {
	
	private static final String TAG = "reload";

	Button start;
	Button refresh;

	private NotificationManager notificationManager;
		
	
	public sync() {
	}
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reload);
        
        start = (Button) findViewById(R.id.buttonStart);        
        start.setOnClickListener(this);
        
        refresh = (Button) findViewById(R.id.Refresh);        
        refresh.setOnClickListener(this);
        
    	notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	notificationManager.cancel(ContentUpdateService.UPDATE_COMPLETE_NOTIFICATION_ID);	
    	updateStatus();
   	}

	
	public void onClick(View src) {		
		switch (src.getId()) {
		case R.id.buttonStart:
			Log.d(TAG, "Starting content update service service");
			TaskQueue taskQueue = ArticleDAOFactory.getTaskQueue();
			
			ArticleDAO articleDAO = ArticleDAOFactory.getDao(this);
			articleDAO.evictSections();
			articleDAO.evictAll();
			
			List<Section> sections = articleDAO.getSections();
			if (sections != null) {
				for (Section section : sections) {
					Log.i(TAG, "Injecting section into update queue: " + section.getName());
					taskQueue.addArticleTask(new UpdateSectionArticlesTask(articleDAO, section, this));
				}
			}
			
			startService(new Intent(this, ContentUpdateService.class));			
			break;
		}
		updateStatus();
	}
	
	
	public void updateStatus() {
		TaskQueue taskQueue = ArticleDAOFactory.getTaskQueue();		
		
		String statusMessage = Integer.toString(taskQueue.getArticleSize()) + " article sets and " +
			Integer.toString(taskQueue.getImageSize()) + " images to load.";
				
		TextView status = (TextView) findViewById(R.id.Status);		
		status.setText(statusMessage);
		
		boolean canRun = taskQueue.isEmpty();
		start.setEnabled(canRun);
	}
	
}