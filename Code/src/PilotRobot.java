
import lejos.hardware.Brick;
import lejos.hardware.motor.Motor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.SampleProvider;

public class PilotRobot {
	private Brick myEV3;
	private MovePilot pilot;
	private SampleProvider leftBumperSampleProvider, rightBumperSampleProvider, ultrasonicSensorSampleProvider, colourSensorSampleProvider;
	private float[] leftBumperSample, rightBumperSample, ultrasonicSensorSample, colourSensorSample; 
	private NXTUltrasonicSensor ultrasonicSensor;
	private EV3TouchSensor leftBumper, rightBumper;
	private EV3ColorSensor colourSensor;
	
	// Constructor
	public PilotRobot(Brick robot) {
		this.myEV3 = robot;
		
		// Get sensors
		this.leftBumper = new EV3TouchSensor(myEV3.getPort("S2"));
		this.rightBumper = new EV3TouchSensor(myEV3.getPort("S1"));
		this.ultrasonicSensor = new NXTUltrasonicSensor(myEV3.getPort("S3"));
		this.colourSensor = new EV3ColorSensor(myEV3.getPort("S4"));

		// Initialise sensor sample providers
		leftBumperSampleProvider = leftBumper.getTouchMode();
		rightBumperSampleProvider = rightBumper.getTouchMode();
		colourSensorSampleProvider = colourSensor.getRGBMode();
		ultrasonicSensorSampleProvider = ultrasonicSensor.getDistanceMode();
		
		// Initialise sensor samples
		leftBumperSample = new float[leftBumperSampleProvider.sampleSize()];
		rightBumperSample = new float[rightBumperSampleProvider.sampleSize()];
		colourSensorSample = new float[colourSensorSampleProvider.sampleSize()];
		ultrasonicSensorSample = new float[ultrasonicSensorSampleProvider.sampleSize()];
		
		// Initialise wheels and create chassis
		Wheel leftWheel = WheeledChassis.modelWheel(Motor.B, 3.3).offset(-10.0);
		Wheel rightWheel = WheeledChassis.modelWheel(Motor.C, 3.3).offset(10.0);
		Chassis chassis = new WheeledChassis( new Wheel[]{leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);
		
		// Initialise move pilot with the chassis
		this.pilot = new MovePilot(chassis);
	}
	
	// Is the left bumper pressed?
	public boolean isLeftBumperPressed() {
    	leftBumperSampleProvider.fetchSample(leftBumperSample, 0);
    	return (leftBumperSample[0] == 1.0);
	}
	
	// Is the right bumper pressed?
	public boolean isRightBumperPressed() {
    	rightBumperSampleProvider.fetchSample(rightBumperSample, 0);
    	return (rightBumperSample[0] == 1.0);
	}
	
	// Get the colour from the colour sensor
	public float[] getColourSensor() {
    	colourSensorSampleProvider.fetchSample(colourSensorSample, 0);
    	return colourSensorSample;
	}
	
	// Get ultrasonic reading
	public float getUltrasonicSensor() {
		ultrasonicSensorSampleProvider.fetchSample(ultrasonicSensorSample, 0);
		return ultrasonicSensorSample[0];
	}
	
	// Get the move pilot
	public MovePilot getPilot() {
		return pilot;
	}
	
	// End the sensors
	public void endSensors() {
		leftBumper.close();
		rightBumper.close();
		colourSensor.close();
	}
}
