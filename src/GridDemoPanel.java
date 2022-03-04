import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GridDemoPanel extends JPanel implements MouseListener
{
	private Cell[][] theGrid;
	public final static int NUM_ROWS = 12;
	public final static int NUM_COLS = 12;
	public GridDemoFrame myParent;
	public int score;
	
	public GridDemoPanel(GridDemoFrame parent)
	{
		super();
		resetCells();
		setBackground(Color.BLACK);
		addMouseListener(this);
		myParent = parent;
	}
	
	/**
	 * makes a new board with random colors, completely filled in.
	 */
	public void resetCells()
	{
		theGrid = new Cell[NUM_ROWS][NUM_COLS];
		for (int r =0; r<NUM_ROWS; r++)
			for (int c=0; c<NUM_COLS; c++)
				theGrid[r][c] = new Cell(r,c);
		score = 0;
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		for (int r =0; r<NUM_ROWS; r++)
			for (int c=0; c<NUM_COLS; c++)
				theGrid[r][c].drawSelf(g);
	}
	
	/**
	 * goes through the board and finds any missing chips. Pushes down any live chips into the empty spots. Then pushes any live 
	 * columns left into spaces with any empty columns.
	 * Performed after every click, in case the board has changed.
	 */
	public void clearChips()
	{
		// Find empty spots and shuffle colors down into them.
		ArrayList<Integer> numLiveCellsInCols = new ArrayList<Integer>();
		for (int col = 0; col < NUM_COLS; col++) // working left to right.
		{
			int numLiveCells = 0;
			for (int row = NUM_COLS-1; row >0; row--) // working from the bottom up.
			{
				if (theGrid[row][col].isLive())
					numLiveCells++; 
				else // if we've encountered a dead cell...
				{
					boolean foundALiveOne = false;
					for (int r = row-1; r>=0; r--) // search upwards from here.
					{
						if (theGrid[r][col].isLive()) // we found a live one above the empty one. Move this down to "row"
						{
							foundALiveOne = true;
							theGrid[row][col].setColorID(theGrid[r][col].getColorID());
							theGrid[row][col].setIsLive(true);
							theGrid[r][col].setIsLive(false);
							numLiveCells++;
							break;
						}
					}
					if (!foundALiveOne) // no point continuing to look above "row."
						break;
				}
				
			}
			numLiveCellsInCols.add(numLiveCells); 							
		}
		// Fill in empty columns
		for (int col = 0; col < NUM_COLS; col++)
		{
			if (0==numLiveCellsInCols.get(col))
			{
				for (int c = col+1; c<NUM_COLS; c++)
				{
					if (numLiveCellsInCols.get(c) > 0)
					{
						for (int row =0 ; row < NUM_ROWS; row++)
						{
							theGrid[row][col].setColorID(theGrid[row][c].getColorID());
							theGrid[row][col].setIsLive(theGrid[row][c].isLive());
							theGrid[row][c].setIsLive(false);
						}
						numLiveCellsInCols.set(col, numLiveCellsInCols.get(c));
						numLiveCellsInCols.set(c, 0);
						
						break;
					}
				}	
			}
		}
	
	}
	
	
	/**
	 * the mouse listener has detected a click, and it has happened on the cell in theGrid at row, col
	 * @param row
	 * @param col
	 */
	public void userClickedCell(int row, int col)
	{
		if (!theGrid[row][col].isLive())
			return;
		int clickedColorID = theGrid[row][col].getColorID();
		System.out.println("You just clicked cell at row "+row+", col "+col+",which has color #"+clickedColorID); // temporary, so you can see what it is doing.
		boolean foundMatch = (row>0 && theGrid[row-1][col].getColorID() == clickedColorID) ||
							 (col>0 && theGrid[row][col-1].getColorID() == clickedColorID) ||
							 (row<NUM_ROWS-1 && theGrid[row+1][col].getColorID() == clickedColorID)||
							 (col<NUM_COLS-1 && theGrid[row][col+1].getColorID() == clickedColorID);
		if (foundMatch)
		{
			System.out.println("Found a match for color id: "+clickedColorID); // temporary, so you can see what it is doing.
			int count = clearCellAndNeighborsOfColor(row, col, clickedColorID);
			// TODO: Do something meaningful with "count."
			score++;
			myParent.updateScore(score); // this is how we change the score at the bottom of the window.
		}
		// shuffle the chips to fill the empty spots and redraw the screen.
		clearChips();
		repaint();
		if (gameIsOver())
			handleGameOverDialog();
	}
	
	/**
	 * A recursive method that sets the "isLive" variable of the cell in theGrid at row/col to false, and checks
	 *  for any neighbors that are currently alive and have the same color and tells them to do the same.
	 *  Returns a count of how many cells are "killed."
	 * @param row
	 * @param col
	 * @param colorID
	 * @return how many cells are removed by this method and it's children.
	 */
	public int clearCellAndNeighborsOfColor(int row, int col, int colorID)
	{
		// -----------------------------
		// TODO: fill in this method!
		// Hint: consider your base cases -- under what circumstances would a
		// combination of (row, col, colorID) lead to not wanting to continue
		// recursing?

		// suggestion: when checking your neighbors, call the recursion on all
		//  four (N, S, E, W) regardless of their state, and let their base
		//  cases sort out whether to return quickly.
		
		// ------------------------------
		return -1; // TODO: you'll probably want to change this....
	}
	
	/**
	 * determines whether the grid has any remaining live cells with color that matches a live neighbor.
	 * @return whether such a match exists on the board.
	 */
	public boolean gameIsOver()
	{
		for (int row = 0; row<NUM_ROWS; row++)
			for (int col = 1; col<NUM_COLS; col++)
				if (theGrid[row][col-1].isLive() && theGrid[row][col].isLive() && theGrid[row][col-1].getColorID() == theGrid[row][col].getColorID())
					return false;
		for (int row = 1; row<NUM_ROWS; row++)
			for (int col = 0; col<NUM_COLS; col++)
				if (theGrid[row-1][col].isLive() && theGrid[row][col].isLive() && theGrid[row-1][col].getColorID() == theGrid[row][col].getColorID())
					return false;
					
		return true;
	}
	/**
	 * presuming the game is over, displays a message that the game has ended. When the player responds, resets the board.
	 */
	public void handleGameOverDialog()
	{
		JOptionPane.showMessageDialog(this, "Game Over.");
		
		resetCells();
		repaint();
	}
	
	//============================ Mouse Listener Overrides ==========================

	@Override
	// mouse was just released within about 1 pixel of where it was pressed.
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub
		// mouse location is at e.getX() , e.getY().
		// if you wish to convert to the rows and columns, you can integer-divide by the cell size.

		
		
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// TODO Auto-generated method stub
		// mouse location is at e.getX() , e.getY().
		// if you wish to convert to the rows and columns, you can integer-divide by the cell size.
				
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub
		// mouse location is at e.getX() , e.getY().
		// if you wish to convert to the rows and columns, you can integer-divide by the cell size.
		int col = e.getX()/Cell.CELL_SIZE;
		int row = e.getY()/Cell.CELL_SIZE;
		userClickedCell(row,col);
	}

	@Override
	// mouse just entered this window
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	// mouse just left this window
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	
	
}
