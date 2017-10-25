
public class OccupancyGridManager {
	private int[][] grid;
	
	public OccupancyGridManager(int xGridCount, int yGridCount) {
		// Initialise grid
		grid = new int[xGridCount][yGridCount];
	}
	
	public void updateGridValue(int x, int y, int value) {
		if(grid.length > x)
			if(grid[x].length > y)
				grid[x][y] += value;
	}
	
	public int getGridValue(int x, int y) {
		return grid[x][y];
	}
}
