import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
* Maze represents a maze that can be navigated. The maze
* should indicate its start and end squares, and where the
* walls are. 
*
* Eventually, this class will be able to load a maze from a
* file, and solve the maze.
* The starter code has part of the implementation of load, but
* it does not read and store the information about where the walls of the maze are.
*
*/
public class Maze { 
    //Number of rows in the maze.
    private int numRows;
    
    //Number of columns in the maze.
    private int numColumns;
    
    //Grid coordinates for the starting maze square
    private int startRow;
    private int startColumn;
    
    //Grid coordinates for the final maze square
    private int finishRow;
    private int finishColumn;
    
    //**************YOUR CODE HERE******************
    //You'll likely want to add one or more additional instance variables
    //to store the squares of the maze
    
    private int curRow;
    private int curColumn;
    private ArrayList<ArrayList<String>> maze;
    private ArrayList<String> descriptor;
    private MazeSquare mazeSquare;
    private Map<List<Integer>, MazeSquare> allSquares = new HashMap<List<Integer>, MazeSquare>();
    private MysteryStackImplementation<MazeSquare> stack;


    /**
     * Creates an empty maze with no squares.
     */
    public Maze() {
        //You can add any code you need to initialize instance 
        //variables you've added.
        this.maze = new ArrayList<ArrayList<String>>();
        this.descriptor = new ArrayList<String>();
        descriptor.add("*");
        descriptor.add("7");
        descriptor.add("_");
        descriptor.add("|");
        this.allSquares = new HashMap<List<Integer>, MazeSquare>();
        this.stack = new MysteryStackImplementation<MazeSquare>();
    } 
    
    /**
     * Loads the maze that is written in the given fileName.
     * Returns true if the file in fileName is formatted correctly
     * (meaning the maze could be loaded) and false if it was formatted
     * incorrectly (meaning the maze could not be loaded). The correct format
     * for a maze file is given in the assignment description. Ways 
     * that you should account for a maze file being incorrectly
     * formatted are: one or more squares has a descriptor that doesn't
     * match  *, 7, _, or | as a descriptor; the number of rows doesn't match
     * what is specified at the beginning of the file; or the number of
     * columns in any row doesn't match what's specified at the beginning
     * of the file; or the start square or the finish square is outside of
     * the maze. You can assume that the file does start with the number of
     * rows and columns.
     * 
     */
    public boolean load(String fileName) { 
        try {
        // convert txt file into arraylists
        Scanner scanner = new Scanner(new File(fileName));
        while (scanner.hasNext()) {
            String next = scanner.nextLine();
            ArrayList<String> mazeInfo = new ArrayList<String>();
            
            if (next.length() > 0) {
                for (int i = 0; i < next.length(); i++) {
                mazeInfo.add(String.valueOf(next.trim().charAt(i)));
                }
                maze.add(mazeInfo);
            }
            else {
                return false;
            }
        }

        // set instance variables accordingly
        numRows = Integer.parseInt(maze.get(0).get(2));
        numColumns = Integer.parseInt(maze.get(0).get(0));
        startRow = Integer.parseInt(maze.get(1).get(2));
        startColumn = Integer.parseInt(maze.get(1).get(0));
        finishRow = Integer.parseInt(maze.get(2).get(2));
        finishColumn = Integer.parseInt(maze.get(2).get(0));

        // check whether row, column, start square and finish square exit 
        for (int i = 0; i < 3; i++) {
            if (maze.get(i).get(0).equals(" ") || maze.get(i).get(2).equals(" ")) {
                return false;
            }
        }

        // check whether start square and finish square are within the maze boundary
        if (Integer.parseInt(maze.get(0).get(0)) > numColumns || Integer.parseInt(maze.get(0).get(2)) > numRows || Integer.parseInt(maze.get(1).get(0)) > numColumns || Integer.parseInt(maze.get(1).get(2)) > numRows ) {
            return false;
        }

        // check whether the last two lines have the correct number of symbols and the correct symbols
        if (maze.get(3).size() != numColumns && maze.size()-3 != numRows) {
            return false;
        }
        for (int i = 3; i < maze.size(); i++) {
            for (int j = 0; j < maze.get(i).size(); j++)
            if (!descriptor.contains(maze.get(i).get(j))) {
                return false;
            }
        }
        scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found.");
        }
        return true;
    } 
    
    /**
     * Returns true if number is greater than or equal to lower bound
     * and less than upper bound. 
     * @param number
     * @param lowerBound
     * @param upperBound
     * @return true if lowerBound ≤ number < upperBound
     */
    private static boolean isInRange(int number, int lowerBound, int upperBound) {
        return number < upperBound && number >= lowerBound;
    }
    
    /**
     * Prints the maze with the start and finish squares marked. Does
     * not include a solution.
     */
    public void print() {
        //We'll print off each row of squares in turn.
        for(int row = 0; row < numRows; row++) {
            
            //Print each of the lines of text in the row
            for(int charInRow = 0; charInRow < 4; charInRow++) {
                //Need to start with the initial left wall.
                if(charInRow == 0) {
                    System.out.print("+");
                } else {
                    System.out.print("|");
                }
                
                for(int col = 0; col < numColumns; col++) {
                    MazeSquare curSquare = this.getMazeSquare(row, col);
                    if(charInRow == 0) {
                        //We're in the first row of characters for this square - need to print
                        //top wall if necessary.
                        if(curSquare.hasTopWall()) {
                            System.out.print(getTopWallString());
                        } else {
                            System.out.print(getTopOpenString());
                        }
                    } else if(charInRow == 1 || charInRow == 3) {
                        //These are the interior of the square and are unaffected by
                        //the start/final state.
                        if(curSquare.hasRightWall()) {
                            System.out.print(getRightWallString());
                        } else {
                            System.out.print(getOpenWallString());
                        }    
                    } 
                    else {
                        //We must be in the second row of characters.
                        //This is the row where start/finish should be displayed if relevant

                        //Check if we're in the start or finish state
                     
                        if(startRow == row && startColumn == col) {
                            System.out.print("  S  ");
                        } else if(finishRow == row && finishColumn == col) {
                            System.out.print("  F  ");
                        } 
                        else {
                            System.out.print("     ");
                        }
                        if(curSquare.hasRightWall()) {
                            System.out.print("|");
                        } else {
                            System.out.print(" ");
                        }
                    }
                }
                //Now end the line to start the next
                System.out.print("\n");
            }           
        }
        
        //Finally, we have to print off the bottom of the maze, since that's not explicitly represented
        //by the squares. Printing off the bottom separately means we can think of each row as
        //consisting of four lines of text.
        printFullHorizontalRow(numColumns);
    }
    
    /**
     * Prints the very bottom row of characters for the bottom row of maze squares (which is always walls).
     * numColumns is the number of columns of bottom wall to print.
     */
    private static void printFullHorizontalRow(int numColumns) {
        System.out.print("+");
        for(int row = 0; row < numColumns; row++) {
            //We use getTopWallString() since bottom and top walls are the same.
            System.out.print(getTopWallString());
        }
        System.out.print("\n");
    }
    
    /**
     * Returns a String representing the bottom of a horizontal wall.
     */
    private static String getTopWallString() {
        return "-----+";
    }
    
    /**
     * Returns a String representing the bottom of a square without a
     * horizontal wall.
     */
    private static String getTopOpenString() {
        return "     +";
    }
    
    /**
     * Returns a String representing a left wall (for the interior of the row).
     */
    private static String getRightWallString() {
        return "     |";
    }
    
    /**
     * Returns a String representing no left wall (for the interior of the row).
     */
    private static String getOpenWallString() {
        return "      ";
    }
    
    /**
     * Implement me! This method should return the MazeSquare at the given 
     * row and column. The line "return null" is added only to make the
     * code compile before this method is implemented. Delete that line and
     * replace it with your own code.
     */
    public MazeSquare getMazeSquare(int row, int col) {
        //**************YOUR CODE HERE******************
        boolean top = true;
        boolean right = true;
        if ((this.maze.get(row+3).get(col)).equals("_")) {
            top = true;
            right = false;
        } 
        else if ((this.maze.get(row+3).get(col)).equals("7")) {
            top = true;
            right = true;
        }
        else if ((this.maze.get(row+3).get(col)).equals("*")) {
            top = false;
            right = false;
        }
        else if ((this.maze.get(row+3).get(col)).equals("|")) {
            top = false;
            right = true;
        }
        mazeSquare = new MazeSquare (row, col, top, right);
        return mazeSquare;
    }

    public boolean checkSquare(int row, int col) {
        boolean check = false;
        List<Integer> list = new ArrayList<Integer>();
        list.add(row);
        list.add(col);
        MazeSquare nextSquare = allSquares.get(list);
        if (nextSquare.whetherVisited() == false){
            check = true;
        }
        return check;
    }

    /**
    * Computes and returns a solution to this maze. If there are multiple
    * solutions, only one is returned, and getSolution() makes no guarantees about
    * which one. However, the returned solution will not include visits to dead
    * ends or any backtracks, even if backtracking occurs during the solution
    * process. 
    *
    * @return a stack of MazeSquare objects containing the sequence of squares
    * visited to go from the start square (bottom of the stack) to the finish
    * square (top of the stack). If there is no solution, an empty stack is
    * returned.
    */
    public Stack<MazeSquare> getSolution() {
        for (int i = 0; i < maze.size()-3; i++) {
            for (int j = 0; j < this.numColumns; j++) {
                MazeSquare eachSquare = getMazeSquare(i, j);
                eachSquare.setVisited(false);
                List<Integer> list = new ArrayList<Integer>();
                list.add(i);
                list.add(j);
                allSquares.put(list,eachSquare);
            }
        }

        //get starting square and add it to stack
        curRow = startRow;
        curColumn = startColumn;
        List<Integer> list = new ArrayList<Integer>();
        list.add(startRow);
        list.add(startColumn);
        MazeSquare curSquare = allSquares.get(list);
        curSquare.setVisited(true);
        stack.push(curSquare);

        boolean solve = false;
        while(solve == false) {
            //System.out.println("while");
            if (curRow == finishRow && curColumn == finishColumn) {
                solve = true;
                System.out.println("Solution: ");
            }
            //go up one spot
            else if (isInRange(curRow-1, 0, numRows) && checkSquare(curRow-1, curColumn) == true && !maze.get(3+curRow).get(curColumn).equals("7") && !maze.get(3+curRow).get(curColumn).equals("_")) {
                List<Integer> newList = new ArrayList<Integer>();
                newList.add(curRow-1);
                newList.add(curColumn);
                MazeSquare nextSquare = allSquares.get(newList);
                nextSquare.setVisited(true);
                stack.push(nextSquare);
                curRow = curRow-1;
                //System.out.println("go up");
            }
            //go down one spot
            else if (isInRange(curRow+1, 0, numRows) && checkSquare(curRow+1, curColumn) == true && !maze.get(3+curRow+1).get(curColumn).equals("7") && !maze.get(3+curRow+1).get(curColumn).equals("_")) {
                List<Integer> newList = new ArrayList<Integer>();
                newList.add(curRow+1);
                newList.add(curColumn);
                MazeSquare nextSquare = allSquares.get(newList);
                nextSquare.setVisited(true);
                stack.push(nextSquare);
                curRow = curRow+1;
                //System.out.println("go down");
            }
            //go left one spot
            else if (isInRange(curColumn-1, 0, numColumns)  && checkSquare(curRow, curColumn-1) == true && !maze.get(3+curRow).get(curColumn-1).equals("7") && !maze.get(3+curRow).get(curColumn-1).equals("|")) {
                List<Integer> newList = new ArrayList<Integer>();
                newList.add(curRow);
                newList.add(curColumn-1);
                MazeSquare nextSquare = allSquares.get(newList);
                nextSquare.setVisited(true);
                stack.push(nextSquare);
                curColumn = curColumn-1;
                //System.out.println("go left");
            }
            //go right one spot
            else if (isInRange(curColumn+1, 0, numColumns) && checkSquare(curRow, curColumn+1) == true && !maze.get(3+curRow).get(curColumn).equals("7") && !maze.get(3+curRow).get(curColumn).equals("|")) {
                List<Integer> newList = new ArrayList<Integer>();
                newList.add(curRow);
                newList.add(curColumn+1);
                MazeSquare nextSquare = allSquares.get(newList);
                nextSquare.setVisited(true);
                stack.push(nextSquare);
                curColumn = curColumn+1;
                //System.out.println("go right");
            }
            else {
                stack.pop();
                if (stack.size() == 0) {
                    System.out.println("Sorry, the maze is unsolvable.");
                    solve = true; //tho not really, just to exit while loop
                }
                else {
                    curRow = stack.peek().getRow();
                    curColumn = stack.peek().getColumn();
                    //System.out.println("pop");
                }
            }   
        } 
        //System.out.println("what?");
        return stack;
    }

    public void printSolution() {
        ArrayList<MazeSquare> array = stack.toArray();
        for(int row = 0; row < numRows; row++) {
            for(int charInRow = 0; charInRow < 4; charInRow++) {
                if(charInRow == 0) {
                    System.out.print("+");
                } else {
                    System.out.print("|");
                }
                
                for(int col = 0; col < numColumns; col++) {
                    MazeSquare curSquare = this.getMazeSquare(row, col);
                    if(charInRow == 0) {
                        if(curSquare.hasTopWall()) {
                            System.out.print(getTopWallString());
                        } else {
                            System.out.print(getTopOpenString());
                        }
                    } else if(charInRow == 1 || charInRow == 3) {
                        if(curSquare.hasRightWall()) {
                            System.out.print(getRightWallString());
                        } else {
                            System.out.print(getOpenWallString());
                        }    
                    } 
                    else {
                        if(startRow == row && startColumn == col) {
                            System.out.print("  S  ");
                        } 
                        else if(finishRow == row && finishColumn == col) {
                            System.out.print("  F  ");
                        } 
                  
                        else {
                            boolean n = false;
                            for (int i = 0; i < array.size(); i++) {
                              if (array.get(i).getRow() == curSquare.getRow() && array.get(i).getColumn() == curSquare.getColumn()) {
                                  System.out.print("  *  ");
                                  n = true; 
                                  break;  
                              } 
                            }
                            if (n != true) {
                               System.out.print("     ");  
                            }
                        }
                        if(curSquare.hasRightWall()) {
                            System.out.print("|");
                        } else {
                            System.out.print(" ");
                        }
                    }
                }
                System.out.print("\n");
            }           
        }
        printFullHorizontalRow(numColumns);
    }
    
 
    /**
     * You should modify main so that if there is only one
     * command line argument, it loads the maze and prints it
     * with no solution. If there are two command line arguments
     * and the second one is --solve,
     * it should load the maze, solve it, and print the maze
     * with the solution marked. No other command lines are valid.
     */ 
    public static void main(String[] args) { 
        Maze game = new Maze();
        String fileName = args[0];
        if (args.length == 1){
            //System.out.println(game.load(fileName));
            game.load(fileName);
            game.print();
        }

        else if (args.length == 2) {
            String solve = args[1];
            if (solve.equals("--solve")) {
                game.load(fileName);
                System.out.println("Original maze:");
                game.print();
                game.getSolution();
                game.printSolution();
            }

        }

    } 
}