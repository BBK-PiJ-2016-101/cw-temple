package student;

import java.util.*;
import game.Edge;

  /**
   * <p> Wrap the Edge class and implement Comparable,
   *     to construct sortable collections of Edge</p>
   *
   * @param Edge to wrap
   */
public class ComparableEdge implements Comparable<ComparableEdge> {
    public Edge innerEdge = null;

    public ComparableEdge(Edge edge) {
	this.innerEdge = new Edge(edge.getSource(), edge.getDest(), edge.length);
    }

    public int compareTo(ComparableEdge other) {
	if (this.innerEdge.length > other.innerEdge.length) {
	    return 1;
	} else if (this.innerEdge.length < other.innerEdge.length) {
	    return -1;
	} else {
	    return 0;
	}
    }
}
