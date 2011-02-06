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

public class SectionArticleSet extends AbstractArticleSet implements ArticleSet, Serializable {
	
	private static final long serialVersionUID = 1L;

	protected String[] permittedRefinements = {"blog", "keyword", "contributor", "date", "type"};
	private Section section;

	public SectionArticleSet(Section section, int pageSize) {
		super(pageSize);
		this.section = section;
	}

	public SectionArticleSet(Section section, int pageSize, String dateDisplayName, String fromDate, String toDate) {
		super(pageSize);
		this.section = section;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.dateDisplayName = dateDisplayName;
	}

	@Override
	public String getName() {
		String name = section.getName();
		if (fromDate != null) {
			name = name + " (" + dateDisplayName + ")";
		}
		return name;
	}
	
	@Override
	public String getShortName() {
		if (fromDate != null) {
			return dateDisplayName;
		}
		return section.getName();
	}
	
	@Override
	public List<String> getPermittedRefinements() {
		if (isDateRefinedArticleSet()) {
			return Arrays.asList("date");
		}
		return Arrays.asList(permittedRefinements);
	}
	
	public Section getSection() {
		return section;
	}
	
}