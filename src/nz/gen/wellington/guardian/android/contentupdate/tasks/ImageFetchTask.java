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

import nz.gen.wellington.guardian.android.api.ImageDAO;
import nz.gen.wellington.guardian.android.model.ContentUpdateReport;

public class ImageFetchTask implements ContentUpdateTaskRunnable {

	private String url;
	private ContentUpdateReport report;
	private String description;	
	private ImageDAO imageDao;
	
	public ImageFetchTask(String thumbnailUrl, String description, ImageDAO imageDAO) {
		this.url = thumbnailUrl;
		this.description = description;
		this.imageDao = imageDAO;
	}
	
	@Override
	public void run() {
		if (!imageDao.isAvailableLocally(url)) {
			imageDao.getImage(url);
			report.setImageCount(report.getImageCount()+1);
		}
	}
	
	@Override
	public void stop() {
		imageDao.stopLoading();
	}
	
	@Override
	public String getTaskName() {
		if (description != null) {
			return "Fetching image: " + description;
		}
		return "Fetching image: " + url;
	}
	
	@Override
	public void setReport(ContentUpdateReport report) {
		this.report = report;		
	}
	
}
