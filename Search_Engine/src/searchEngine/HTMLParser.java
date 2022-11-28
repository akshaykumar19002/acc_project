package src.searchEngine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HTMLParser {

	/**
	 * Method to save the HTML document, parse it and save it as a text document as well.
	 * @param doc - Document object with content of html page
	 * @param weburl - url of the html page
	 */
	public static void saveHTMLDoc(Document doc, String weburl) {
		try {

			PrintWriter htmlfile = new PrintWriter(
					new FileWriter(PathFinder.htmlDirectoryPath + doc.title().replace('/', '_') + ".html"));

			htmlfile.write(doc.toString());
			htmlfile.close();
			parseHtml(PathFinder.htmlDirectoryPath + doc.title().replace('/', '_') + ".html", weburl,
					doc.title().replace('/', '_') + ".txt");

		} catch (Exception e) {

		}

	}

	/**
	 * Parse the html file content and store the text of html in a txt file.
	 * @param htmlfile - path to html file
	 * @param weburl - url for html file
	 * @param textfile - name of text file
	 * @throws Exception - thrown in case parsing failed or file not found
	 */
	private static void parseHtml(String htmlfile, String weburl, String textfile) throws Exception {
		File nfile = new File(htmlfile);
		Document doc = Jsoup.parse(nfile, "UTF-8");
		String data = doc.text().toLowerCase();
		data = weburl + "::" + data;
		PrintWriter filewrite = new PrintWriter(PathFinder.txtDirectoryPath + textfile);
		filewrite.println(data);
		filewrite.close();
	}
	
	/**
	 * Main method to test the functionality
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String... args) throws IOException {
		String url = "https://www.guru99.com/software-testing-introduction-importance.html";
		Document document = Jsoup.connect(url).get();
		saveHTMLDoc(document, url);
	}
}
