
public class OccupancyGridCounter {
	private int[][] grid;
	
	public OccupancyGridCounter(int xGridCount, int yGridCount) {
		// Initialise grid
		grid = new int[xGridCount][yGridCount];
	}
	
	public void updateGridValue(int x, int y) {
		grid[x][y]++;
	}
	
	public int getGridValue(int x, int y) {
		return grid[x][y];
	}
}
