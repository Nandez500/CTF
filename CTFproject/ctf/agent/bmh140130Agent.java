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
	int mapLength = 1000;
	Node firstParent = null;
	Node firstNode = new Node(0,0,firstParent,'X');
	double count = 0;
	Node currentNode = firstNode;
	private ArrayList<Node> stuckNodes = new ArrayList<Node>();
	private ArrayList<Node> badNodes = new ArrayList<Node>();
	private ArrayList<Node> uniqueNodes = new ArrayList<Node>();
	private Stack <Node> lStack = new Stack <Node> ();



	// implements Agent.getMove() interface
	public int getMove( AgentEnvironment inEnvironment )
	{	
		count += 1;
		System.out.println(count);
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
		setMapLengthIfAtEnemyBase(inEnvironment);

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
				for(Node stuckNode: stuckNodes)
					if(currentNode.children.get(i).locationY == stuckNode.locationY 
						&& currentNode.children.get(i).locationX == stuckNode.locationX)
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
			stuckNodes.add(currentNode);
			return backtrack(inEnvironment);	
		}
		return advance(inEnvironment, currentNode.children);

	}




	public boolean oursIsCloser(AgentEnvironment inEnvironment)
	{
		if(inEnvironment.isBaseWest(inEnvironment.OUR_TEAM, ranged) 
			&& Math.abs(currentNode.locationX) < mapLength/2 - 1)
		{
			return true;
		}
		if(inEnvironment.isBaseEast(inEnvironment.OUR_TEAM, ranged) 
			&& Math.abs(currentNode.locationX) < mapLength/2 - 1)
		{
			return true;	
		}

		return false;

	}

	public boolean[] getGoalFlags (AgentEnvironment inEnvironment)
	{
		boolean [] goalFlags = {false,false,false,false};
		if( !inEnvironment.hasFlag(inEnvironment.OUR_TEAM) 
				&& !inEnvironment.hasFlag(inEnvironment.ENEMY_TEAM)) {
				// make goal the enemy flag IF WE DONT AND THEY DONT
				goalFlags[0] = inEnvironment.isFlagNorth( 
					inEnvironment.ENEMY_TEAM, ranged );
			
				goalFlags[1] = inEnvironment.isFlagSouth( 
					inEnvironment.ENEMY_TEAM, ranged );
			
				goalFlags[2] = inEnvironment.isFlagEast( 
					inEnvironment.ENEMY_TEAM, ranged );
			
				goalFlags[3] = inEnvironment.isFlagWest( 
					inEnvironment.ENEMY_TEAM, ranged );
				}


			//GO FOR THEIRS IF IM ON THEIR SIDE AND THEY HAVE FLAG AND WE DONT	
			else if(!inEnvironment.hasFlag(inEnvironment.OUR_TEAM)
				&& inEnvironment.hasFlag(inEnvironment.ENEMY_TEAM)
				&& !oursIsCloser(inEnvironment))
			{
				goalFlags[0] = inEnvironment.isFlagNorth( 
					inEnvironment.ENEMY_TEAM, ranged );
			
				goalFlags[1] = inEnvironment.isFlagSouth( 
					inEnvironment.ENEMY_TEAM, ranged );
			
				goalFlags[2] = inEnvironment.isFlagEast( 
					inEnvironment.ENEMY_TEAM, ranged );
			
				goalFlags[3] = inEnvironment.isFlagWest( 
					inEnvironment.ENEMY_TEAM, ranged );
			}	

			//I GO AFTER OUR FLAG FROM ENEMY IF IM ON OUR SIDE AND THEY HAVE FLAG AND WE DONT
			else if(!inEnvironment.hasFlag(inEnvironment.OUR_TEAM)
				&& inEnvironment.hasFlag(inEnvironment.ENEMY_TEAM)
				&& oursIsCloser(inEnvironment))	
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

			//I CAPTURE ENEMY DUDE IF HE HAS FLAG AND I DONT BUT WE DO
			else if(!inEnvironment.hasFlag()
				&& inEnvironment.hasFlag(inEnvironment.OUR_TEAM)
				&& inEnvironment.hasFlag(inEnvironment.ENEMY_TEAM) )
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

			//I GO FOR THEIRS IF HE DOESNT HAVE FLAG AND I DONT BUT WE DO
			else if(!inEnvironment.hasFlag()
				&& inEnvironment.hasFlag(inEnvironment.OUR_TEAM)
				&& !inEnvironment.hasFlag(inEnvironment.ENEMY_TEAM))	
			{
				goalFlags[0] = inEnvironment.isFlagNorth( 
					inEnvironment.ENEMY_TEAM, ranged );
			
				goalFlags[1] = inEnvironment.isFlagSouth( 
					inEnvironment.ENEMY_TEAM, ranged );
			
				goalFlags[2] = inEnvironment.isFlagEast( 
					inEnvironment.ENEMY_TEAM, ranged );
			
				goalFlags[3] = inEnvironment.isFlagWest( 
					inEnvironment.ENEMY_TEAM, ranged );
			}		

			//I CAPTURE ENEMY IF HE HAS FLAG AND I DO BUT IM ON THEIR SIDE
			else if(inEnvironment.hasFlag(inEnvironment.ENEMY_TEAM)
				&& inEnvironment.hasFlag()
				&& !oursIsCloser(inEnvironment))
			{/////
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
				//I GO HOME IF EITHER ENEMY DOESNT HAVE FLAG
				//OR THEY HAVE FLAG BUT IM ON OUR SIDE
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
			uniqueNodes = new ArrayList<Node>();
		}
		else if(bottomHomeCorner && !startedAtNorth)
		{
			currentNode = firstNode;
			uniqueNodes = new ArrayList<Node>();
		}

	}

	public void setMapLengthIfAtEnemyBase(AgentEnvironment inEnvironment)
	{
		if(inEnvironment.isBaseNorth(inEnvironment.ENEMY_TEAM,immediate)
			|| inEnvironment.isBaseSouth(inEnvironment.ENEMY_TEAM,immediate))
		{
			mapLength = Math.abs(currentNode.locationX) + 1;
		}
		else if(inEnvironment.isBaseEast(inEnvironment.ENEMY_TEAM,immediate)
			|| inEnvironment.isBaseWest(inEnvironment.ENEMY_TEAM,immediate))
		{
			mapLength = Math.abs(currentNode.locationX) + 2;
		}

	}

	public int backtrack(AgentEnvironment inEnvironment)
	{		
			//IF WE KEEP BACKTRACKING AT THIS LOCATION MIGHT BE BAD.
			if(currentNode.parent!= null && currentNode.parent.children.size() == 1)	
				currentNode.repetitions++;	

			//PAST THREE WE SHOULD STOP ADVANCING HERE FROM THE PARENT NODE
			if(currentNode.repetitions >= 3)
			{
				stuckNodes.add(currentNode);
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
	

//-------------------------------------------------------------------
	//RETURNS CHILDREN TO BE APPENDED TO TREE
	public ArrayList<Node> expandCurrentNode (AgentEnvironment inEnvironment)
	{
		currentNode.expanded = true;
		Node newNode;
		boolean obstNorth = inEnvironment.isObstacleNorthImmediate();
		boolean obstSouth = inEnvironment.isObstacleSouthImmediate();
		boolean obstEast = inEnvironment.isObstacleEastImmediate();
		boolean obstWest = inEnvironment.isObstacleWestImmediate();

		ArrayList<Node> children = new ArrayList<Node>();
		if(currentNode.direction!='S' && !obstNorth)
		{
			boolean badChild = false;
			int dupeCount = 0;
			for(Node stuckNode: stuckNodes)
			{
				if(stuckNode.locationX == currentNode.locationX 
						&& stuckNode.locationY == currentNode.locationY + 1)
				{
					badChild = true;
					break;
				}
			}


			for(Node uniqueNode : uniqueNodes)
			{
				if(uniqueNode.locationX == currentNode.locationX 
						&& uniqueNode.locationY == currentNode.locationY + 1
						&& uniqueNode.direction == 'N')
				{
					dupeCount++;
				}
			}
			if(!badChild && dupeCount <= 3)
			{
				newNode = new Node(currentNode.locationX,
					currentNode.locationY + 1, currentNode, 'N');
				uniqueNodes.add(newNode);
				children.add(newNode);
			}
		}

		if(currentNode.direction!='N' && !obstSouth)
		{
			boolean badChild = false;
			int dupeCount = 0;
			for(Node stuckNode: stuckNodes)
			{
				if(stuckNode.locationX == currentNode.locationX 
						&& stuckNode.locationY == currentNode.locationY - 1)
				{
					badChild = true;
					break;
				}
			}

			for(Node uniqueNode : uniqueNodes)
			{
				if(uniqueNode.locationX == currentNode.locationX 
						&& uniqueNode.locationY == currentNode.locationY - 1
						&& uniqueNode.direction == 'S')
				{
					dupeCount++;
				}
			}

			if(!badChild && dupeCount <= 3)
			{
				newNode = new Node(currentNode.locationX,
					currentNode.locationY - 1, currentNode, 'S');
				uniqueNodes.add(newNode);
				children.add(newNode);
			}

		}

		//EAST
		if(currentNode.direction!='W' && !obstEast)
		{
			boolean badChild = false;
			int dupeCount = 0;
			for(Node stuckNode: stuckNodes)
			{
				if(stuckNode.locationX == currentNode.locationX + 1
						&& stuckNode.locationY == currentNode.locationY)
				{
					badChild = true;
					break;
				}
			}
			for(Node uniqueNode : uniqueNodes)
			{
				if(uniqueNode.locationX == currentNode.locationX + 1
						&& uniqueNode.locationY == currentNode.locationY
						&& uniqueNode.direction == 'E')
				{
					dupeCount++;
				}
			}

			if(!badChild && dupeCount <= 3)
			{	
				newNode = new Node(currentNode.locationX + 1,
					currentNode.locationY, currentNode, 'E');
				uniqueNodes.add(newNode);
				children.add(newNode);
			}
		}

		//WEST
		if(currentNode.direction!='E' && !obstWest)
		{
			boolean badChild = false;
			int dupeCount = 0;
			for(Node stuckNode: stuckNodes)
			{
				if(stuckNode.locationX == currentNode.locationX - 1
						&& stuckNode.locationY == currentNode.locationY)
				{
					badChild = true;
					break;
				}
			}
			for(Node uniqueNode : uniqueNodes)
			{
				if(uniqueNode.locationX == currentNode.locationX - 1
						&& uniqueNode.locationY == currentNode.locationY
						&& uniqueNode.direction == 'W')
				{
					dupeCount++;				}
			}

			if(!badChild && dupeCount <= 3)
			{
				newNode = new Node(currentNode.locationX - 1,
					currentNode.locationY, currentNode, 'W');
				uniqueNodes.add(newNode);
				children.add(newNode);
			}
		}

		return children;
	}



	public Node selectNode(AgentEnvironment inEnvironment, ArrayList<Node> children)
	{


		boolean northBlocked = inEnvironment.isAgentNorth(inEnvironment.OUR_TEAM,immediate);
		boolean southBlocked = inEnvironment.isAgentSouth(inEnvironment.OUR_TEAM,immediate);
		boolean eastBlocked = inEnvironment.isAgentEast(inEnvironment.OUR_TEAM,immediate);
		boolean westBlocked = inEnvironment.isAgentWest(inEnvironment.OUR_TEAM,immediate);

		if(inEnvironment.hasFlag() && oursIsCloser(inEnvironment))
		{
			northBlocked = northBlocked 
				|| inEnvironment.isAgentNorth(inEnvironment.ENEMY_TEAM,immediate);
			southBlocked = southBlocked 
				|| inEnvironment.isAgentSouth(inEnvironment.ENEMY_TEAM,immediate);
			eastBlocked = eastBlocked  
				|| inEnvironment.isAgentEast(inEnvironment.ENEMY_TEAM,immediate);
			westBlocked = westBlocked 
				|| inEnvironment.isAgentWest(inEnvironment.ENEMY_TEAM,immediate);
		}
		/*
		else if((inEnvironment.hasFlag() && !oursIsCloser(inEnvironment))|| inEnvironment.hasFlag(inEnvironment.ENEMY_TEAM))
		{
			northBlocked = northBlocked 
				|| (inEnvironment.isAgentNorth(inEnvironment.ENEMY_TEAM,immediate)
					&& !inEnvironment.isFlagNorth(inEnvironment.OUR_TEAM,immediate));
			southBlocked = southBlocked 
				|| (inEnvironment.isAgentSouth(inEnvironment.ENEMY_TEAM,immediate)
					&& !inEnvironment.isFlagSouth(inEnvironment.OUR_TEAM,immediate));
			eastBlocked = eastBlocked  
				|| (inEnvironment.isAgentEast(inEnvironment.ENEMY_TEAM,immediate)
					&& !inEnvironment.isFlagEast(inEnvironment.OUR_TEAM,immediate));
			westBlocked = westBlocked 
				|| (inEnvironment.isAgentWest(inEnvironment.ENEMY_TEAM,immediate)
					&& !inEnvironment.isFlagWest(inEnvironment.OUR_TEAM,immediate));
		}
		*/
		else
		{
			northBlocked = northBlocked 
				|| (inEnvironment.isAgentNorth(inEnvironment.ENEMY_TEAM,immediate)
					&& !inEnvironment.isFlagNorth(inEnvironment.OUR_TEAM,immediate));
			southBlocked = southBlocked 
				|| (inEnvironment.isAgentSouth(inEnvironment.ENEMY_TEAM,immediate)
					&& !inEnvironment.isFlagSouth(inEnvironment.OUR_TEAM,immediate));
			eastBlocked = eastBlocked  
				|| (inEnvironment.isAgentEast(inEnvironment.ENEMY_TEAM,immediate)
					&& !inEnvironment.isFlagEast(inEnvironment.OUR_TEAM,immediate));
			westBlocked = westBlocked 
				|| (inEnvironment.isAgentWest(inEnvironment.ENEMY_TEAM,immediate)
					&& !inEnvironment.isFlagWest(inEnvironment.OUR_TEAM,immediate));
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
