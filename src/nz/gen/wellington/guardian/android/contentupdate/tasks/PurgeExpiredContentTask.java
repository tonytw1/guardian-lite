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

package nz.gen.wellington.guardian.android.contentupdate.tasks;

import nz.gen.wellington.guardian.android.api.ArticleDAO;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;
import android.content.Context;

public class PurgeExpiredContentTask implements ContentUpdateTaskRunnable {

	private Context context;

	public PurgeExpiredContentTask(Context context) {
		this.context = context;
	}

	
	@Override
	public void run() {
		ArticleDAO articleDAO = SingletonFactory.getArticleDao(context);
		articleDAO.clearExpiredCacheFiles(context);
	}
	
	@Override
	public void stop() {		
	}
	
	@Override
	public String getTaskName() {
		return "Clearing expired article sets";
	}


	@Override
	public void setReport(ContentUpdateReport report) {
	}
	
}
