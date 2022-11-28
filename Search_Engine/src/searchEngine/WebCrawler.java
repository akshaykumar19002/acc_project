package src.searchEngine;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {
	
	private Set<String> listOfLinks = new HashSet<>();
	private final int MAXDEPTH = 1;
	private String regex = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
	
	/**
	 * Crawl, parse and store the web pages using Jsoup library.
	 * @param url - webpage url to crawl
	 * @param depth - level at which link is found during crawling
	 */
	public void start(String url, int depth) {
		Pattern pattern = Pattern.compile(regex);
		if (depth <= MAXDEPTH) {
			try {
				Document document = Jsoup.connect(url).get();
				
				HTMLParser.saveHTMLDoc(document, url);
				depth++;
				
				if (depth < MAXDEPTH) {
					Elements links = document.select("a[href]");
					for (Element page: links) {
						if (verifyUrl(page.attr("abs:href")) && pattern.matcher(page.attr("href")).find()) {
							System.out.println(page.attr("abs:href"));
							start(page.attr("abs:href"), depth);
							listOfLinks.add(page.attr("abs:hred"));
						}
					}
				}
				
			} catch (Exception e) {
//				System.out.println("Failed to crawl the webpage.");
			}
		}
	}
	
	/**
	 * Verify url by checking the extension and checking if the url is already parsed
	 * @param nextUrl - url to check
	 * @return - true if url is for an html page else false
	 */
	private boolean verifyUrl(String nextUrl) {
		if (listOfLinks.contains(nextUrl))
			return false;
		if (nextUrl.endsWith(".html"))
			return true;
		return false;
	}
	
	/**
	 * Method to test web crawler
	 * @param args
	 */
	public static void main(String... args) {
		String url = "https://www.guru99.com/software-testing-introduction-importance.html";
		new WebCrawler().start(url, 0);
	}

}
