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

package nz.gen.wellington.guardian.android.api.caching;

import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.guardian.android.model.Section;

public class InMemorySectionCache {
	
	private List<Section> sections;
	
	public InMemorySectionCache() {
		this.sections = new ArrayList<Section>();
	}

	public void clear() {
		sections.clear();
	}
		
	public synchronized void addAll(List<Section> sections) {
		this.sections.clear();
		this.sections.addAll(sections);
	}

	public synchronized List<Section> getAll() {
		return new ArrayList<Section>(sections);	// TODO return imputable copy
	}

	public synchronized boolean isEmpty() {
		return sections.isEmpty();
	}

}
