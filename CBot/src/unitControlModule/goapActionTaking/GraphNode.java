package unitControlModule.goapActionTaking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

class GraphNode { // TODO: Add to UML and Descriptions
	
	GoapAction action = null;
	
	HashSet<GoapState> preconditions;
	HashSet<GoapState> effects;
	List<GraphPath<GraphNode, DefaultWeightedEdge>> pathsToThisNode = new ArrayList<GraphPath<GraphNode, DefaultWeightedEdge>>();
	
	GraphNode(HashSet<GoapState> preconditions, HashSet<GoapState> effects) {
		this.preconditions = preconditions;
		this.effects = effects;
	}
	
	GraphNode(GoapAction goapAction) {
		if(goapAction != null) {
			this.preconditions = goapAction.getPreconditions();
			this.effects = goapAction.getEffects();
			this.action = goapAction;
		}
	}
	
	// -------------------- Functions
	
	void overwriteOwnProperties(GraphNode newGraphNode) {
		if(newGraphNode != null) {
			this.action = newGraphNode.action;
			this.preconditions = newGraphNode.preconditions;
			this.effects = newGraphNode.effects;
		}
	}
	
	/**
	 * Function for adding paths to a node so that the order in which the node is accessed is saved (Important!). If these would not be stored invalid orders of actions could be added to the graph as a node can return multiple access paths!
	 *
	 * @param newPath the path with which the node is accessible.
	 */
	void addGraphPath(GraphPath<GraphNode, DefaultWeightedEdge> newPath) {
		List<GraphNode> newPathNodeList = newPath.getVertexList();
		boolean notInSet = true;
		
		if(this.pathsToThisNode.isEmpty()) {
			notInSet = true;
		} else {
			for (GraphPath<GraphNode, DefaultWeightedEdge> storedPath : this.pathsToThisNode) {
				List<GraphNode> nodeList = storedPath.getVertexList();
				boolean isSamePath = true;
				
				for (int i = 0; i < nodeList.size() && isSamePath; i++) {
					if(!nodeList.get(i).equals(newPathNodeList.get(i))) {
						isSamePath = false;
					}
				}
				
				if(isSamePath) {
					notInSet = false;
					
					break;
				}
			}
		}
		
		if(notInSet) {
			this.pathsToThisNode.add(newPath);
		}
	}
	
	
	
	
	
	
	
	// TODO: REMOVE
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if(this.effects == null) {
			sb.append("GOAL: ");
			
			for (GoapState goapState : this.preconditions) {
				sb.append(goapState.effect.toString() + "=" + goapState.value + "-");
			}
		} else {
			if(this.action == null && this.preconditions == null) {
				sb.append("START: ");
			} else {
				sb.append(this.action.getClass().getSimpleName() + ": ");
			}
			
			for (GoapState goapState : this.effects) {
				sb.append(goapState.effect.toString() + "=" + goapState.value + "-");
			}
		}
		return sb.toString();
	}
}
