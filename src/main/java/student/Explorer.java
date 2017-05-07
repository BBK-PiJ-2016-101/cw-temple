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

    ExplorationState theExplorationState = null;
    EscapeState theEscapeState = null;

    long thePreviousNode = 0;
    
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
    public void explore(ExplorationState state) {
	this.theExplorationState = state;
	List visited = new ArrayList();
	visited.add(this.theExplorationState.getCurrentLocation());
	Stack<NodeStatus> depthFirstPath = new Stack();
	Stack<NodeStatus> depthFirstStack = NeighbourStack(this.theExplorationState);
	while (depthFirstStack.empty() == false) {
	    NodeStatus nextNode = depthFirstStack.pop();
	    if (! visited.contains(nextNode.getId())) {
		// If the nextNode is adjacent, move to it, otherwise, rewind
		List neighbourIds = new ArrayList();
		for (NodeStatus neighbour : this.theExplorationState.getNeighbours()) {
		    neighbourIds.add(neighbour.getId());
		}
		if (neighbourIds.contains(nextNode.getId())) {
		    this.theExplorationState.moveTo(nextNode.getId());		    
		} else {
		    NodeStatus rewindHead = null;
		    depthFirstPath.pop();
		    System.out.println(nextNode.getId());
		    while (!neighbourIds.contains(nextNode.getId())) {
			System.out.println("Rewinding to " + depthFirstPath.peek().getId());
			rewindHead = depthFirstPath.pop();
			this.theExplorationState.moveTo(rewindHead.getId());
			neighbourIds = new ArrayList();
			for (NodeStatus neighbour : this.theExplorationState.getNeighbours()) {
			    neighbourIds.add(neighbour.getId());
			}
		    }
		    System.out.println("Rewound to " + theExplorationState.getCurrentLocation());
		    // Put this back on the path so as not to disjoint it if we rewind to the same place twice
		    depthFirstPath.push(rewindHead);
		    this.theExplorationState.moveTo(nextNode.getId());
		}
		System.out.println("Moved to " + this.theExplorationState.getCurrentLocation());
		System.out.println("The Orb is " + this.theExplorationState.getDistanceToTarget() + "  tiles away");
		visited.add(this.theExplorationState.getCurrentLocation());
		depthFirstPath.push(nextNode);
		System.out.println("Pushed " + nextNode.getId() + " to the path stack");
		if (this.theExplorationState.getDistanceToTarget() == 0) {
		    System.out.println("The orb is at " + this.theExplorationState.getCurrentLocation());
		    break;
		}
		for (NodeStatus neighbour : this.theExplorationState.getNeighbours()) {
		    depthFirstStack.push(neighbour);
		}
	    }
	}
    }

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

    public void escape(EscapeState state) {
	this.theEscapeState = state;
	ComparableNode startNode = new ComparableNode(theEscapeState.getCurrentNode());
	startNode.distance = 0;
	List<ComparableNode> allNodes = new ArrayList();
	for (Node node : theEscapeState.getVertices()) {
	    ComparableNode comparableNode = new ComparableNode(node);
	    comparableNode.distance = 1000000;
	    allNodes.add(comparableNode);
	}
	allNodes.add(startNode);
	Collections.sort(allNodes);
	List<ComparableNode> relaxedNodes = new ArrayList();
	while (allNodes.size() != 0) {
	    ComparableNode thisNode = allNodes.remove(0);
	    for (Node neighbour : thisNode.innerNode.getNeighbours()) {
		int distance = thisNode.innerNode.getEdge(neighbour).length;
		for (ComparableNode relaxNode : allNodes) {
		    if (relaxNode.innerNode == neighbour) {
			int index = allNodes.indexOf(relaxNode);
			ComparableNode swap = allNodes.get(index);
			if (swap.distance > thisNode.distance + distance) {
			    swap.distance = thisNode.distance + distance;
			    swap.lastNode = thisNode;
			    thisNode.nextNode = swap;
			    allNodes.set(index, swap);
			}
		    }
		}
	    }
	    relaxedNodes.add(thisNode);
	    Collections.sort(allNodes);
	}
	Collections.sort(relaxedNodes);
	ComparableNode relaxedStartNode = null;
	ComparableNode relaxedExitNode = null;
	int relaxedExitDistance = 0;
	for (ComparableNode relaxedNode : relaxedNodes) {
	    System.out.println("Distance to " + relaxedNode.innerNode.getId() + " is " + relaxedNode.distance);
	    if (relaxedNode.innerNode == theEscapeState.getExit()) {
		relaxedExitDistance = relaxedNode.distance;
		relaxedExitNode = relaxedNode;
	    }
	    if (relaxedNode.innerNode == theEscapeState.getCurrentNode()) {
		relaxedStartNode = relaxedNode;
	    }
	}
	System.out.println("The exit is " + relaxedExitDistance + " distance away");
        ComparableNode pathElement = relaxedExitNode.lastNode;
	List<Node> nodePath = new ArrayList();
	while (pathElement != null) {
	    System.out.println(pathElement.innerNode.getId() + " is " + (relaxedExitDistance - pathElement.distance) + " away from exit ");
	    if (pathElement.innerNode.getNeighbours().contains(theEscapeState.getCurrentNode())) {
		System.out.println("And is a neighbour");
	    }
	    nodePath.add(pathElement.innerNode);
	    pathElement = pathElement.lastNode;
	}
	Collections.reverse(nodePath);
	nodePath.remove(0);
	/*
	for (int i = nodePath.size() - 1; i >= 0; i--) { 
	    theEscapeState.moveTo(nodePath.get(i));
	    System.out.println("I moved to " + nodePath.get(i).getId());
	}
	*/
	for (Node pathNode : nodePath) {
	    System.out.println("Moving to " + pathNode.getId());
	    theEscapeState.moveTo(pathNode);
	}
	theEscapeState.moveTo(theEscapeState.getExit());

	System.out.println("Forward");
	pathElement = relaxedStartNode.nextNode;
	while (pathElement != null) {
	    System.out.println(pathElement.innerNode.getId());
	    pathElement = pathElement.nextNode;
	}		
	
	/*
	List<ComparableNode> startNodes = new ArrayList();
	List<ComparableEdge> startEdges = new ArrayList();
	for (Edge edge : startNode.innerNode.getExits()) {
	    startEdges.add(new ComparableEdge(edge));
	}
	Collections.sort(startEdges);
	for (ComparableEdge edge : startEdges) {
	    ComparableNode node = new ComparableNode(edge.innerEdge.getDest());
	    node.distance = startNode.distance + edge.innerEdge.length;
	    startNodes.add(node);
	}
	Collections.sort(startNodes);
	for (ComparableNode node : startNodes) {
	    System.out.println("Node ID " + node.innerNode.getId() + " has a distance of " + node.distance);
	}
	*/
    }
}
	
	/*
	List<Node> neighbours = new ArrayList<Node>(firstNode.getNeighbours());
	List<Edge> edges = new ArrayList();
	for (Node neighbour : neighbours) {
	    edges.add(firstNode.getEdge(neighbour));
	}
	for (Edge edge : edges) {
	    System.out.println("The edge from " + firstNode.getId() + " to " + edge.getDest().getId() + " is " + edge.length + " long");
	}
	System.out.println("We have " + this.theEscapeState.getTimeRemaining() + " time remaining");
	this.theEscapeState.moveTo(neighbours.get(0));
	System.out.println("We have " + this.theEscapeState.getTimeRemaining() + " time remaining");
	ComparableEdge firstEdge = new ComparableEdge(edges.get(0));
	ComparableEdge secondEdge = new ComparableEdge(edges.get(1));
	if (firstEdge.compareTo(secondEdge) < 1) {
	    System.out.println("Shortest edge is " + firstEdge.innerEdge.length);
	} else {
	    System.out.println("Shortest edge is " + secondEdge.innerEdge.length);
	}
    }

    /*
    private(EscapeState state, int pathRange) {
	Node currentNode = state.getCurrentNode();
	List<Edge> edges = new ArrayList<Edges>(currentNode.getExits());
    }
    */
