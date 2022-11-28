package src.searchEngine;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class SearchEngine {
	
	static Scanner sc = new Scanner(System.in);
	
	/**
	 * Main method to initiate the search engine
	 * @param args
	 */
	public static void main(String...args) {
		SearchEngine engine = new SearchEngine();
		
		System.out.println("******************** Search Engine **************\n Initializing....");		

		int input = 0;
		do {
			System.out.println("Please select one of the options:");
			System.out.println("1) Enter '1' to start web crawling from the URL you will enter");
			System.out.println("2) Enter '2' to quit");

			//getting user input for search engine
			input = sc.nextInt();
			
			switch (input) {
			case 1:
				System.out.println("\nPlease enter valid URL:");
				String url = sc.next();
				if (!url.startsWith("https://")) {
					url = "https://" + url;
				}
				engine.searchWord(url);
				break;
				
			default:
				System.out.println("Exiting.........");
				input = 0;
			}
			
		} while (input != 0);
		System.out.println("Thank you very much for using the search engine");
	}
	
	/**
	 * Crawling the website and then searching the word
	 * @param url - url for crawling
	 */
	private void searchWord(String url) {
		if (!isValidUrl(url)) {
			System.out.println("Entered url is invalid!");
			System.out.println("Please try again....");
			return;
		}
		
		WebCrawler crawler = new WebCrawler();
		SearchWord searchWord = new SearchWord();
		
		System.out.println("Entered url is valid....");
		System.out.println("Web crawling started...");
		crawler.start(url, 0);
		System.out.println("Web crawling completed successfully");
		
		Map<String, Integer> files = new HashMap<>();
		
		String choice = "Y";
		do {
			System.out.println("Search Query: \n");
			System.out.println("Enter a word you want to search...");
			String wordToSearch = sc.next();
			
			int noOfWords = 0, totalNoOfFiles = 0;
			files.clear();
			
			try {
				
				System.out.println("\nSearching the word...");
				File filePath = new File(PathFinder.txtDirectoryPath);
				
				for (File file: filePath.listFiles()) {
					try {
						In fileIn = new In(file.getAbsolutePath());
						
						String txtFile = fileIn.readAll();
						fileIn.close();
						Pattern pattern = Pattern.compile("::");
						String[] fileName = pattern.split(txtFile);
						noOfWords = searchWord.wordSearch(txtFile, wordToSearch.toLowerCase(), fileName[0]);
	
						if (noOfWords != 0) {
							files.put(fileName[0], noOfWords);
							totalNoOfFiles++;
						}
					} catch(Exception e) {
//						System.out.println("Failed to open file: " + e.getMessage());
					}
				}

				if(totalNoOfFiles>0) {
					System.out.println("\nNumber of files which are containing the word : " + wordToSearch + " is : " + totalNoOfFiles);
				}
				else {
					System.out.println("\n File not found for the word containing : "+ wordToSearch);
					System.out.println("\nSeeing if an alternative word exists...");		
					
					searchWord.suggestAlternativeWord(wordToSearch.toLowerCase()); 
				}
				
				searchWord.rankFiles(files, totalNoOfFiles);
			} catch (Exception e) {
				System.out.println("Exception:" + e);
			}
			System.out.println("\n Do you want return to search another word(y/n)?");
			choice = sc.next();
		} while (choice.equalsIgnoreCase("y"));
		
		cleanUp();
	}
	
	/**
	 * Deleting all the files generated during the search
	 */
	private void cleanUp() {
		File textFiles = new File(PathFinder.txtDirectoryPath);
		
		for(File file: textFiles.listFiles()) {
			file.delete();
		}
		
		File htmlFiles = new File(PathFinder.htmlDirectoryPath);
		
		for(File file: htmlFiles.listFiles())
			file.delete();
	}
	
	/**
	 * Checking if the url entered by the user is valid or not
	 * @param url
	 * @return boolean value: true if url is valid
	 */
	private boolean isValidUrl(String url) {
		try {
        	System.out.println("Validating the url...");
        	URL URLObject = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) URLObject.openConnection();
            connection.setRequestMethod("GET");
            int response = connection.getResponseCode();
            System.out.println(response);
            if(response==200) {
            	 return true;
            }
            else {
            	return false;
            }
        }
        catch (Exception e) {
            return false;
        }
	}
}
