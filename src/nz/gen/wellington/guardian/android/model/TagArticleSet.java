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

import java.util.Arrays;
import java.util.List;

import nz.gen.wellington.guardian.model.Section;
import nz.gen.wellington.guardian.model.Tag;

public class TagArticleSet extends AbstractArticleSet implements ArticleSet {
	
	private static final long serialVersionUID = 2L;
	private Tag tag;
	
	private String[] permittedRefinements = {"keyword", "contributor", "blog", "date"};

	public TagArticleSet(Tag tag, int pageSize) {
		super(pageSize);
		this.tag = tag;
	}
	
	public TagArticleSet(Tag tag, int pageSize, String dateDisplayName, String fromDate, String toDate) {
		super(pageSize);
		this.tag = tag;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.dateDisplayName = dateDisplayName;
	}
	
	@Override
	public String getName() {
		StringBuilder name = new StringBuilder();
		if (!tag.isSectionKeyword() && tag.getSection() != null) {
			name.append(tag.getSection().getTag().getName() + " - ");
		}
		name.append(tag.getName());
		if (fromDate != null) {
			name.append(" (" + dateDisplayName + ")");
		}
		return name.toString();
	}
	
	@Override
	public String getShortName() {
		if (fromDate != null) {
			return dateDisplayName;
		}
		return tag.getName();
	}

	@Override
	public Section getSection() {
		return tag.getSection();
	}
	
	@Override
	public List<String> getPermittedRefinements() {
		if (isDateRefinedArticleSet()) {
			return Arrays.asList("date");
		}
		return Arrays.asList(permittedRefinements);
	}
	
	public Tag getTag() {
		return tag;
	}
	
	@Override
	public boolean isFeatureTrailAllowed() {
		return !tag.isContributorTag();
	}
	
}
