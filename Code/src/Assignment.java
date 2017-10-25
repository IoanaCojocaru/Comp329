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
	private static OccupancyGridCounter gridCounter;
	
	private static NXTUltrasonicSensor uSensor;
	private static Brick myEV3;
	private static GraphicsLCD lcd;
	
	private static ServerSocket server;
	private static Socket client;
	private static DataOutputStream dOut;
	
	public static void main(String[] args) {
		// Get grid size for occupancy grid + count
		xGridCount = DIMENSION_X / ROBOT_LENGTH;
		yGridCount = DIMENSION_Y / ROBOT_WIDTH;
		
		gridManager = new OccupancyGridManager(xGridCount, yGridCount);
		gridCounter = new OccupancyGridCounter(xGridCount, yGridCount);
		
		// Initialise robot objects
		/*
		PilotRobot me = new PilotRobot();		
		PilotMonitor myMonitor = new PilotMonitor(me, 400);	

		// Set up the behaviours for the Arbitrator and construct it.
		Behavior b1 = new DriveForward(me);
		Behavior b2 = new BackUp(me);
		Behavior [] bArray = {b1, b2};
		Arbitrator arby = new Arbitrator(bArray);
		*/
		
		myEV3 = BrickFinder.getDefault();
		lcd = myEV3.getGraphicsLCD();

		uSensor = new NXTUltrasonicSensor(myEV3.getPort("S3"));
		
		try {
			server = new ServerSocket(1234);
			//System.out.println("Awaiting client..");
			client = server.accept();
			//System.out.println("CONNECTED");
			OutputStream out = client.getOutputStream();
			dOut = new DataOutputStream(out);
		} catch(Exception e) {
			
		}
		
		
		String row;
		int gridVal;
		
		// Check ultrasound
		while(true) {
			Button.waitForAnyPress();
			
			if(Button.ESCAPE.isDown())
				break;
		
			float sensorReading = checkSensor();
			
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
					gridCounter.updateGridValue(gridCell, 0);
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
				if(gridCounter.getGridValue(i, j) > 0)
					gridVal = gridManager.getGridValue(i, j) / gridCounter.getGridValue(i, j);
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
	
	private static float checkSensor() {
		SampleProvider distSP = uSensor.getDistanceMode();

		float[] distSample = new float[distSP.sampleSize()]; // Size is 1
		
		distSP.fetchSample(distSample, 0);

		return distSample[0];
	}
}
