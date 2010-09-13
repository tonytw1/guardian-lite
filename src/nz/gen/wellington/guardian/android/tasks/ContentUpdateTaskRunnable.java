package nz.gen.wellington.guardian.android.tasks;

import nz.gen.wellington.guardian.android.model.ContentUpdateReport;

public interface ContentUpdateTaskRunnable extends Runnable {

	public void setReport(ContentUpdateReport report);
	public String getTaskName();
	public void stop();
	
}
