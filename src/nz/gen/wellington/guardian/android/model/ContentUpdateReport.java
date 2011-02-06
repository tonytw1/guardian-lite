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

package nz.gen.wellington.guardian.android.model;

import java.util.Date;

import nz.gen.wellington.guardian.android.utils.DateTimeHelper;


public class ContentUpdateReport {
	
	private int sectionCount;
	private int articleCount;
	private int imageCount;
	private Date startTime;
	
	public ContentUpdateReport() {
		sectionCount = 0;
		articleCount = 0;
		imageCount = 0;
		startTime = DateTimeHelper.now();
	}


	public int getSectionCount() {
		return sectionCount;
	}


	public void setSectionCount(int sectionCount) {
		this.sectionCount = sectionCount;
	}


	public int getArticleCount() {
		return articleCount;
	}


	public void setArticleCount(int articleCount) {
		this.articleCount = articleCount;
	}


	public int getImageCount() {
		return imageCount;
	}


	public void setImageCount(int imageCount) {
		this.imageCount = imageCount;
	}


	public Date getStartTime() {
		return startTime;
	}
	
}
