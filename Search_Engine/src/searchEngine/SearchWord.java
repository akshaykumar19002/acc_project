package src.searchEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchWord {

	List<String> keys;
	Map<String, Integer> wordScores;

	/**
	 * Constructor to initialize the keys and numbers datastructures.
	 */
	public SearchWord() {
		keys = new ArrayList<>();
		wordScores = new HashMap<>();
	}

	/**
	 * Rank the top 3 files based on the occurrence of searched word in the html files
	 * @param files -> map with key as file name and value as count in that file for that word
	 * @param occurence
	 */
	public void rankFiles(Map<String, Integer> files, int occurence) {

		List<Map.Entry<?, Integer>> fileList = new ArrayList<>(files.entrySet());

		Collections.sort(fileList, new Comparator<Map.Entry<?, Integer>>() {

			@Override
			public int compare(Entry<?, Integer> o1, Entry<?, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		Collections.reverse(fileList);

		if (occurence != 0) {
			System.out.println("Page Ranking");
			System.out.println("\n-----Displaying top 3 search results ---------");

			int noOfFetch = 3;
			int j = 0;

			while (fileList.size() > j && noOfFetch > 0) {
				if (fileList.get(j).getKey() != null) {
					System.out.println(j+1 + ". " + fileList.get(j).getKey());
					j++;
				}
				noOfFetch--;
			}
		}
	}

	/**
	 * Prints a list of similar words compared to word entered by user.
	 * It uses edit distance algorithm to find similarity score.
	 * @param wordToSearch - word users wants to search
	 */
	public void suggestAlternativeWord(String wordToSearch) {
		String line = " ";
		String regexpattern = "[a-z0-9]+";
		int fileNumber = 0;

		Pattern pattern = Pattern.compile(regexpattern);
		Matcher matcher = pattern.matcher(line);

		File directory = new File(PathFinder.txtDirectoryPath);
		File[] fileList = directory.listFiles();
		for (int i = 0; i < fileList.length; i++) {
			try {
				findWord(fileList[i], fileNumber, matcher, wordToSearch);
				fileNumber++;
			} catch (FileNotFoundException err) {
				err.printStackTrace();
			}
		}
		
		Integer allowedDistance = 1;
		boolean matchFound = false;

		int i = 0;
		for (Map.Entry<String, Integer> entry : wordScores.entrySet()) {
			if (allowedDistance == entry.getValue()) {
				i++;
				if (i == 1)
					System.out.println("Did you mean? ");
				System.out.print("(" + i + ") " + entry.getKey() + "\n");
				matchFound = true;
			}
		}
		if (!matchFound)
			System.out.println("Entered word cannot be resolved....");
	}
	
	/**
	 * Updates the numbers hash map with words in the file and 
	 * @param srcFile
	 * @param fileNumber
	 * @param match
	 * @param str
	 * @throws FileNotFoundException
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public void findWord(File srcFile, int fileNumber, Matcher match, String wordToSearch)
			throws FileNotFoundException, ArrayIndexOutOfBoundsException {
		try {
			BufferedReader bufferObject = new BufferedReader(new FileReader(srcFile));
			String line = null;

			while ((line = bufferObject.readLine()) != null) {
				match.reset(line);
				while (match.find()) {
					keys.add(match.group());
				}
			}
			bufferObject.close();
			for (int l = 0; l < keys.size(); l++) {
				wordScores.put(keys.get(l), editDistanceAlgo(wordToSearch.toLowerCase(), keys.get(l).toLowerCase()));
			}
		} catch (Exception e) {
//			System.out.println("Exception:" + e);
		}

	}

	/**
	 * Returns edit distance score for two words
	 * @param word1 -> word
	 * @param word2 -> word
	 * @return -> edit distance score
	 */
	public int editDistanceAlgo(String word1, String word2) {
		int length1 = word1.length();
		int length2 = word2.length();

		int[][] my_array = new int[length1 + 1][length2 + 1];

		for (int i = 0; i <= length1; i++) {
			my_array[i][0] = i;
		}

		for (int j = 0; j <= length2; j++) {
			my_array[0][j] = j;
		}

		for (int i = 0; i < length1; i++) {
			char c1 = word1.charAt(i);
			for (int j = 0; j < length2; j++) {
				char c2 = word2.charAt(j);

				if (c1 == c2) {
					my_array[i + 1][j + 1] = my_array[i][j];
				} else {
					int replace = my_array[i][j] + 1;
					int insert = my_array[i][j + 1] + 1;
					int delete = my_array[i + 1][j] + 1;

					int min = replace > insert ? insert : replace;
					min = delete > min ? min : delete;
					my_array[i + 1][j + 1] = min;
				}
			}
		}
		return my_array[length1][length2];
	}

	/**
	 * Using KMP Algorithm to search for word in a specific file.
	 * @param readAllLines - string consisting of the content of the file
	 * @param word - word to search
	 * @param fileName - name of the html file
	 * @return - frequency of word in that file
	 */
	public int wordSearch(String readAllLines, String word, String fileName) {
		int counter = 0;
		int lineoffset = 0;

		KMP KMPObj = new KMP(word.toLowerCase());
		Scanner sc = new Scanner(readAllLines);

		while (sc.hasNextLine()) {
			String line = sc.nextLine().toLowerCase();

			int offset = KMPObj.search(line);

			lineoffset = offset;
			if (offset != line.length())
			{
				counter++;
				while (offset != line.length()) {
					int pos = offset + word.length();
					line = line.substring(pos);
					offset = KMPObj.search(line);
					int currentoffset = word.length() + offset;
					lineoffset += currentoffset;
					if (offset != line.length()) {
						counter++;
					}
				}
			}

		}
		sc.close();
		if (counter != 0) {
			System.out.println("Value found in the HTML file --> " + fileName + " --> " + counter + " times");
			System.out.println("-------------------------------------------------------------------------");
		}
		return counter;
	}
	
	public static void main(String... args) {
		String url = "https://www.guru99.com/software-testing-introduction-importance.html";
		new WebCrawler().start(url, 0);
		
		// testing alternate words
		new SearchWord().suggestAlternativeWord("test");
		
		// testing page ranking
		String word = "test";
		Map<String, Integer> files = new HashMap<>();
		int noOfFiles = 0;
			File filePath = new File(PathFinder.txtDirectoryPath);
			for (File file: filePath.listFiles()) {
				try {
					In fileIn = new In(file.getAbsolutePath());
					String txtFile = fileIn.readAll();
					fileIn.close();
					Pattern pattern = Pattern.compile("::");
					String[] fileName = pattern.split(txtFile);
					int noOfWords = new SearchWord().wordSearch(txtFile, word.toLowerCase(), fileName[0]);

					if (noOfWords != 0) {
						files.put(fileName[0], noOfWords);
						noOfFiles++;
					}
				} catch(Exception e) {
//					System.out.println("Failed to open file: " + e.getMessage());
				}
			}

			if(noOfFiles>0) {
				System.out.println("\nNumber of files which are containing the word : " + word + " is : " + noOfFiles);
			}
	}

}
