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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.android.api.filtering.HtmlCleaner;
import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.SectionColourMap;
import nz.gen.wellington.guardian.android.model.Tag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ContentApiStyleJSONParser {
	
	private static final String TAG = "ContentApiStyleJSONParser";	
	private static final String NEW_LINE = "\n";

	private HtmlCleaner htmlCleaner;
	
	public ContentApiStyleJSONParser() {
		htmlCleaner = new HtmlCleaner();
	}
	
	public List<Section> parseSectionsJSON(InputStream input) {
		try {
			StringBuilder content = new StringBuilder();
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			String str;
			while ((str = in.readLine()) != null) {
				content.append(str);
				content.append(NEW_LINE);
			}
			in.close();

			JSONObject json = new JSONObject(content.toString());
			if (!isResponseOk(json)) {
				return null;
			}
			JSONObject response = json.getJSONObject("response");
			JSONArray results = response.getJSONArray("results");

			List<Section> sections = new LinkedList<Section>();
			for (int i = 0; i < results.length(); i++) {
				JSONObject section = results.getJSONObject(i);
				final String sectionName = htmlCleaner.stripHtml(section.getString("webTitle"));
				final String id = section.getString("id");
				sections.add(new Section(id, sectionName, SectionColourMap.getColourForSection(id)));
			}
			return sections;

		} catch (JSONException e) {
			Log.w(TAG, "JSONException while parsing articles: " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "IOException while parsing articles: " + e.getMessage());
		}
		return null;
	}
	
	
	public List<Tag> parseTagsJSON(InputStream input, Map<String, Section> sections) {
		try {
			StringBuilder content = new StringBuilder();
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			String str;
			while ((str = in.readLine()) != null) {
				content.append(str);
				content.append(NEW_LINE);
			}
			in.close();

			JSONObject json = new JSONObject(content.toString());
			if (!isResponseOk(json)) {
				return null;
			}
			JSONObject response = json.getJSONObject("response");
			JSONArray results = response.getJSONArray("results");

			List<Tag> tags = new LinkedList<Tag>();
			for (int i = 0; i < results.length(); i++) {
				JSONObject tag = results.getJSONObject(i);
				final String id = tag.getString("id");
				final String tagName = tag.getString("webTitle");
				final String type = tag.getString("type");

				Section section = null;
				if (type.equals("contributor")) {
					tags.add(new Tag(tagName, id, null));
				} else {
					if (tag.has("sectionId")) {
						final String sectionId = tag.getString("sectionId");
						section = sections.get(sectionId);
						if (section != null) {
							tags.add(new Tag(tagName, id, section));
						}
					}
				}
			}

			return tags;

		} catch (JSONException e) {
			Log.e(TAG, "JSONException while parsing articles: " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "IOException while parsing articles: " + e.getMessage());
		}
		return null;
	}
	
	
	public boolean isResponseOk(JSONObject json) {
		try {
			JSONObject response = json.getJSONObject("response");
			String status = response.getString("status");
			return status != null && status.equals("ok");
		} catch (JSONException e) {
			return false;
		}
	}

}
