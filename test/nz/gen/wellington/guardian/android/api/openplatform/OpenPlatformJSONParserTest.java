package nz.gen.wellington.guardian.android.api.openplatform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.TestCase;
import nz.gen.wellington.guardian.android.api.openplatfrom.OpenPlatformJSONParser;
import nz.gen.wellington.guardian.android.model.Section;

import org.json.JSONObject;
import org.junit.Test;

public class OpenPlatformJSONParserTest extends TestCase {

	OpenPlatformJSONParser parser = new OpenPlatformJSONParser(null, null);
	
	@Test
	public void testCanParseSectionContentResults() throws Exception {			
	//	List<Article> articles = parser.parseArticlesXml(loadContent("open-platform/science.json"), new ArrayList<Section>());		
	//	assertEquals(10, articles.size());
		
	//	Article first = articles.get(0);
	//	assertEquals("science/2010/may/24/women-domestic-violence", first.getId());
	//	assertEquals("Why do so many women put up with domestic violence?", first.getTitle());
	//	assertEquals("Carole Jahme shines the cold light of evolutionary psychology on readers' problems. This week: domestic violence", first.getStandfirst());
	//	assertEquals("http://static.guim.co.uk/sys-images/Society/Pix/pictures/2008/10/28/domesticviolencetrail.jpg", first.getThumbnailUrl());
	//	assertEquals(1, first.getAuthors().size());
	}
		

	@Test
	public void testCanParseSectionsJSON() throws Exception {	
		List<Section> sections = parser.parseSectionsJSON(loadContent("open-platform/sections.json"));
		assertEquals(38, sections.size());
	}
	
	@Test
	public void testCanSeeValidStatus() throws Exception {
		StringBuilder content = new StringBuilder();
		final InputStream input = loadContent("open-platform/sections.json");
		BufferedReader in = new BufferedReader(new InputStreamReader(input));
		String str;
		while ((str = in.readLine()) != null) {
			content.append(str);
			content.append("\n");
		}
		in.close();

		JSONObject json = new JSONObject(content.toString());
		assertTrue(parser.isResponseOk(json));
	}
	
	private InputStream loadContent(String filename) throws IOException {
		File contentFile = new File(ClassLoader.getSystemClassLoader().getResource(filename).getFile());
		return new FileInputStream(contentFile);
	}
	
}
