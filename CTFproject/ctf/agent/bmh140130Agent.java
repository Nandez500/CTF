package ctf.agent;

//numDeaths
//Node(coordinate,badboolean,1 parent,3 children)
//queue = path to where you died
//list = of nodes we determine to be bad 

import ctf.common.*;

public class bmh140130Agent extends Agent {
	
	static boolean immediate = true;
	static boolean ranged = false;

	// implements Agent.getMove() interface
	public int getMove( AgentEnvironment inEnvironment )
	{
			// booleans describing direction of goal
			// goal is either enemy flag, or our base
			boolean [] goalFlags = getGoalFlags(inEnvironment);
			boolean goalNorth = goalFlags[0];
			boolean goalSouth = goalFlags[1];
			boolean goalEast = goalFlags[2];
			boolean goalWest = goalFlags[3];

			
			
			
			// now we have direction booleans for our goal	
			
			// check for immediate obstacles blocking our path		
			boolean obstNorth = inEnvironment.isObstacleNorthImmediate()
				|| inEnvironment.isAgentNorth(inEnvironment.OUR_TEAM, immediate);	
			boolean obstSouth = inEnvironment.isObstacleSouth
			Immediate()
				|| inEnvironment.isAgentSouth(inEnvironment.OUR_TEAM, immediate);
			boolean obstEast = inEnvironment.isObstacleEastImmediate()
				|| inEnvironment.isAgentEast(inEnvironment.OUR_TEAM, immediate);
			boolean obstWest = inEnvironment.isObstacleWestImmediate()
				|| inEnvironment.isAgentWest(inEnvironment.OUR_TEAM, immediate);
				
 
			//EDITEDITEDITEDIT
			//Set it up to avoid teammates.



			// if the goal is north only, and we're not blocked
			if( goalNorth && ! goalEast && ! goalWest && !obstNorth ) {
				// move north
				return AgentAction.MOVE_NORTH;
				}
				
			// if goal both north and east
			if( goalNorth && goalEast ) {
				// pick north or east for move with 50/50 chance
				if( Math.random() < 0.5 && !obstNorth ) {
					return AgentAction.MOVE_NORTH;
					}
				if( !obstEast ) {	
					return AgentAction.MOVE_EAST;
					}
				if( !obstNorth ) {	
					return AgentAction.MOVE_NORTH;
					}
				}	
				
			// if goal both north and west	
			if( goalNorth && goalWest ) {
				// pick north or west for move with 50/50 chance
				if( Math.random() < 0.5 && !obstNorth ) {
					return AgentAction.MOVE_NORTH;
					}
				if( !obstWest ) {	
					return AgentAction.MOVE_WEST;
					}
				if( !obstNorth ) {	
					return AgentAction.MOVE_NORTH;
					}	
				}
			
			// if the goal is south only, and we're not blocked
			if( goalSouth && ! goalEast && ! goalWest && !obstSouth ) {
				// move south
				return AgentAction.MOVE_SOUTH;
				}
			
			// do same for southeast and southwest as for north versions	
			if( goalSouth && goalEast ) {
				if( Math.random() < 0.5 && !obstSouth ) {
					return AgentAction.MOVE_SOUTH;
					}
				if( !obstEast ) {
					return AgentAction.MOVE_EAST;
					}
				if( !obstSouth ) {
					return AgentAction.MOVE_SOUTH;
					}
				}
					
			if( goalSouth && goalWest && !obstSouth ) {
				if( Math.random() < 0.5 ) {
					return AgentAction.MOVE_SOUTH;
					}
				if( !obstWest ) {
					return AgentAction.MOVE_WEST;
					}
				if( !obstSouth ) {
					return AgentAction.MOVE_SOUTH;
					}
				}
			
			// if the goal is east only, and we're not blocked
			if( goalEast && !obstEast ) {
				return AgentAction.MOVE_EAST;
				}
			
			// if the goal is west only, and we're not blocked	
			if( goalWest && !obstWest ) {
				return AgentAction.MOVE_WEST;
				}	
			
			// otherwise, make any unblocked move
			if( !obstNorth ) {
				return AgentAction.MOVE_NORTH;
				}
			else if( !obstSouth ) {
				return AgentAction.MOVE_SOUTH;
				}
			else if( !obstEast ) {
				return AgentAction.MOVE_EAST;
				}
			else if( !obstWest ) {
				return AgentAction.MOVE_WEST;
				}	
			else {
				// completely blocked!
				return AgentAction.DO_NOTHING;
				}	
	}

	public boolean[] getGoalFlags (AgentEnvironment inEnvironment)
	{
		boolean [] goalFlags = {false,false,false,false};
		if( !inEnvironment.hasFlag() ) {
				// make goal the enemy flag
				goalFlags[0] = inEnvironment.isFlagNorth( 
					inEnvironment.ENEMY_TEAM, ranged );
			
				goalFlags[1] = inEnvironment.isFlagSouth( 
					inEnvironment.ENEMY_TEAM, ranged );
			
				goalFlags[2] = inEnvironment.isFlagEast( 
					inEnvironment.ENEMY_TEAM, ranged );
			
				goalFlags[3] = inEnvironment.isFlagWest( 
					inEnvironment.ENEMY_TEAM, ranged );
				}
			else {
				// we have enemy flag.
				// make goal our base
				goalFlags[0] = inEnvironment.isBaseNorth( 
					inEnvironment.OUR_TEAM, ranged );
			
				goalFlags[1] = inEnvironment.isBaseSouth( 
					inEnvironment.OUR_TEAM, ranged );
			
				goalFlags[2] = inEnvironment.isBaseEast( 
					inEnvironment.OUR_TEAM, ranged );
			
				goalFlags[3] = inEnvironment.isBaseWest( 
					inEnvironment.OUR_TEAM, ranged );
				}
		return goalFlags;
	}
}
