package nz.gen.wellington.guardian.android.activities;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.api.ArticleDAOFactory;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class startup extends Activity {

	private static final String TAG = "startup";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "Starting up");
		Intent intent = new Intent(this, main.class);
		this.finish();
		
		Context context = this.getApplicationContext();
		ArticleDAO articleDAO = ArticleDAOFactory.getDao(context);
		articleDAO.clearExpiredCacheFiles(context);
				
		this.startActivity(intent);
		Log.i(TAG, "Finished startup");
	}

}
