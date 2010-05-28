package nz.gen.wellington.guardian.android.services;

import java.util.LinkedList;

public class TaskQueue {

	private LinkedList<Runnable> tasks;

	public TaskQueue() {
		tasks = new LinkedList<Runnable>();
	}

	public void addTask(Runnable task) {
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

	public Runnable removeLast() {
		return tasks.removeLast();
	}

}
