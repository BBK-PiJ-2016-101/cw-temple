package student;
import game.NodeStatus;

public class StackedNodeStatus {
    NodeStatus node = null;
    int visited = 0;
    public StackedNodeStatus(NodeStatus node) {
	this.node = node;
    }
    public void setVisited(int visited) {
	this.visited = visited;
    }
}
