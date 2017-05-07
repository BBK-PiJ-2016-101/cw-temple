package student;

import java.util.*;
import game.Node;

public class ComparableNode implements Comparable<ComparableNode> {
    public Node innerNode = null;
    public ComparableNode lastNode = null;
    public ComparableNode nextNode = null;
    public int distance = -1;

    public ComparableNode(Node node) {
	this.innerNode = node;
    }

    public int compareTo(ComparableNode other) {
	if (this.distance > other.distance) {
	    return 1;
	} else if (this.distance < other.distance) {
	    return -1;
	} else {
	    return 0;
	}
    }
}
