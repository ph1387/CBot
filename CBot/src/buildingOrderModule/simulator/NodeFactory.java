package buildingOrderModule.simulator;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

// TODO: UML ADD
/**
 * NodeFactory.java --- A factory that stores and creates instances of
 * {@link Node}s. This is necessary since vast amounts of these containers are
 * necessary for performing a simulation and therefore creating the objects of
 * the class is CPU intensive. Therefore this class provides a way of using the
 * same instances multiple times. </br>
 * </br>
 * <b> Notice: </b> The values in the {@link Node} instances are <b>NOT</b>
 * reseted. This functionality must be manually implemented!
 * 
 * @author P H - 05.07.2017
 *
 */
public class NodeFactory {

	private Queue<Node> availableNodes = new LinkedList<Node>();

	public NodeFactory(int intitialNodeCount) {
		for (int i = 0; i < intitialNodeCount; i++) {
			this.availableNodes.add(this.createNewNode());
		}
	}

	public NodeFactory() {

	}

	// -------------------- Functions

	private Node createNewNode() {
		return new Node();
	}

	/**
	 * Function for receiving an available Node instance.
	 * 
	 * @return an available Node instance.
	 */
	public Node receiveNode() {
		// If there are no available Nodes inside the Queue create new ones.
		if (this.availableNodes.peek() == null) {
			this.availableNodes.add(this.createNewNode());
		}

		return this.availableNodes.poll();
	}

	/**
	 * Function for returning a Node instance making it available for other
	 * receivers again.
	 * 
	 * @param node
	 *            the Node that can be used by other classes.
	 */
	public void markNodeAsAvailable(Node node) {
		this.availableNodes.add(node);
	}

	/**
	 * Function for returning a collection of Node instances making them
	 * available for other receivers again.
	 * 
	 * @param nodes
	 *            the Nodes that can be used by other classes.
	 */
	public void markNodesAsAvailable(Collection<Node> nodes) {
		for (Node node : nodes) {
			this.markNodeAsAvailable(node);
		}
	}
}
