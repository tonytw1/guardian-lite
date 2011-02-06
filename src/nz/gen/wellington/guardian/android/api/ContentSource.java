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

package nz.gen.wellington.guardian.android.api;

import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.activities.ui.ArticleCallback;
import nz.gen.wellington.guardian.android.model.ArticleBundle;
import nz.gen.wellington.guardian.android.model.ArticleSet;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;

/*
 * This interface is here to remind you that you can source content from the Content API,
 * the RSS feeds or elsewhere. You could add implementations for entirely different publications.
 */

public interface ContentSource {

	public List<Section> getSections();
	ArticleBundle getArticles(ArticleSet articleSet, List<Section> sections, ArticleCallback articleCallback);
	public String getRemoteChecksum(ArticleSet articleSet, int pageSize);
	public void stopLoading();
	public List<Tag> searchTags(String searchTerm, List<String> allowedTagSearchTypes, Map<String, Section> sections);
	
}
