package nz.gen.wellington.guardian.android.services;

import nz.gen.wellington.guardian.android.model.ContentUpdateReport;

public interface ContentUpdateTaskRunnable extends Runnable {

	public ContentUpdateReport getReport();
	
}
