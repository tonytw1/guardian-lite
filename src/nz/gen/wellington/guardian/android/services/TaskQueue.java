package nz.gen.wellington.guardian.android.services;

import java.util.LinkedList;

public class TaskQueue {

	private LinkedList<ContentUpdateTaskRunnable> articleTasks;
	private LinkedList<ContentUpdateTaskRunnable> imageTasks;

	
	public TaskQueue() {
		articleTasks = new LinkedList<ContentUpdateTaskRunnable>();
		imageTasks = new LinkedList<ContentUpdateTaskRunnable>();
	}

	
	public synchronized void addArticleTask(ContentUpdateTaskRunnable articleTask) {
		synchronized (this) {
			articleTasks.addFirst(articleTask);
			this.notify();
		}
	}
	
	public synchronized void addImageTask(ContentUpdateTaskRunnable imageTask) {
		synchronized (this) {
			imageTasks.addFirst(imageTask);
			this.notify();
		}
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

	
}
