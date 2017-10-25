/*
 *  COMP329 Assignment 1
 *  Main Assignment class
 */

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.Battery;
import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Assignment {
	private static final int DIMENSION_X = 100, DIMENSION_Y = 100; // X and Y dimensions of the arena in cm
	private static final int ROBOT_LENGTH = 25, ROBOT_WIDTH = 20; // Length and width of the robot in cm
	private static int xGridCount, yGridCount;
	
	private static OccupancyGridManager gridManager;

	private static Brick myEV3;
	private static GraphicsLCD lcd;
	
	private static ServerSocket server;
	private static Socket client;
	private static DataOutputStream dOut;
	
	private static PilotRobot pilotRobot;
	private static PilotMonitor pilotMonitor;
	
	public static void main(String[] args) {
		// Get grid size for occupancy grid + grid counter
		xGridCount = DIMENSION_X / ROBOT_LENGTH;
		yGridCount = DIMENSION_Y / ROBOT_WIDTH;
		
		// Initialise grid manager
		gridManager = new OccupancyGridManager(xGridCount, yGridCount);
		
		// Initialise robot objects
		myEV3 = BrickFinder.getDefault();
		lcd = myEV3.getGraphicsLCD();
		pilotRobot = new PilotRobot(myEV3);
		pilotMonitor = new PilotMonitor(pilotRobot, 400);
		
		// Create behaviours and an arbitrator
		Behavior navigateObstacle = new BehaviourNavigateObstacle(pilotRobot);
		
		Behavior[] behaviours = {navigateObstacle};
		
		Arbitrator arbitrator = new Arbitrator(behaviours);

		try {
			server = new ServerSocket(1234);
			//System.out.println("Awaiting client..");
			client = server.accept();
			//System.out.println("CONNECTED");
			OutputStream out = client.getOutputStream();
			dOut = new DataOutputStream(out);
		} catch(Exception e) {
			
		}
		
		// Start arbitrator
		//arbitrator.go();
		
		
		String row;
		int gridVal;
		
		// Check ultrasound
		while(true) {
			Button.waitForAnyPress();
			
			if(Button.ESCAPE.isDown())
				break;
		
			float sensorReading = pilotRobot.getUltrasonicSensor();
			
			// Convert sensor reading to int
			if(sensorReading < Float.POSITIVE_INFINITY) {
				int sensorReadingInt = (int) (sensorReading * 100);
				int gridCell = sensorReadingInt / ROBOT_LENGTH;
				//gridCell--;
				
				try {
					dOut.writeUTF("Detected obstacle at distance " + sensorReadingInt + "cm in cell (" + gridCell + ", 0)");
					dOut.flush();
				} catch(Exception e) {
					
				}
				
				if(gridCell >= 0 && gridCell < xGridCount) {
					gridManager.updateGridValue(gridCell, 0, 1);
				}
				
				lcd.clear();
				for(int i = 0; i < xGridCount; i++) {
					row = "";
					
					for(int j = 0; j < yGridCount; j++) {
						gridVal = gridManager.getGridValue(i, j);
						row += " " + gridVal;
					}
					
					lcd.drawString(row, 0, ((yGridCount - i) * 10), 0);
				}
			}
		}

		lcd.clear();
		for(int i = 0; i < xGridCount; i++) {
			row = "";
			
			for(int j = 0; j < yGridCount; j++) {
				if(gridManager.getGridCounterValue(i, j) > 0)
					gridVal = gridManager.getGridValue(i, j) / gridManager.getGridCounterValue(i, j);
				else
					gridVal = 0;
				
				row += " " + gridVal;
			}
			
			lcd.drawString(row, 0, ((yGridCount - i) * 10), 0);
		}
		
		Button.waitForAnyPress();
		
		try {
			dOut.writeUTF("Finished");
			server.close();
		} catch(Exception e) {
			
		}
		
	}
	
	// Does the next grid cell contain an obstacle?
	public static boolean nextGridContainsObstacle() {
		// Get current grid value
		return false;
	}
	
	// Have we completed the occupancy grid?
	public static boolean isMapFinished() {
		for(int i = 0; i < xGridCount; i++) {
			for(int j = 0; j < yGridCount; j++) {
				if(gridManager.getGridCounterValue(i, j) == 0)
					return false;
			}
		}
		
		return true;
	}
}
