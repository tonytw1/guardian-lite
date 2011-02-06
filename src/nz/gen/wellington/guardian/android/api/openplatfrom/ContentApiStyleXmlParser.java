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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import nz.gen.wellington.guardian.android.activities.ui.ArticleCallback;
import nz.gen.wellington.guardian.android.factories.SingletonFactory;
import nz.gen.wellington.guardian.android.model.ArticleBundle;

import org.xml.sax.SAXException;

import android.content.Context;
import android.util.Log;

public class ContentApiStyleXmlParser {
	
	public static final String ARTICLE_AVAILABLE = "nz.gen.wellington.guardian.android.api.ARTICLE_AVAILABLE";
	
	private static final String TAG = "ContentApiStyleXmlParser";

	private Context context;
	private ContentResultsHandler handler;
	
	public ContentApiStyleXmlParser(Context context) {
		this.context = context;
	}
	
	public ArticleBundle parseArticlesXml(InputStream inputStream, ArticleCallback articleCallback) {
		try {
			handler = SingletonFactory.getContentResultsHandler(context);			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			handler.setArticleCallback(articleCallback);
			saxParser.parse(inputStream, handler);
			inputStream.close();
			return handler.getResult();

		} catch (SAXException e) {
			Log.e(TAG, "SAXException while parsing content xml: " + e.getMessage());			
		} catch (IOException e) {
			Log.e(TAG, "IOException while parsing content xml: " + e.getMessage());
		} catch (ParserConfigurationException e) {
			Log.e(TAG, "ParserConfigurationException while parsing content xml: " + e.getMessage());
		}
		return null;
	}
	
	public void stop() {
		if (handler != null) {
			handler.stop();
		}
	}
	
}
