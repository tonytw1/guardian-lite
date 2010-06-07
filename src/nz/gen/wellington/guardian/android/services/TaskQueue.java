package nz.gen.wellington.guardian.android.services;

import java.util.LinkedList;

public class TaskQueue {

	private LinkedList<ContentUpdateTaskRunnable> tasks;

	public TaskQueue() {
		tasks = new LinkedList<ContentUpdateTaskRunnable>();
	}

	public void addTask(ContentUpdateTaskRunnable task) {
		synchronized (this) {
			tasks.addFirst(task);
			this.notify();
		}
	}

	public int getSize() {
		return tasks.size();
	}

	public boolean isEmpty() {
		return tasks.isEmpty();
	}

	public ContentUpdateTaskRunnable removeLast() {
		return tasks.removeLast();
	}

}
