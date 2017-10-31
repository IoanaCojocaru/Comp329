import lejos.hardware.Button;

public class MapDrawerTester {

	public static void main(String[] args) {
		MapDrawer map = new MapDrawer();
		
		int test[][] = new int[5][6];
		
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 6; j++) {
				test[i][j] = 0;
			}	
		}
		test[0][0] = 1;
		test[0][3] = 2;
		test[0][5] = 2;
		map.initMap(test);
		Button.waitForAnyPress();
		map.printMap(test);
		Button.waitForAnyPress();
	}

}
