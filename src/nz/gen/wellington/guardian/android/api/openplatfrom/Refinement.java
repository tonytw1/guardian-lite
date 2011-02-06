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

package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.io.Serializable;

import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;

public class Refinement implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String displayName;
	private Tag tag;
	private String fromDate;
	private String toDate;

	public Refinement(Tag tag) {
		this.tag = tag;
	}

	public Refinement(Section section) {
		// TODO Auto-generated constructor stub
	}

	public Refinement(String displayName, String fromDate, String toDate) {
		this.displayName = displayName;
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public String getFromDate() {
		return fromDate;
	}

	public String getToDate() {
		return toDate;
	}
	
}
