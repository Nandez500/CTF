package ctf.agent;

//numDeaths
//Node(coordinate,badboolean,1 parent,3 children)
//queue = path to where you died
//list = of nodes we determine to be bad 
import java.util.ArrayList;
import java.util.Stack;
import java.util.Queue;
import ctf.common.*;

public class bmh140130Agent extends Agent {
	static boolean immediate = true;
	static boolean ranged = false;
	private ArrayList<Node> badNodes;
	Node firstNode = Node({0,0},NULL,'X');
	Node currentNode = firstNode;
	private Stack <Node> lStack = new Stack <Node> (initialNode);



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
		boolean obstNorth = inEnvironment.isObstacleNorthImmediate();
		boolean obstSouth = inEnvironment.isObstacleSouthImmediate();
		boolean obstEast = inEnvironment.isObstacleEastImmediate();
		boolean obstWest = inEnvironment.isObstacleWestImmediate();
		
		

 
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

		//add candidate nodes to the tree, based on if there is obstacle and if location not on bad locations 
		ArrayList<Node> children = new ArrayList<Node>();


		// if nothing to add, and we didn't get the flag in this path, avoid (we don't have flag yet) and add to bad paths
		//Backtrack



		//set currentnode to selected node
		currentNode = selectNode(inEnvironment);

		//move agent
		if(currentNode.direction == 'N')
		{
			return AgentAction.MOVE_NORTH;
		}
		if(currentNode.direction == 'S')
		{
			return AgentAction.MOVE_SOUTH;
		}
		if(currentNode.direction == 'E')
		{
			return AgentAction.MOVE_EAST;
		}
		if(currentNode.direction == 'W')
		{
			return AgentAction.MOVE_WEST;
		}
	}


	public int getMoveBeforeFlag(AgentEnvironment inEnvironment)
	{

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

	public Node selectNode(AgentEnvironment inEnvironment, ArrayList<Node> children)
	{
		boolean northBlocked = obstNorth || isAgentNorth(inEnvironment.OUR_TEAM,immediate);
		boolean southBlocked = obstSouth || isAgentSouth(inEnvironment.OUR_TEAM,immediate);
		boolean eastBlocked = obstEast || isAgentEast(inEnvironment.OUR_TEAM,immediate);
		boolean westBlocked = obstWest || isAgentWest(inEnvironment.OUR_TEAM,immediate);
		ArrayList<Node> selectedNodes = new ArrayList<Nodes>();
		//prune blocked moves
		for(int i; i < children.size(); i++ )
		{
			if(children[i].direction == 'N' && northBlocked)
			{
				children.remove(i);
				i--;
			} 
			else if (children[i].direction == 'S' && southBlocked) 
			{
				children.remove(i);
				i--;
			}
			else if (children[i].direction == 'E' && eastBlocked)
			{
				children.remove(i);
				i--;
			}
			else if (children[i].direction == 'W' && westBlocked)
			{
				children.remove(i);
				i--;
			}
		}	

		//heuristic
		for(Node child : children)
		{
			if(child.direction == 'N' && goalNorth)
				selectedNodes.add(child);
			else if(child.direction == 'S' && goalSouth)
				selectedNodes.add(child);
			else if(child.direction == 'E' && goalEast)
				selectedNodes.add(child);
			else if(child.direction == 'W' && goalWest)
				selectedNodes.add(child);
		}

		//final choice
		int randomIndex = (int)(Math.random()*selectedNodes.size());
		if(selectedNodes.size() == 0)
			return children.get(randomIndex);
		else if(selectedNodes.size() == 1)
			return selectedNodes.get(0);
		else
			return selectedNodes.get(randomIndex);
	}

	class Node
	{
		int [] location;
		Node parent;
		char direction;
		ArrayList<Node> children;
		boolean expanded;

		public Node(int [] l, Node p, char d)
		{
			location = l;
			parent = p;
			direction = d;
		} 
	}
}
