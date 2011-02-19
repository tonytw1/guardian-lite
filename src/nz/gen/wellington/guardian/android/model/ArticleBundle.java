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
import java.util.Map;

import nz.gen.wellington.guardian.model.Article;
import nz.gen.wellington.guardian.model.Refinement;

public class ArticleBundle implements Serializable {
	
	private static final long serialVersionUID = 5L;
	
	private List<Article> articles;
	private Map<String, List<Refinement>> refinements;
	private String checksum;
	private String description;
	
	public ArticleBundle(List<Article> articles, Map<String, List<Refinement>> refinements, String checksum, String description) {
		this.articles = articles;
		this.refinements = refinements;
		this.checksum = checksum;
		this.description = description;
	}

	public List<Article> getArticles() {
		return articles;
	}

	public Map<String, List<Refinement>> getRefinements() {
		return refinements;
	}

	public String getChecksum() {
		return checksum;
	}
	
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
	
	public String getDescription() {
		return description;
	}
	
}
