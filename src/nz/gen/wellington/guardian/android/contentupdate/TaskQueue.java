package nz.gen.wellington.guardian.android.contentupdate;

import java.util.LinkedList;

import nz.gen.wellington.guardian.android.contentupdate.tasks.ContentUpdateTaskRunnable;

import android.content.Context;
import android.content.Intent;

public class TaskQueue {

    public static final String QUEUE_CHANGED = "nz.gen.wellington.guardian.android.event.TASK_QUEUE_CHANGED";

	private LinkedList<ContentUpdateTaskRunnable> articleTasks;
	private LinkedList<ContentUpdateTaskRunnable> imageTasks;

	private Context context;

	
	public TaskQueue(Context context) {
		this.context = context;
		articleTasks = new LinkedList<ContentUpdateTaskRunnable>();
		imageTasks = new LinkedList<ContentUpdateTaskRunnable>();
	}

	
	public synchronized void addArticleTask(ContentUpdateTaskRunnable articleTask) {
		synchronized (this) {
			articleTasks.add(articleTask);
			this.notify();
			announceTaskQueueChange();
		}
	}
	
	public synchronized void addImageTask(ContentUpdateTaskRunnable imageTask) {
		synchronized (this) {
			imageTasks.addFirst(imageTask);
			this.notify();
			announceTaskQueueChange();

		}
	}


	public synchronized void clear() {
		articleTasks.clear();
		imageTasks.clear();
		announceTaskQueueChange();
	}

	
	public synchronized int getArticleSize() {
		return articleTasks.size();
	}

	public synchronized int getImageSize() {
		return imageTasks.size();
	}
	
		
	public synchronized ContentUpdateTaskRunnable getNext() {
		if (!this.isArticleEmpty()) {
			return this.getNextArticleTask();
		} else if (!this.isImageEmpty()) {
			return this.getNextImageTask();
		} else {
			return null;
		}
	}
	
	
	public synchronized void remove(ContentUpdateTaskRunnable task) {
		if (articleTasks.contains(task)) {
			articleTasks.remove(task);
		} else if (imageTasks.contains(task)) {
			imageTasks.remove(task);
		}
		this.notify();
		announceTaskQueueChange();
	}
	
	
	private boolean isArticleEmpty() {
		return articleTasks.isEmpty();
	}

	private ContentUpdateTaskRunnable getNextArticleTask() {
		return articleTasks.getFirst();
	}
	
	private ContentUpdateTaskRunnable getNextImageTask() {
		return imageTasks.getFirst();
	}

	private boolean isImageEmpty() {
		return imageTasks.isEmpty();
	}
	
	public boolean isEmpty() {
		return isArticleEmpty() && isImageEmpty();
	}
	
	
	private void announceTaskQueueChange() {
		Intent intent = new Intent(QUEUE_CHANGED);
		intent.putExtra("article_queue_size", this.getArticleSize());
		intent.putExtra("image_queue_size", this.getImageSize());
		context.sendBroadcast(intent);
	}
	
	
}
