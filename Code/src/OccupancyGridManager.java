/*
 *  COMP329 Assignment 1
 *  Occupancy grid manager
 *  Creates occupancy grid and updates grid tile values
 */
public class OccupancyGridManager {
	private int[][] grid;
	private OccupancyGridCounter counter;
	
	// Constructor
	public OccupancyGridManager(int xGridCount, int yGridCount) {
		// Initialise grid
		grid = new int[xGridCount][yGridCount];
		counter = new OccupancyGridCounter(xGridCount, yGridCount);
	}
	
	// Update a specified grid tile value
	public void updateGridValue(int x, int y, int value) {
		if(x >= 0 && grid.length > x) {
			if(y >= 0 && grid[x].length > y) {
				for(int i = 0; i < x; i++) {
					counter.updateGridValue(i, y);
				}
				grid[x][y] += value;
				counter.updateGridValue(x, y);
			}
		}
	}
	
	// Get a specified grid tile value
	public int getGridValue(int x, int y) {
		if(x >= 0 && grid.length > x)
			if(y >= 0 && grid[x].length > y)
				return grid[x][y];
		
		return -1;
	}
	
	// Get a specified grid tile counter value
	public int getGridCounterValue(int x, int y) {
		if(x >= 0 && grid.length > x)
			if(y >= 0 && grid[x].length > y)
				return counter.getGridValue(x, y);
		
		return -1;
	}
}
