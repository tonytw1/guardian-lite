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
import java.util.List;

public class SavedArticlesArticleSet extends AbstractArticleSet implements Serializable, ArticleSet {

	private static final long serialVersionUID = 1L;
	
	private List<String> articlesIds;
		
	public SavedArticlesArticleSet(List<String> articleIds) {
		super(0);
		this.articlesIds = articleIds;
	}
	
	@Override
	public String getName() {
		return "Saved items";
	}
	
	@Override
	public boolean isEmpty() {
		return articlesIds.isEmpty();
	}

	public List<String> getArticlesIds() {
		return articlesIds;
	}
	
}
