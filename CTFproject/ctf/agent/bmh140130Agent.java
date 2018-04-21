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
	boolean startedAtNorth = false;
	Node firstParent = null;
	Node firstNode = new Node(0,0,firstParent,'X');
	Node currentNode = firstNode;
	private ArrayList<Node> badNodes = new ArrayList<Node>();
	private ArrayList<Node> visitedNodes = new ArrayList<Node>();
	private Stack <Node> lStack = new Stack <Node> ();


	// implements Agent.getMove() interface
	public int getMove( AgentEnvironment inEnvironment )
	{	

		// booleans describing direction of goal
		// goal is either enemy flag, or our base


		boolean obstNorth = inEnvironment.isObstacleNorthImmediate();
		boolean obstSouth = inEnvironment.isObstacleSouthImmediate();
		//set up first state (if we are pointing to first node)
		if(currentNode == firstNode)
		{
			if(obstNorth)
			{
				startedAtNorth = true;
			}

			else if(obstSouth)
			{
				startedAtNorth = false;
			}
		}

		
		resetIfAtSpawn(inEnvironment);

		//EXPAND NODE, based on if there is obstacle and if location not on bad locations 
		if(!currentNode.expanded)
		{	
			currentNode.children = expandCurrentNode(inEnvironment);
		}
		
		//PRUNE BAD LOCATION NODES FROM EXISTING CHILDREN
		else
		{
			for(int i = 0 ; i < currentNode.children.size(); i++)
			{
				for(Node badNode: badNodes)
					if(currentNode.children.get(i).locationY == badNode.locationY 
						&& currentNode.children.get(i).locationX == badNode.locationX)
					{
						currentNode.children.remove(i);
						i--;
						break;
					}
			}
		}

		

		//BACKTRACK IF NO CHILDREN, ADD TO LIST OF BAD NODES
		if(currentNode.children.size() == 0)
		{
			badNodes.add(currentNode);
			return backtrack(inEnvironment);	
		}

		System.out.println(currentNode.locationX +"," + currentNode.locationY);
		System.out.println(currentNode.direction);
		return advance(inEnvironment, currentNode.children);

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

			//WE CAPTURE ENEMY DUDE IF HE HAS FLAG AND WE DO TOO.
			else if(!inEnvironment.hasFlag()
				&& inEnvironment.hasFlag(inEnvironment.OUR_TEAM) 
				&& inEnvironment.hasFlag(inEnvironment.ENEMY_TEAM))
			{
				goalFlags[0] = inEnvironment.isFlagNorth( 
					inEnvironment.OUR_TEAM, ranged );
			
				goalFlags[1] = inEnvironment.isFlagSouth( 
					inEnvironment.OUR_TEAM, ranged );
			
				goalFlags[2] = inEnvironment.isFlagEast( 
					inEnvironment.OUR_TEAM, ranged );
			
				goalFlags[3] = inEnvironment.isFlagWest( 
					inEnvironment.OUR_TEAM, ranged );
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

	//SET CURRENTNODE TO FIRST NODE IF AGENT IS BACK AT SPAWN
	public void resetIfAtSpawn(AgentEnvironment inEnvironment)
	{
		//WHICH SIDE OF BASE DO WE SPAWN
		boolean obstNorth = inEnvironment.isObstacleNorthImmediate();
		boolean obstSouth = inEnvironment.isObstacleSouthImmediate();

		//ARE WE IN HOME COLUMN
		boolean inHomeColumn = !inEnvironment.isBaseEast(inEnvironment.OUR_TEAM,ranged) 	
			&& !inEnvironment.isBaseWest(inEnvironment.OUR_TEAM,ranged)
			&& (inEnvironment.isBaseNorth(inEnvironment.OUR_TEAM,ranged)
			|| inEnvironment.isBaseSouth(inEnvironment.OUR_TEAM,ranged));


		//ARE WE WHERE WE SPAWNED ORIGINALLY. RESET TO FIRSTNODE IF SO.
		boolean topHomeCorner = obstNorth && inHomeColumn;
		boolean bottomHomeCorner = obstSouth && inHomeColumn;
		if(topHomeCorner && startedAtNorth)
		{
			currentNode = firstNode;
		}
		else if(bottomHomeCorner && !startedAtNorth)
		{
			currentNode = firstNode;
		}

	}

	public int backtrack(AgentEnvironment inEnvironment)
	{		
			//IF WE KEEP BACKTRACKING AT THIS LOCATION MIGHT BE BAD.
			currentNode.parent.repetitions++;	

			//PAST THREE WE SHOULD STOP ADVANCING FROM THE PARENT NODE
			if(currentNode.parent.repetitions >= 3)
			{
				for(Node child: currentNode.parent.children)
				{
					badNodes.add(child);
				}
			}
			//backtrack
			if(currentNode.direction == 'N' && !inEnvironment.isAgentSouth(inEnvironment.OUR_TEAM, immediate))
			{
				currentNode = currentNode.parent;
				return AgentAction.MOVE_SOUTH;
			}
			else if(currentNode.direction == 'S' && !inEnvironment.isAgentNorth(inEnvironment.OUR_TEAM, immediate))
			{
				currentNode = currentNode.parent;
				return AgentAction.MOVE_NORTH;
			}
			else if(currentNode.direction == 'E' && !inEnvironment.isAgentWest(inEnvironment.OUR_TEAM, immediate))
			{
				currentNode = currentNode.parent;
				return AgentAction.MOVE_WEST;
			}
			else if(currentNode.direction == 'W' && !inEnvironment.isAgentEast(inEnvironment.OUR_TEAM, immediate))
			{
				currentNode = currentNode.parent;
				return AgentAction.MOVE_EAST;
			}		
			else
				return AgentAction.DO_NOTHING;
	}

	public int advance(AgentEnvironment inEnvironment, ArrayList <Node> children)
	{
		Node nextNode = selectNode(inEnvironment, children);

		//SELECT GOOD LOCATION, BASED ON NOT BLOCKED BY AGENT and HEURISTIC.
		if(nextNode != null)
		{
			currentNode = nextNode;
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
			else
				return AgentAction.MOVE_WEST;
		}

		//EVERYTHING BLOCKED
		else 
			return backtrack(inEnvironment);
		
	}
	//RETURNS CHILDREN TO BE APPENDED TO TREE
	public ArrayList<Node> expandCurrentNode (AgentEnvironment inEnvironment)
	{
		currentNode.expanded = true;
		boolean badChild = false;
		boolean obstNorth = inEnvironment.isObstacleNorthImmediate();
		boolean obstSouth = inEnvironment.isObstacleSouthImmediate();
		boolean obstEast = inEnvironment.isObstacleEastImmediate();
		boolean obstWest = inEnvironment.isObstacleWestImmediate();

		ArrayList<Node> children = new ArrayList<Node>();
		if(currentNode.direction!='S' && !obstNorth)
		{
			for(Node badNode: badNodes)
			{
				if(badNode.locationX == currentNode.locationX 
						&& badNode.locationY == currentNode.locationY + 1)
				{
					badChild = true;
					break;
				}
			}

			if(!badChild)
				children.add(new Node(currentNode.locationX,
					currentNode.locationY + 1, currentNode, 'N'));
		}

		if(currentNode.direction!='N' && !obstSouth)
		{
			for(Node badNode: badNodes)
			{
				if(badNode.locationX == currentNode.locationX 
						&& badNode.locationY == currentNode.locationY - 1)
				{
					badChild = true;
					break;
	`			}
			}

			if(!badChild)
				children.add(new Node(currentNode.locationX,
					currentNode.locationY - 1, currentNode, 'S'));
		}
		if(currentNode.direction!='W' && !obstEast)
		{
			for(Node badNode: badNodes)
			{
				if(badNode.locationX == currentNode.locationX + 1
						&& badNode.locationY == currentNode.locationY)
				{
					badChild = true;
					break;
				}
			}

			if(!badChild)
				children.add(new Node(currentNode.locationX + 1,
					currentNode.locationY, currentNode, 'E'));
		}
		if(currentNode.direction!='E' && !obstWest)
		{
			for(Node badNode: badNodes)
			{
				if(badNode.locationX == currentNode.locationX - 1
						&& badNode.locationY == currentNode.locationY)
				{
					badChild = true;
					break;
				}
			}

			if(!badChild)
				children.add(new Node(currentNode.locationX - 1,
					currentNode.locationY, currentNode, 'W'));
		}
		return children;
	}

	public Node selectNode(AgentEnvironment inEnvironment, ArrayList<Node> children)
	{


		boolean northBlocked = inEnvironment.isAgentNorth(inEnvironment.OUR_TEAM,immediate);
		boolean southBlocked = inEnvironment.isAgentSouth(inEnvironment.OUR_TEAM,immediate);
		boolean eastBlocked = inEnvironment.isAgentEast(inEnvironment.OUR_TEAM,immediate);
		boolean westBlocked = inEnvironment.isAgentWest(inEnvironment.OUR_TEAM,immediate);

		if(inEnvironment.hasFlag())
		{
			northBlocked = northBlocked || inEnvironment.isAgentNorth(inEnvironment.ENEMY_TEAM,immediate);
			southBlocked = southBlocked || inEnvironment.isAgentSouth(inEnvironment.ENEMY_TEAM,immediate);
			eastBlocked = eastBlocked  || inEnvironment.isAgentEast(inEnvironment.ENEMY_TEAM,immediate);
			westBlocked = westBlocked || inEnvironment.isAgentWest(inEnvironment.ENEMY_TEAM,immediate);
		}

		boolean [] goalFlags = getGoalFlags(inEnvironment);
		boolean goalNorth = goalFlags[0];
		boolean goalSouth = goalFlags[1];
		boolean goalEast = goalFlags[2];
		boolean goalWest = goalFlags[3];
		ArrayList<Node> freeChildren = new ArrayList<Node>();
		ArrayList<Node> selectedNodes = new ArrayList<Node>();

		//prune blocked moves
		for(int i = 0; i < children.size(); i++ )
		{
			if(children.get(i).direction == 'N' && !northBlocked)
			{
				freeChildren.add(children.get(i));
			} 
			else if (children.get(i).direction == 'S' && !southBlocked) 
			{
				freeChildren.add(children.get(i));
			}
			else if (children.get(i).direction == 'E' && !eastBlocked)
			{
				freeChildren.add(children.get(i));
			}
			else if (children.get(i).direction == 'W' && !westBlocked)
			{
				freeChildren.add(children.get(i));
			}
		}	

		//IF NO CHILDREN REMAIN AFTER PRUNING, WE WAIT FOR AGENT TO PASS
		if(freeChildren.size() == 0)
		{
			return null;
		}

		//heuristic
		for(Node child : freeChildren)
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
			return freeChildren.get(randomIndex);
		else if(selectedNodes.size() == 1)
			return selectedNodes.get(0);
		else
			return selectedNodes.get(randomIndex);
	}

	class Node
	{
		int locationX = 0;
		int locationY = 0;
		Node parent;
		char direction = 'X';
		ArrayList<Node> children = new ArrayList<Node>();
		boolean expanded = false;
		int repetitions = 0;

		public Node(int x, int y, Node p, char d)
		{
			locationX = x;
			locationY = y;
			parent = p;
			direction = d;
		} 
	}

}
