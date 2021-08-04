import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.*;

public class FlashcardDisplayer {

    private FlashcardPriorityQueue queue;
    private int queueSize = 0;

    /**
    * Creates a flashcard displayer with the flashcards in file.
    * File has one flashcard per line. On each line, the date the flashcard 
    * should next be shown is first (format: YYYY-MM-DDTHH-MM), followed by a comma, 
    * followed by the text for the front of the flashcard, followed by another comma,
    * followed by the text for the back of the flashcard. You can assume that the 
    * front/back text does not itself contain commas. (I.e., a properly formatted file
    * has exactly 2 commas per line.)
    * In the format above, the time may be omitted, or the time may be more precise
    * (e.g., seconds may be included). The parse method in LocalDateTime can deal
    * with these situations without any changes to your code.
    */
    public FlashcardDisplayer(String file) {
        try {
            List<String[]> eachLineInFile = new ArrayList<String[]>();
            Scanner scanner = new Scanner(new File(file));
            while (scanner.hasNext()) {
                String next = scanner.nextLine();
                String[] nextLine = next.split(",");
                eachLineInFile.add(nextLine);
                queueSize++;
            }
            queue = new FlashcardPriorityQueue(queueSize);
            for (String[] each : eachLineInFile) {
                Flashcard eachCard = new Flashcard(each[0], each[1], each[2]);
                queue.add(eachCard);
            }
            scanner.close();
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found.");
            System.exit(0);
        }
    }

    /**
    * Writes out all flashcards to a file so that they can be loaded
    * by the FlashcardDisplayer(String file) constructor. Returns true
    * if the file could be written. The FlashcardDisplayer should still
    * have all of the same flashcards after this method is called as it
    * did before the method was called. However, it may be that flashcards
    * with the same exact next display date and time are removed in a different order.
    */
    public boolean saveFlashcards(String outFile) {
        PrintWriter toFile = null;
        boolean fileOpened;
        boolean written = false;
        try {
            toFile = new PrintWriter(outFile);
            fileOpened = true;
        }
        catch (FileNotFoundException e) {
            fileOpened = false;
            System.err.println("outFile not found.");
        }
        if (fileOpened == true && !queue.isEmpty()) {
            for (int n = 0; n < queueSize; n++) {
                Flashcard eachCard = queue.poll();
                String line = eachCard.getDueDate() + "," + eachCard.getFrontText() + "," + eachCard.getBackText();
                toFile.println(line);
            }
            queue.clear();
            written = true;
            toFile.close();
        }
        else {
            System.out.println("Sorry, you cannot save right now.");
        }
        return written;
    }

    /**
    * Displays any flashcards that are currently due to the user, and 
    * asks them to report whether they got each card correct. If the
    * card was correct, it is added back to the deck of cards with a new
    * due date that is one day later than the current date and time; if
    * the card was incorrect, it is added back to the card with a new due
    * date that is one minute later than that the current date and time.
    */
    public void displayFlashcards() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        boolean through = false;
        while (!queue.isEmpty() && through != true) {
            Scanner keyboard = new Scanner(System.in);
            Flashcard card = queue.peek();
            if (card.getDueDate().isBefore(now) || card.getDueDate().isEqual(now)) {
                queue.poll();
                System.out.println("Card: "); 
                System.out.println(card.getFrontText());
                System.out.print("[Press return for back of card]");
                String input = keyboard.nextLine();
                while (!input.equals("") && !input.equals("exit")) {
                    System.out.println("Please enter something valid.");
                    input = keyboard.nextLine();
                }
                if (input.equals("exit")) {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                System.out.println(card.getBackText());
                System.out.println("Press 1 if you got the card correct and 2 if you got the card incorrect.");
                int input2 = keyboard.nextInt();
                while (input2 != 1 && input2 != 2) {
                    System.out.println("Please enter something valid.");
                    input2 = keyboard.nextInt();
                }
                if (input2 == 1) {
                    LocalDateTime time = now.plusDays(1);
                    String newTime = time.format(formatter);
                    Flashcard newCard = new Flashcard(newTime,card.getFrontText(),card.getBackText());
                    queue.add(newCard);
                }
                if (input2 == 2) {
                    LocalDateTime time = now.plusMinutes(1);
                    String newTime = time.format(formatter);
                    Flashcard newCard = new Flashcard(newTime,card.getFrontText(),card.getBackText());
                    queue.add(newCard);
                }
            }
            else {
                through = true;
            }
        }
        System.out.println("No cards are waiting to be studied!");
        // for (int n = 0; n < queueSize; n++) {
        //     System.out.println(queue.poll());
        // }
    }
 
    public static void main(String[] args) {
        String fileName = args[0];
        FlashcardDisplayer displayer = new FlashcardDisplayer(fileName);
        System.out.println("Time to practice flashcards! The computer will display your flashcards, you generate the response in your head, and then see if you got it right. The computer will show you cards that you miss more often than those you know!");
        System.out.println("Enter a command (quiz, save, exit):");
        Scanner keyboard = new Scanner(System.in);
        String response = keyboard.nextLine();
        while (!response.equals("exit")) {
            if (response.equals("quiz")) {
                displayer.displayFlashcards();
                System.out.println("Enter a command (quiz, save, exit):");
                response = keyboard.nextLine();
            }
            else if (response.equals("save")) {
                System.out.println("Type a filename where you'd like to save the flashcards:");
                String outFile = keyboard.nextLine();
                displayer.saveFlashcards(outFile);
                System.out.println("Enter a command (quiz, save, exit):");
                response = keyboard.nextLine();
            }
            else {
                System.out.println("Please enter a valid command.");
                response = keyboard.nextLine();
            }
        }
        keyboard.close();
        System.out.println("Goodbye!");
        System.exit(0);
    }
}