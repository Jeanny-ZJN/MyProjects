import java.util.*;
import java.io.*;

public class WordCountMap {

    private List<String> file = new ArrayList<String>();
    private List<String> stopWords = new ArrayList<String>();
    private TreeNode root = new TreeNode(" ");

    private class TreeNode {
        private String item;
        private int count;
        private List<TreeNode> children;
        
        public TreeNode(String item) {
            this.item = item;
            this.children = new ArrayList<TreeNode>();
            this.count = 0;
        }
    }

    /** constructor of WordCountMap, loads the file and the stopwords and creates a tree using incrementCount method **/
    public WordCountMap(String FileName, String StopWords) {
        try {
            Scanner scanner = new Scanner(new File(StopWords));
            while (scanner.hasNext()) {
                String next = scanner.nextLine();
                stopWords.add(next);
            }
            scanner.close();
        }
        catch (FileNotFoundException e) {
            System.err.println("StopWords not found.");
        }

        try {
            Scanner scanner2 = new Scanner(new File(FileName));
            while (scanner2.hasNext()) {
                String next2 = scanner2.nextLine();
                String[] nextLine = next2.split(" ");
                for (String s : nextLine) {
                    if (s.startsWith("'")) {
                        s = s.substring(1);
                    }
                    if (s.endsWith("'")) {
                        s = s.substring(0, s.length() - 2);
                    }
                    s = s.replace("-", "");
                    s = s.replaceAll("(?!['])\\p{Punct}", "");
                    s = s.toLowerCase();
                    if (!stopWords.contains(s) && s.length() > 0) {
                        file.add(s);
                        incrementCount(s);
                    }
                }
            }
            scanner2.close();
        }
        catch (FileNotFoundException e) {
            System.err.println("FileName not found.");
        }
        //System.out.println(file);
    }

    /** converts a character to string **/
    private String toString(Character c) {
        String s = Character.toString(c);
        return s;
    }

   /**
    * Adds 1 to the existing count for word, or adds word to the WordCountMap
    * with a count of 1 if it was not already present.
    * Implementation must be recursive, not iterative.
    */
    public void incrementCount(String word) {
        int index = 0;
        char[] character = word.toCharArray();
        incrementCountHelper(root, character, index);
    }

    private void incrementCountHelper(TreeNode node, char[] character, int index) {
        if (index == character.length - 1) {
            boolean through = false;
            for (TreeNode n : node.children) {
                if (n.item.equals(toString(character[index]))) {
                    through = true;
                    n.count++;
                }
            }
            if (through == false) {
                TreeNode newNode = new TreeNode(toString(character[index]));
                node.children.add(newNode);
                newNode.count++;
            }
        }
        else if (index < character.length - 1) {
            boolean through = false;
            for (TreeNode n : node.children) {
                if (n.item.equals(toString(character[index]))) {
                    through = true;
                    incrementCountHelper(n, character, index+1);
                }
            }
            if (through == false) {
                TreeNode newNode = new TreeNode(toString(character[index]));
                node.children.add(newNode);
                incrementCountHelper(newNode, character, index+1);
            }         
        }
    }

   /**
    * Remove 1 to the existing count for word. If word is not present, does
    * nothing. If word is present and this decreases its count to 0, removes
    * any nodes in the tree that are no longer necessary to represent the
    * remaining words.
    */
    public void decrementCount(String word) {
        int index = 0;
        char[] character = word.toCharArray();
        decrementCountHelper(root, character, index);
    }

    private void decrementCountHelper(TreeNode node, char[] character, int index) {
        if (index == character.length - 1) {
            for (TreeNode n : node.children) {
                if (n.item.equals(toString(character[index]))) {
                    n.count--;
                    if (n.count == 0 && n.children.isEmpty()) {
                        node.children.remove(n);
                    }
                    break;
                }
            }
        }
        else if (index < character.length - 1) {
            for (TreeNode n : node.children) {
                if (n.item.equals(toString(character[index]))) {
                    decrementCountHelper(n, character, index+1);
                }
            }
        }
    }

   /**
    * Returns true if word is stored in this WordCountMap with
    * a count greater than 0, and false otherwise.
    * Implementation must be recursive, not iterative.
    */
    public boolean contains(String word) {
        int index = 0;
        char[] character = word.toCharArray();
        return containsHelper(root, character, index);
    }

    private boolean containsHelper(TreeNode node, char[] character, int index) {
        boolean contains = false;
        if (index == character.length - 1) {
            for (TreeNode n : node.children) {
                if (n.item.equals(toString(character[index])) && n.count > 0) {
                    contains = true;
                    break;
                }
            }
            return contains;
        }
        else {
            for (TreeNode n : node.children) {
                if (n.item.equals(toString(character[index]))) {
                    return containsHelper(n, character, index+1);
                }
            }
            return contains; 
        }
    }

   /**
    * Returns the count of word, or -1 if word is not in the WordCountMap.
    * Implementation must be recursive, not iterative.
    */
    public int getCount(String word) {
        int index = 0;
        char[] character = word.toCharArray();
        return getCountHelper(root, character, index);
    }

    private int getCountHelper (TreeNode node, char[] character, int index) {
        boolean through = false;
        int count = -100;
        TreeNode cur = new TreeNode(null);
        if (index == character.length - 1) {
            for (TreeNode n : node.children) {
                if (n.item.equals(toString(character[index]))) {
                    through = true;
                    count = n.count;
                    break;
                }
            }
            if (through == false) {
                return count = -1;
            }
            else {
                return count;
            }
        }
        else {
            for (TreeNode n : node.children) {
                if (n.item.equals(toString(character[index]))) {
                    through = true;
                    cur = n;
                }
            }
            if (through == false) {
                return count = -1;
            }
            else {
                return getCountHelper(cur, character, index+1);
            }
        }
    }

   /** 
    * Returns a list of WordCount objects, one per word stored in this 
    * WordCountMap, sorted in decreasing order by count. 
    */
    public List<WordCount> getWordCountsByCount() {
        List<WordCount> list = new ArrayList<WordCount>();
        List<String> allWords = new ArrayList<String>();
        for (String word : file) {
            if (!allWords.contains(word)) {
                allWords.add(word);
            }
        }
        for (String word : allWords) {
            WordCount wordCount = new WordCount(word,getCount(word));
            list.add(wordCount);
        }
        Collections.sort(list, WordCount.sortCount);
        return list;
    }

   /** 
    * Returns a count of the total number of nodes in the tree. 
    * A tree with only a root is a tree with one node; it is an acceptable
    * implementation to have a tree that represents no words have either
    * 1 node (the root) or 0 nodes.
    * Implementation must be recursive, not iterative.
    */
    public int getNodeCount() {
        int nodeCount = 1;
        return getNodeCountHelper(root, nodeCount);
    }

    private int getNodeCountHelper(TreeNode node, int nodeCount) {
        for (int i = 0; i < node.children.size(); i++) {
            if (!node.children.get(i).children.isEmpty()) {
                nodeCount++;
                nodeCount = getNodeCountHelper(node.children.get(i), nodeCount);
            }
            else {
                nodeCount++;
            }
        }
        return nodeCount;
    }

    public static void main(String[] args) {
        /** use words in test.txt to test out each method in WordCountMap **/
        String fileName = args[0];
        String stopWords = args[1];
        WordCountMap map = new WordCountMap(fileName, stopWords);
        String word = "refrain";
        //String word2 = "reckoned";        
        System.out.println(map.getCount(word));
        //System.out.println(map.getCount(word2));
        map.decrementCount(word);
        //map.incrementCount(word);
        System.out.println(map.getCount(word));
        //System.out.println(map.getCount(word2));
        System.out.println(map.contains(word));
        System.out.println(map.getNodeCount());
        
        /** prints out the top 10 most used words in test.txt **/
        List<WordCount> wc = map.getWordCountsByCount();
        for (int i = 0; i < 10; i++) {
            System.out.println(wc.get(i).getWord() + " " + wc.get(i).getCount());
        }
    }
}