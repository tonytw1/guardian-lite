package nz.gen.wellington.guardian.android.api.openplatfrom;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import nz.gen.wellington.guardian.android.model.Section;
import nz.gen.wellington.guardian.android.model.Tag;

public class ContentApiStyleUrlBuilder {
	
	private static final String SEARCH_QUERY = "search";
	private static final String SECTIONS_QUERY = "sections";
	private static final String TAGS = "tags";
	private static final String OR = "|";
	
	private String apiKey;
	private String format = "xml";

	private List<Section> sections;
	private List<Tag> tags;
	private boolean showAll;
	private boolean showRefinements;
	private Integer pageSize;
	private String searchTerm;
	
	public ContentApiStyleUrlBuilder(String apiKey) {
		this.apiKey = apiKey;
		this.sections = new ArrayList<Section>();
		this.tags = new ArrayList<Tag>();
		this.showAll = false;
		this.showRefinements = false;
	}
	
	public String toSearchQueryUrl() {
		StringBuilder url = new StringBuilder("/" + SEARCH_QUERY);
		appendCoreParameters(url);
		
		StringBuilder tagsParameter = new StringBuilder();			
		if (!tags.isEmpty()) {
			for (Tag tag : tags) {
				tagsParameter.append(tag.getId());
				tagsParameter.append(OR);		
			}
		}
				
		if (!sections.isEmpty()) {
			for (Section section : sections) {
				tagsParameter.append(section.getId() + "/" + section.getId());
				tagsParameter.append(OR);
			}			
		}
		
		if (tagsParameter.length() > 0) {
			String tags = tagsParameter.substring(0, tagsParameter.length()-1);
			try {
				tags = URLEncoder.encode(tags, "UTF8");
			} catch (UnsupportedEncodingException e) {
			}
			url.append("&tag=");
			url.append(tags);				
		}
		
		// TODO about article sets shouldn't go through the content api classes
		//if (articleSet instanceof AboutArticleSet) {
		//	 url = new StringBuilder(apiPrefix + "/about");
		//}
		
		return url.toString();
	}
	
	
	public String toTagSearchQueryUrl() {		
		StringBuilder url = new StringBuilder("/" + TAGS);	// TODO this call should be proxied by guardian-lite
		appendCoreParameters(url);
		url.append("&type=keyword%2Ccontributor%2Cblog");	// TODO push to allowed types constant somewhere
		url.append("&q=" + URLEncoder.encode(searchTerm));		
		return url.toString();
	}
	

	public String toSectionsQueryUrl() {
		StringBuilder url = new StringBuilder("/" + SECTIONS_QUERY);
		appendCoreParameters(url);
		return url.toString();
	}

	
	public void addSection(Section section) {
		sections.add(section);
	}

	public void addTag(Tag tag) {
		tags.add(tag);
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}
	
	public void setShowRefinements(boolean showRefinements) {
		this.showRefinements = showRefinements;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	private void appendCoreParameters(StringBuilder url) {
		url.append("?format=" + format);
		if (pageSize != null) {
			url.append("&page-size=" + pageSize);		
		}
		if (showAll) {
			url.append("&show-fields=all");
			url.append("&show-tags=all");
		}
		
		if (apiKey != null && !apiKey.trim().equals("")) {
			url.append("&api-key=" + apiKey);
		}
		
		if (showRefinements) {
			url.append("&show-refinements=all");
		}
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	
}
