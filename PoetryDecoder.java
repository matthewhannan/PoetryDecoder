
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Scanner;

/**
 * STUDENTS FILL IN PROPER DOCUMENTATION HERE
 * @author Matthew Hannan
 * COP 3502 Section Number: 1A87
 *
 */
public class PoetryDecoder {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
	
		System.out.println("Please enter your input: ");
		String hex = sc.nextLine();
 
		System.out.print(decode(hex));
		
		sc.close();
	}

	public static String decode(String hex) {
		
		//set up individual arrays for confirmed words of lengths 4,5, and 6
		String shortest[] = (findWordsOfLength(toEnglishLetters(hex),4));
		String medium[] = (findWordsOfLength(toEnglishLetters(hex),5));
		String longest[] = (findWordsOfLength(toEnglishLetters(hex),6));
		//formatted the confirmed english words into poetic format
		String finalPoem = formatPoem(shortest,medium,longest);
		
		return finalPoem;
	
	}

	public static String toEnglishLetters(String hex){
		//cuts the last letter of the string if the string is of odd length
		if (hex.length() % 2 != 0){
			
			hex = hex.substring(0, hex.length() - 1);
		}
		
		String letters = "";
		//turns hexidecimals given to english letters using ascii values from the chart
		for (int z = 0; z < hex.length(); z = z + 2){

			String subString = hex.substring(z,z+2);
			
			int ASCII = Integer.parseInt(subString, 16);
			
			if ((ASCII >= 65 && ASCII <= 90) || ASCII >= 97 && ASCII <= 122){
				letters += (char)ASCII;
			}
			
			else{
				ASCII = (ASCII % 26) + 97;
				letters += (char)ASCII;
			}
			
		}
		return letters;
	}

	public static String[] findWordsOfLength(String letters, int wordSize){
		//counts length of english letter string
		int lettersLength = letters.length();
		//algorithm to calculate number of possibilities of a specific letter length word within the english string given
		String[] possibleWords = new String[(letters.length() - wordSize) + 1];
		
		int counter = -1;
		//runs through array of possibilities and creates an array of equal length but only stores confirmed words first and the rest nulls
		for (int i = 0; i < possibleWords.length; i++){
			String subString = letters.substring(i, i + wordSize);
			
			boolean wordOrNot = isWord(subString);
			
			if (wordOrNot == true){
				counter += 1;
				possibleWords[counter] = subString;
			}
		}
		
		return possibleWords;

	}

	public static String formatPoem(String[] shortest, String[] medium, String[] longest){
		
		String formattedPoem = "";
		
		//three counter variables to adjust the position the pointer points to in each array
		
		int small = 0;
		int mid = 0;
		int large = 0;

		int shortestLen = shortest.length;
		int mediumLen = medium.length;
		int longestLen = longest.length;

		// Counter to tab the appropriate amount of times per new line
		int counter = 1;

		while ((small < shortestLen) || (mid < mediumLen) || (large < longestLen)){
			// Flags to check if there was  a mid, or large word on this line. if so, add a space.
			// Used so we don't need to check if medium is in bounds and medium is not null in when printing the
			// large word for example.
			boolean hadMed = false;
			boolean hadLarge = false;


			if (large < longestLen){
				if (longest[large] == null) {
					// Increment, but don't print anything because we don't want to print null
					large+=1;
				} 
				else {
					formattedPoem += longest[large];
					large += 1;
					hadLarge = true;
				}
			}
			
			if (mid < mediumLen){
				if (medium[mid] == null) {
					// Increment, but don't print anything because we don't want to print null 
					mid+=1;
				} else {
					if (hadLarge) {
						formattedPoem += " ";
					}
					formattedPoem += medium[mid];
					mid += 1;
					hadMed = true;
				}
			}
			
			if (small < shortestLen){
				if (shortest[small] == null) {
					// Increment, but don't print anything because we don't want to print null
					small+=1;
				} else {
					if (hadMed) {
						formattedPoem += " ";
					}
					formattedPoem += shortest[small];
					small += 1;
				}
			}
			
			//increment how many times we return to a new line
			formattedPoem += "\n";

			//loop to print the correct number of tabs per line as indicated by the counter
			for (int i = 0; i < counter; i++){
				formattedPoem += "\t";
			}
			
			counter++;
		}
		
		return formattedPoem;
		
	}

	private static boolean isWord(String possWord) {
		boolean isWord = true;
		try {
			//connect to the URL. 
			String s = getUrl(possWord);
			Document d = Jsoup.connect(s).timeout(6000).get();
			Elements tdTags = d.select("h3");

			// Loop over all tdTags, in this case: the h3 tag 
			for( Element element : tdTags ){
				String check = element.toString();

				//Wordnet has a special h3 tag that appears only if the word is not in the dictionary
				//We search for this tag. If it is found, then the word searched is not in the dictionary
				if(check.equals("<h3>Your search did not return any results.</h3>") ){
					isWord = false;
				}
			}
		}
		catch (IOException e) {
			System.err.print("CHECK INTERNET CONNECTION. Could not connect to jsoup URL.");
			System.exit(0);
		}
		return isWord;
	}

	private static String getUrl(String search) {
		//Standard URL for wordnet to search
		String url = "http://wordnetweb.princeton.edu/perl/webwn?s=";
		String newURL = null;
		try {
			//Get new page from word wordnet and get its location
			Document doc = Jsoup.connect(url + search).timeout(6000).get();
			newURL = doc.location().toString();
		}
		catch (IOException e) {
			System.err.print("CHECK INTERNET CONNECTION. Could not connect to jsoup URL.");
			System.exit(0);
		}
		//Return the string of the new URL. 
		return (newURL);
	}

}

