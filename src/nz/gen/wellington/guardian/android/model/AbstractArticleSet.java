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

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import nz.gen.wellington.guardian.model.Section;

public abstract class AbstractArticleSet implements ArticleSet, Serializable {
	
	private static final long serialVersionUID = 3L;
	private int pageSize;
	private String sourceUrl;
	
	protected String fromDate;
	protected String toDate;
	protected String dateDisplayName;
	
	private String[] permittedRefinements = {};

	public AbstractArticleSet(int pageSize) {
		this.pageSize = pageSize;
	}
	
	@Override
	public String getShortName() {
		return getName();
	}

	@Override
	public Section getSection() {
		return null;
	}

	@Override
	public int getPageSize() {
		return pageSize;
	}

	@Override
	public String getSourceUrl() {
		return sourceUrl;
	}

	@Override
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isFeatureTrailAllowed() {
		return true;
	}

	@Override
	public List<String> getPermittedRefinements() {
		return Arrays.asList(permittedRefinements);
	}
	
	public String getFromDate() {
		return fromDate;
	}
	
	public String getToDate() {
		return toDate;
	}

	protected boolean isDateRefinedArticleSet() {
		return toDate != null;
	}
	
}
