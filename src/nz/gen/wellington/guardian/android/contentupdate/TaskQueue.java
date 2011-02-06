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
