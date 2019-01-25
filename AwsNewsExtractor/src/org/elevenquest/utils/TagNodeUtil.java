package org.elevenquest.utils;

import org.htmlcleaner.TagNode;

public class TagNodeUtil {

	public static String makePadding(int numberofpadding) {
		StringBuffer sb = new StringBuffer();
		for( int cnt = 0 ; cnt < numberofpadding; cnt++ )
			sb.append(" ");
		return sb.toString();
	}

	public static void printRecusiveTagNodes(TagNode item, int depth) {
		System.out.println( makePadding(depth) + item.getName());
		TagNode[] children = item.getChildTags();
		for( int cnt = 0 ; cnt < children.length ; cnt++ ) {
			printRecusiveTagNodes(children[cnt], depth + 1);
		}
	}

}
