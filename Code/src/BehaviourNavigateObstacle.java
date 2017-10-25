/*
 *  COMP329 Assignment 1
 *  Obstacle navigation behaviour
 *  Activates is robot encounters obstacle
 */

import lejos.robotics.subsumption.Behavior;

public class BehaviourNavigateObstacle implements Behavior {
	public boolean suppressed;
	private PilotRobot pilotRobot;

	// Constructor
	public BehaviourNavigateObstacle(PilotRobot robot){
    	 this.pilotRobot = robot;
    }

	// Finish the action
	public void suppress(){
		suppressed = true;
	}

	// Check if the next grid tile has an obstacle in
	public boolean takeControl(){
		if(Assignment.nextGridContainsObstacle())
			return true;
		else
			return false;
	}

	// Perform action to navigate obstacle
	public void action() {
		// Allow this method to run
		//suppressed = false;
		
		
	}
}
