import java.util.EmptyStackException;
import java.util.*;
/** 
 * An implementation of the Stack ADT.
 * 
 * @param <T> The type of data that the stack stores.
 * 
 * @author Jadrian Miles
 * @author Anna Rafferty
 */

public class MysteryStackImplementation<T> implements Stack<T> {
    // The array storing all the _s.
    // INVARIANT: The first _n elements of the array have items stored in
    //            them; the rest of the array is nulls.
    private T[] _s;
    // Magic number for default initial size of the array.
    private static final int DS = 16;
    // Magic number for the growth rate of the array.
    private static final int AGF = 2;
    // The number of items currently stored in the stack.
    // INVARIANT: This number is accurate before and after each method call.
    private int _n;
    
    /** Default constructor: creates an empty stack. */
    public MysteryStackImplementation() {
        allocateEmpty();
    }
    
    //return the size of the stack
    public int size() {
        return _n;
    }

    //convert stack in an arraylist
    public ArrayList<T> toArray() {
        ArrayList<T> array = new ArrayList<T>();
        while (!this.isEmpty()) {
            array.add(this.pop());
        }
        return array;
    }

    public void push(T item) {
        ensureCapacity();
        _s[_n] = item;
        _n++;
    }

    public T pop() {
        if(_n == 0) {
            throw new EmptyStackException();
        }
        _n--;
        T item_on_top = _s[_n];
        _s[_n] = null;
        return item_on_top;
    }

    public T peek() {
        if(_n == 0) {
            throw new EmptyStackException();
        }
        return _s[_n - 1];
    }

    public boolean isEmpty() {
        return (_n == 0);
    }

    public void clear() {
        allocateEmpty();
    }

    private void allocateEmpty() {
        @SuppressWarnings("unchecked")
          T[] tmp = (T[]) new Object[DS];
        _s = tmp;
        _n = 0;
    }

    private void ensureCapacity() {
        if(_n < _s.length) {
            return;
        }
        @SuppressWarnings("unchecked")
          T[] tmp = (T[]) new Object[_s.length * AGF];
        for(int i = 0; i < _s.length; ++i) {
            tmp[i] = _s[i];
        }
        _s = tmp;
    }
    
    public static void main(String[] args) {
        // Test out the ADT methods.
        String s;
        Stack<String> testStack = new MysteryStackImplementation<String>();
          System.out.printf(" isEmpty? %b\n", testStack.isEmpty());
        testStack.push("Four");  System.out.println("testStack.push(\"Four\")");
          System.out.printf(" isEmpty? %b\n", testStack.isEmpty());
          System.out.printf(" top: %s\n", testStack.peek());
        testStack.push("Score");  System.out.println("testStack.push(\"Score\")");
          System.out.printf(" top: %s\n", testStack.peek());
        s = testStack.pop();  System.out.println("testStack.pop()");
          System.out.printf(" popped: %s\n", s);
          System.out.printf(" top: %s\n", testStack.peek());
        testStack.push("Seven");  System.out.println("testStack.push(\"Seven\")");
          System.out.printf(" top: %s\n", testStack.peek());
        s = testStack.pop();  System.out.println("testStack.pop()");
          System.out.printf(" popped: %s\n", s);
          System.out.printf(" isEmpty? %b\n", testStack.isEmpty());
          System.out.printf(" top: %s\n", testStack.peek());
        s = testStack.pop();  System.out.println("testStack.pop()");
          System.out.printf(" popped: %s\n", s);
          System.out.printf(" isEmpty? %b\n", testStack.isEmpty());
          System.out.printf(" top: %s\n", testStack.peek());
        s = testStack.pop();  System.out.println("testStack.pop()");
          System.out.printf(" popped: %s\n", s);
          System.out.printf(" isEmpty? %b\n", testStack.isEmpty());
          System.out.printf(" top: %s\n", testStack.peek());
    }
}
