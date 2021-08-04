import java.util.*;
import java.io.*;

public class WordCounter {
    public static void main(String[] args){
        /** create a WordCountMap and create a tree, get the List<WordCount> by descending order **/
        String textFileName = args[0];
        WordCountMap map = new WordCountMap(textFileName, "StopWords.txt");
        List<WordCount> wc = map.getWordCountsByCount();

        /** prints out the entire List<WordCount>**/
        if (args.length == 1) {
            for (int i = 0; i < wc.size(); i++) {
                System.out.println(wc.get(i).getWord() + " : " + wc.get(i).getCount());
            }
        }

        /** use WorldCloudMaker to generate html codes for the word cloud, and convert that into a html file by using codes found in the reading Anna provides **/
        else if (args.length == 3) {
            String numberOfWordsToInclude = args[1];
            String outFileName = args[2];
            List<WordCount> wc2 = new ArrayList<WordCount>();
            for (int i = 0; i < Integer.parseInt(numberOfWordsToInclude); i++) {
                wc2.add(wc.get(i));
            }

            String html = WordCloudMaker.getWordCloudHTML(outFileName, wc2);
            boolean fileOpened;
            PrintWriter toFile = null;
            try {
                toFile = new PrintWriter(outFileName);
                fileOpened = true;
            }
            catch (FileNotFoundException e) {
                fileOpened = false;
                System.err.println("File not found.");
            }
            if (fileOpened == true) {
                Scanner scanner = new Scanner(html);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    toFile.println(line);
                    //System.out.println(line);
                }
                scanner.close();
                toFile.close();
            }
        }
    }
}