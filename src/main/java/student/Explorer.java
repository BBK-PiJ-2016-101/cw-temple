package student;

import game.EscapeState;
import game.ExplorationState;
import game.NodeStatus;
import game.Node;
import game.Edge;
import java.util.Collection;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.Set;

import java.util.Collections;
import java.util.Arrays;

public class Explorer {

    // Keep class scope references to the states to pass between methods
    ExplorationState theExplorationState = null;
    EscapeState theEscapeState = null;
    
  /**
   * Explore the cavern, trying to find the orb in as few steps as possible.
   * Once you find the orb, you must return from the function in order to pick
   * it up. If you continue to move after finding the orb rather
   * than returning, it will not count.
   * If you return from this function while not standing on top of the orb,
   * it will count as a failure.
   *   
   * <p>There is no limit to how many steps you can take, but you will receive
   * a score bonus multiplier for finding the orb in fewer steps.</p>
   * 
   * <p>At every step, you only know your current tile's ID and the ID of all
   * open neighbor tiles, as well as the distance to the orb at each of these tiles
   * (ignoring walls and obstacles).</p>
   * 
   * <p>To get information about the current state, use functions
   * getCurrentLocation(),
   * getNeighbours(), and
   * getDistanceToTarget()
   * in ExplorationState.
   * You know you are standing on the orb when getDistanceToTarget() is 0.</p>
   *
   * <p>Use function moveTo(long id) in ExplorationState to move to a neighboring
   * tile by its ID. Doing this will change state to reflect your new position.</p>
   *
   * <p>A suggested first implementation that will always find the orb, but likely won't
   * receive a large bonus multiplier, is a depth-first search.</p>
   *
   * @param state the information available at the current state
   */

    // Depth first search to the Orbs
    public void explore(ExplorationState state) {
	// set the class scope reference
	this.theExplorationState = state;
	// mark the first NodeStatus visited
	List visited = new ArrayList();
	visited.add(this.theExplorationState.getCurrentLocation());
	// initialize a new stack to contain steps in the path to date
	Stack<NodeStatus> depthFirstPath = new Stack();
	// use NeighbourStack to return a stack of Neighbour objects to start the DFS
	Stack<NodeStatus> depthFirstStack = NeighbourStack(this.theExplorationState);
	// visit each neighbour iteratively
	while (depthFirstStack.empty() == false) {
	    // get the next neighbour
	    NodeStatus nextNode = depthFirstStack.pop();
	    // check it hasn't been visited before
	    if (! visited.contains(nextNode.getId())) {
		// if the nextNode is adjacent, move to it
		// make a list of neighbour's getId() to test if nextNode's getId() is a member
		List neighbourIds = new ArrayList();
		for (NodeStatus neighbour : this.theExplorationState.getNeighbours()) {
		    neighbourIds.add(neighbour.getId());
		}
		if (neighbourIds.contains(nextNode.getId())) {
		    this.theExplorationState.moveTo(nextNode.getId());		    
		}
		// if the nextNode is not adjacent, replay the steps in the path to date
		// to get to a tile that neighbours the new branch of the DFS
		// i.e. rewind from the dead end
		else {
		    // keep the path step pop'd from the stack in a predictable reference for re-use
		    NodeStatus rewindHead = null;
		    // the top of the stack will be current tile, pop it and look at the neighbours
		    depthFirstPath.pop();
		    // debug print, should be debug logged
		    // System.out.println(nextNode.getId());
		    while (!neighbourIds.contains(nextNode.getId())) {
			// move to the previous tile
			// debug print, should be debug logged
			// System.out.println("Rewinding to " + depthFirstPath.peek().getId());
			rewindHead = depthFirstPath.pop();
			this.theExplorationState.moveTo(rewindHead.getId());
			// rebuild neighbourIds for the while loop test
			neighbourIds = new ArrayList();
			for (NodeStatus neighbour : this.theExplorationState.getNeighbours()) {
			    neighbourIds.add(neighbour.getId());
			}
		    }
		    // Ensure the path is not made disjoint if the same tile is rewound to twice
		    // Put the rewindHead back on the path, it will be part of the path to the next branch
		    // The next branch starts at nextNode
		    // debug print, should be debug logged
		    // System.out.println("Rewound to " + theExplorationState.getCurrentLocation());
		    depthFirstPath.push(rewindHead);
		    this.theExplorationState.moveTo(nextNode.getId());
		}
		// mark nextNode visited and push it to the path stack
		// debug print, should be debug log
		// System.out.println("Moved to " + this.theExplorationState.getCurrentLocation());
		// System.out.println("The Orb is " + this.theExplorationState.getDistanceToTarget() + "  tiles away");
		visited.add(this.theExplorationState.getCurrentLocation());
		depthFirstPath.push(nextNode);
		// debug print, should be debug log
		//System.out.println("Pushed " + nextNode.getId() + " to the path stack");
		if (this.theExplorationState.getDistanceToTarget() == 0) {
		    // print for unit test
		    System.out.println("The orb is at " + this.theExplorationState.getCurrentLocation());
		    break;
		}
		for (NodeStatus neighbour : this.theExplorationState.getNeighbours()) {
		    depthFirstStack.push(neighbour);
		}
	    }
	}
    }

    // helper method to put getNeighbours in a stack and return them
    // to build the root DFS level
    private Stack NeighbourStack(ExplorationState state) {
	Stack<NodeStatus> neighbourStack = new Stack ();
	for (NodeStatus neighbour : state.getNeighbours()) {
	    neighbourStack.push(neighbour);
	}
	return neighbourStack;
    }

  /**
   * Escape from the cavern before the ceiling collapses, trying to collect as much
   * gold as possible along the way. Your solution must ALWAYS escape before time runs
   * out, and this should be prioritized above collecting gold.
   *
   * <p>You now have access to the entire underlying graph, which can be accessed 
   * through EscapeState.
   * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
   * will return a collection of all nodes on the graph.</p>
   * 
   * <p>Note that time is measured entirely in the number of steps taken, and for each step
   * the time remaining is decremented by the weight of the edge taken. You can use
   * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
   * on your current tile (this will fail if no such gold exists), and moveTo() to move
   * to a destination node adjacent to your current node.</p>
   * 
   * <p>You must return from this function while standing at the exit. Failing to do so before time
   * runs out or returning from the wrong location will be considered a failed run.</p>
   * 
   * <p>You will always have enough time to escape using the shortest path from the starting
   * position to the exit, although this will not collect much gold.</p>
   *
   * @param state the information available at the current state
   */

    // compute Dijkstra's algorithm with a minimum priority queue to escape
    // pick up gold along the way
    public void escape(EscapeState state) {
	// set the class scope reference
	this.theEscapeState = state;
	// initialize the current node as an object for the Min PQ
	ComparableNode startNode = new ComparableNode(theEscapeState.getCurrentNode());
	// set the distance of the start vertex to zero
	startNode.distance = 0;
	// construct the Min PQ using an ArrayList of Nodes wrapped in class implementing Comparable for Collections.sort()
	List<ComparableNode> allNodes = new ArrayList();
	for (Node node : theEscapeState.getVertices()) {
	    ComparableNode comparableNode = new ComparableNode(node);
	    // use an arbitrarily large 'infinity' value, 10^8
	    comparableNode.distance = 100000000;
	    allNodes.add(comparableNode);
	}
	allNodes.add(startNode);
	Collections.sort(allNodes);
	// iteratively relax vertices using Dijsktra's algorithm
	// CLRS/Introduction to Algoritms 3rd Edition
	// "24.3 Dijkstra's algorithm" informed iterative implementation
	List<ComparableNode> relaxedNodes = new ArrayList();
	// test the Min PQ is not empty and take the nearest vertex
	while (allNodes.size() != 0) {
	    ComparableNode thisNode = allNodes.remove(0);
	    // check if a shorter path is available from here
	    for (Node neighbour : thisNode.innerNode.getNeighbours()) {
		int distance = thisNode.innerNode.getEdge(neighbour).length;
		// find the neighbour in the PQ
		for (ComparableNode relaxNode : allNodes) {
		    if (relaxNode.innerNode == neighbour) {
			// prepare to update by get then set
			// 'swap' the object at the index with it's updated state
			int index = allNodes.indexOf(relaxNode);
			ComparableNode swap = allNodes.get(index);
			if (swap.distance > thisNode.distance + distance) {
			    swap.distance = thisNode.distance + distance;
			    // link this node to it's predecessor in the shortest path for escaping
			    swap.lastNode = thisNode;
			    allNodes.set(index, swap);
			}
		    }
		}
	    }
	    // add this node to the relaxed set
	    relaxedNodes.add(thisNode);
	    // sort the PQ to take the next nearest vertex after the loop test
	    Collections.sort(allNodes);
	}
	// sort the vertices by distance from the start
	// set references to the start and end of the escape path
	// set reference to the distance from exit
	Collections.sort(relaxedNodes);
	ComparableNode relaxedStartNode = null;
	ComparableNode relaxedExitNode = null;
	int relaxedExitDistance = 0;
	for (ComparableNode relaxedNode : relaxedNodes) {
	    // debug print, should be debug log
	    // System.out.println("Distance to " + relaxedNode.innerNode.getId() + " is " + relaxedNode.distance);
	    if (relaxedNode.innerNode == theEscapeState.getExit()) {
		relaxedExitDistance = relaxedNode.distance;
		relaxedExitNode = relaxedNode;
	    }
	    if (relaxedNode.innerNode == theEscapeState.getCurrentNode()) {
		relaxedStartNode = relaxedNode;
	    }
	}
	// print distance from the exit for unit test
	System.out.println("The exit is " + relaxedExitDistance + " distance away");
	// build the escape path to follow to escape
        ComparableNode pathElement = relaxedExitNode.lastNode;
	List<Node> nodePath = new ArrayList();
	while (pathElement != null) {
	    // debug print, should be debug log
	    // System.out.println(pathElement.innerNode.getId() + " is " + (relaxedExitDistance - pathElement.distance) + " away from exit ");
	    /* debug test print
	       find where this path intersects current node
	       it's not obvious from the code above that the escape path includes the current node
	       which cannot be moveTo()'d
	       and not the exit itself
	       which must be moveTo()'d explicitly as a last step
	    if (pathElement.innerNode.getNeighbours().contains(theEscapeState.getCurrentNode())) {
		System.out.println("And is a neighbour");
	    }
	    */
	    // add the path and look ahead
	    nodePath.add(pathElement.innerNode);
	    pathElement = pathElement.lastNode;
	}
	// path is in reverse order, and contains the current node
	// reverse it and remove the head
	Collections.reverse(nodePath);
	nodePath.remove(0);

	// follow the escape path
	for (Node pathNode : nodePath) {
	    // test for and pick up gold on the current tile
	    if (theEscapeState.getCurrentNode().getTile().getGold() > 0) {
		theEscapeState.pickUpGold();
	    }
	    // debug print, should be debug log
	    // System.out.println("Moving to " + pathNode.getId());
	    theEscapeState.moveTo(pathNode);
	}
	// moveTo() to getExit()
	theEscapeState.moveTo(theEscapeState.getExit());
    }
}
