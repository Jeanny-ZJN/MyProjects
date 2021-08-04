import java.util.*;
import java.io.*;
//import java.time.*;

public class FlashcardPriorityQueue implements PriorityQueue<Flashcard> {
  
    private Flashcard[] array;
    private int arraySize;
    private int size;

    /**
    * Creates an empty priority queue with specific size.
    */
    public FlashcardPriorityQueue(int size) {
        this.array = new Flashcard[size];
        this.arraySize = 0;
        this.size = size;
    }

    /** adds a flashcard to the array **/
    public void add(Flashcard card) {
        array[arraySize] = card;
        int parentIndex = -1;
        int cardIndex = -1;
        if (arraySize < size - 1) {
            arraySize++;
            parentIndex = (arraySize-2)/5;
            cardIndex = arraySize - 1;
        }
        else if (arraySize == size - 1) {
            parentIndex = (size-2)/5;
            cardIndex = size - 1;
        }
        while (parentIndex >= 0) {
            if (array[parentIndex].compareTo(card) > 0) {
                swap(parentIndex, cardIndex);
                cardIndex = parentIndex;
                parentIndex = (parentIndex-1)/5;
            }
            else {
                parentIndex = -2;
            }
        }    
    }

    public void swap(int index1, int index2) {
        Flashcard temp; 
        temp = array[index2];
        array[index2] = array[index1];
        array[index1] = temp;
    }

    /** polls the first item in the array. I tried to swap the last item in the array to the front, compare it with its children and keep swapping until the order is correct, but I kept getting errors and I couldn't get it to work. So I made this method which first makes the parentIndex null and selects the child with the earliest due date to be the new parent until reaching the end of the array. But then it leaves a null spot in the array, so the last for loop shifts items forward **/
    public Flashcard poll() {
        try {
            Flashcard card = array[0];
            array[0] = null;
            int parentIndex = 0;
            while (0 <= parentIndex && parentIndex < (this.size-1)/5) {
                int nextParentIndex = -100;
                boolean through = false;
                Flashcard min = array[5*parentIndex+1];
                for (int i = 5*parentIndex+2; i < 5*parentIndex+6; i++) {
                    if (i < size) {
                        if (array[i] != null && min.compareTo(array[i]) > 0) {
                            min = array[i];
                            nextParentIndex = i;
                            through = true;
                        }
                    }
                }
                if (through == false) {
                    nextParentIndex = 5*parentIndex+1;
                }
                array[parentIndex] = min;
                array[nextParentIndex] = null;
                parentIndex = nextParentIndex;
            }
            for (int j = 0; j < size - 1; j++) {
                if (array[j] == null) {
                    array[j] = array[j+1];
                    array[j+1] = null;
                }
            }
            // for (int n = 0; n < arraySize + 1; n++) {
            //     System.out.println(array[n].getBackText());
            // }
            return card;
        }
        catch(NoSuchElementException e) {
            System.err.print("Sorry, the queue is currently empty.");
            return null;
        }
    }

    public Flashcard peek() {
        try {
            return array[0];
        }
        catch(NoSuchElementException e) {
            System.err.print("Sorry, the queue is currently empty.");
            return null;
        }
    }

    public boolean isEmpty() {
        if (arraySize <= 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public void clear() {
        array = null;
        this.arraySize = 0;
    }

    public static void main(String[] args) {
        Flashcard a = new Flashcard("2020-05-28T22:00","Carboxylic Acid","4");
        Flashcard b = new Flashcard("2020-05-27T22:00","Sulfuric Acid","-3");
        Flashcard c = new Flashcard("2020-05-26T22:00","Thiols","13");
        Flashcard d = new Flashcard("2020-05-25T22:00","Malonates","13");
        Flashcard e = new Flashcard("2020-05-24T22:00","Alcohol","17");
        Flashcard f = new Flashcard("2020-05-23T22:00","Keyton/Aldehyde","20-24");
        Flashcard g = new Flashcard("2020-05-22T22:00","Nitrile","25");
        Flashcard h = new Flashcard("2020-05-21T22:00","Ester","25");
        Flashcard i = new Flashcard("2020-05-20T22:00","Amine","35");
        Flashcard j = new Flashcard("2020-05-19T22:00","Alkane","50");
        FlashcardPriorityQueue queue = new FlashcardPriorityQueue(10);
        queue.add(a);
        //System.out.println(queue.peek().getFrontText());
        queue.add(b);
        //System.out.println(queue.peek().getFrontText());
        queue.add(c);
        //System.out.println(queue.peek().getFrontText());
        queue.add(d);
        //System.out.println(queue.peek().getFrontText());
        queue.add(e);
        //System.out.println(queue.peek().getFrontText());
        queue.add(f);
        //System.out.println(queue.peek().getFrontText());
        queue.add(g);
        //System.out.println(queue.peek().getFrontText());
        queue.add(h);
        //System.out.println(queue.peek().getFrontText());
        queue.add(i);
        //System.out.println(queue.peek().getFrontText());
        queue.add(j);
        //System.out.println(queue.peek().getFrontText());
        Flashcard card = queue.poll();
        queue.add(card);
        //queue.clear();

        for (int n = 0; n < queue.arraySize; n++) {
            System.out.println(queue.array[n].getFrontText());
        }
    }
}