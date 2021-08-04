/**
* MazeSquare represents a single square within a Maze.
* @author Anna Rafferty
*/ 
public class MazeSquare {
    //Wall variables
    private boolean hasTopWall;
    private boolean hasRightWall;
		
    //Location of this square in a larger maze.
    private int row;
    private int col;
    private boolean visited;
		
    // constructor
    public MazeSquare(int r, int c, boolean top, boolean right) {
        this.row = r;
        this.col = c;
        this.hasTopWall = top;
        this.hasRightWall = right;
    }
		
    public void setVisited(boolean v) {
        this.visited = v;
    }
    public boolean whetherVisited() {
        return this.visited;
    }
    /**
     * Returns true if this square has a top wall.
     */
    public boolean hasTopWall() {
        return hasTopWall;
    }
		
    /**
     * Returns true if this square has a right wall.
     */
    public boolean hasRightWall() {
        return hasRightWall;
    }
		
    /**
     * Returns the row this square is in.
     */
    public int getRow() {
        return row;
    }
		
    /**
     * Returns the column this square is in.
     */
    public int getColumn() {
        return col;
    }
    
    
} 