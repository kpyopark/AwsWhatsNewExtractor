package org.elevenquest.news;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.PrintOrientation;
import org.elevenquest.utils.StringUtil;
import org.elevenquest.utils.TagNodeUtil;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import java.io.*;


public class AwsNewsExtractor {
	
	static String BASE_URL = "https://aws.amazon.com/ko/about-aws/whats-new/";
	static String ITEM_IN_LIST_XPATH = "//*[@id=\"aws-page-content\"]/div/div/main/section/div[2]/div/div/ul/li";
	String target_url = null;
	Pattern ahref_pattern = null; 
	
	/**
	 * @param year
	 */
	public AwsNewsExtractor(String year) {
		target_url = BASE_URL + year;
		ahref_pattern = Pattern.compile("<a\\s+(?:[^>]*?\\s+)?href=([\"'])(.*?)\\1");
	}
	
	public List<WhatsNewItem> extract() {
		ArrayList<WhatsNewItem> list = new ArrayList<WhatsNewItem>();
		HttpURLConnection conn = null;
		HtmlCleaner cleaner = new HtmlCleaner();
		
		try {
			URL url = new URL(target_url);
			conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			TagNode rootNode = cleaner.clean(conn.getInputStream());
			System.out.println(rootNode);
			Object[] items = rootNode.evaluateXPath(ITEM_IN_LIST_XPATH);
			for(Object itemNode : items) list.add(convertFromHtmlTag((TagNode)itemNode));
		} catch (MalformedURLException e) {
			e.printStackTrace();		// From new URL()	
		} catch (IOException ioe) {
			ioe.printStackTrace();		// From url.openConnection();
		} catch (XPatherException xpe) {
			xpe.printStackTrace();		// From rootNode.evaluateXPath();
		}
		return list;
	}
	
	private WhatsNewItem convertFromHtmlTag(TagNode itemNode) {
		WhatsNewItem item = new WhatsNewItem();
		item.header = itemNode.getChildTags()[0].getChildTags()[0].getText().toString();
		item.anouncelink = "https://" + itemNode.getChildTags()[0].getChildTags()[0].getAttributeByName("href");
		item.date = StringUtil.removeHtmlSpaceTag(itemNode.getChildTags()[1].getText().toString()).trim();
		item.description = StringUtil.removeHtmlSpaceTag(itemNode.getChildTags()[2].getText().toString()).trim();
		item.relatedLinks = extractLinks(itemNode.getChildTags()[2].getText().toString());
		System.out.println(item.anouncelink);
		return item;
	}
	
	private List<String> extractLinks(String text) {
		List<String> links = new ArrayList<String>();
		Matcher matcher = ahref_pattern.matcher(text);
		while(matcher.find()) links.add(matcher.group(2));
		return links;
	}

	private static final String QUOTE = "\"";
	private static final Pattern NON_WORD = Pattern.compile(".*\\W.*");

	private final static String quotedString(String unquoted) {
		final StringBuilder sb = new StringBuilder();
		final Matcher m = NON_WORD.matcher(unquoted);
		if (m.matches()) {
			if (unquoted.contains(QUOTE)) {
				unquoted = unquoted.replaceAll(QUOTE, "\"\"");
			}
			return sb.append(QUOTE).append(unquoted).append(QUOTE).toString();
		} else {
			return unquoted;
		}
	}

	private final static void printToCsv(String fileName, List<WhatsNewItem> items) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileOutputStream(fileName));
			for(WhatsNewItem item : items) {
				StringBuffer csv = new StringBuffer();
				csv.append(
						quotedString(item.date))
					.append(",").append(quotedString(item.header))
					.append(",").append(quotedString(item.description))
					.append(",").append(quotedString(item.anouncelink))
					.append(",").append(quotedString(String.join(" ",item.relatedLinks)));
				pw.println(csv);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (pw != null)
				try {
					pw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			;
		}
	}
	
	public static void main(String[] args) {
		String filename;
		String year;
		if(args.length<2) {
			year = args.length == 1 ? args[0] : "2020";
			filename = "./sample.csv";
		} else {
			year = args[0];
			filename = args[1];
		}
		AwsNewsExtractor extractor = new AwsNewsExtractor(year);
		printToCsv(filename, extractor.extract());
	}
}
