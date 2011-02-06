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

public class Tag implements Serializable {
	
	private static final long serialVersionUID = 3L;
	private String name;
	private String id;
	private Section section;

	public Tag(String name, String id, Section section) {
		this.name = name;
		this.id = id;
		this.section = section;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}

	public boolean isSectionKeyword() {
		if (section != null) {
			final String sectionTagId = section.getId() + "/" + section.getId();
			return id.equals(sectionTagId);			
		}
		return false;
	}

	public boolean isContributorTag() {
		return id != null && id.startsWith("profile/");
	}

	public boolean isContentTypeTag() {
		return id != null && id.startsWith("type/");
	}

	public boolean isGalleryTag() {
		return id != null && id.equals("type/gallery");
	}
	
}
