import java.util.Comparator;

public class WordCount {

    private String word;
    private int count;

    public WordCount(String word, int count) {
        this.word = word;
        this.count = count;
    }

    public String getWord() {
        return this.word;
    }
    
    public int getCount() {
        return this.count;
    }

    /** I created this method so I could sort the List<WordCount> in descending order using Collections.sort method **/
    public static Comparator<WordCount> sortCount = new Comparator<WordCount>() {
        public int compare(WordCount one, WordCount two) {
            int count1 = one.getCount();
            int count2 = two.getCount();
            return count2 - count1;
        } 
    };
} 