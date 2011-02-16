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

import nz.gen.wellington.guardian.model.Section;
import nz.gen.wellington.guardian.model.Tag;

public class TagCombinerArticleSet extends AbstractArticleSet implements ArticleSet {

	private static final long serialVersionUID = 1L;
	private Tag leftTag;
	private Tag rightTag;
		
	public TagCombinerArticleSet(Tag leftTag, Tag rightTag, int pageSize) {
		super(pageSize);
		this.leftTag = leftTag;
		this.rightTag = rightTag;
	}

	@Override
	public String getName() {
		return leftTag.getName() + " + " + rightTag.getName();
	}
		
	@Override
	public String getShortName() {
		return rightTag.getName();
	}

	@Override
	public Section getSection() {
		return leftTag.getSection();
	}
	
	public Tag getLeftTag() {
		return leftTag;
	}

	public Tag getRightTag() {
		return rightTag;
	}
	
}
