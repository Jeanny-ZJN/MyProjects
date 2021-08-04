import java.time.*;
import java.time.format.DateTimeFormatter;

public class Flashcard implements Comparable<Flashcard> {

    private String front;
    private String back;
    private LocalDateTime ldt;

    /**
    * Creates a new flashcard with the given dueDate, text for the front
    * of the card (front), and text for the back of the card (back).
    * dueDate must be in the format YYYY-MM-DDTHH:MM. For example,
    * 2020-05-04T13:03 represents 1:03PM on May 4, 2020. It's
    * okay if this method crashes if the date format is incorrect.
    * In the format above, the time may or may not include milliseconds. 
    * The parse method in LocalDateTime can deal with this situation
    *  without any changes to your code.
    */
    public Flashcard(String dueDate, String front, String back) {
        this.front = front;
        this.back = back;
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            ldt = LocalDateTime.parse(dueDate, formatter);
        }
        catch (Exception e) {
            //e.printStackTrace();
            System.err.println("Please input due date in a valid format.");
        }
    }

    /**
    * Gets the text for the front of this flashcard.
    */
    public String getFrontText() {
        return this.front;
    }

    /**
    * Gets the text for the Back of this flashcard.
    */
    public String getBackText() {
        return this.back;
    }

    /**
    * Gets the time when this flashcard is next due.
    */
    public LocalDateTime getDueDate() {
        return this.ldt;
    }

    public int compareTo(Flashcard card) {
        if (this.getDueDate().isBefore(card.getDueDate())) {
            return -1;
        }
        else if (this.getDueDate().isAfter(card.getDueDate())) {
            return 1;
        }
        else {
            return 0;
        }
    }

    public static void main(String[] args) {
        Flashcard card = new Flashcard("2020-05-28T22:00","Carboxylic Acid","4");
        System.out.println(card.getDueDate());
        System.out.println(card.getFrontText());
        System.out.println(card.getBackText());
    }
}