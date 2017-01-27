package unitControlModule.goapActionTaking;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.junit.Test;

public class GoapPlannerTest {

	@Test
	public void planTest() {
			OwnUnitWrapper unit = new OwnUnitWrapper();
			
			long startTime = System.nanoTime();
			Queue<GoapAction> actions = GoapPlanner.plan(unit);
			long endTime = System.nanoTime();

			long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
			System.out.println(duration + "ns - " + duration/1000000 + "ms");
	}
			
			// Insert in addVertices to see all connections
//			for (GraphNode graphNode : graph.vertexSet()) {
//				try {
//					System.out.println("--->" + graphNode.action.getClass().getName());
//				} catch (Exception e) {
//					if(graphNode.equals(start)) {
//						System.out.println("---> START");
//					} else {
//						System.out.println("---> END");
//					}
//				}
//				
//				System.out.print("effects");
//				if(graphNode.effetcs != null) {
//					System.out.println("[" + graphNode.effetcs.size() +"]:");
//					for (GoapState goapState : graphNode.effetcs) {
//						System.out.println(goapState.effect);
//					}
//				} else {
//					System.out.println("[0]:");
//				}
//				
//				System.out.print("preconditions");
//				if(graphNode.preconditions != null) {
//					System.out.println("[" + graphNode.preconditions.size() +"]:");
//					for (GoapState goapState : graphNode.preconditions) {
//						System.out.println(goapState.effect);
//					}
//				} else {
//					System.out.println("[0]:");
//				}
//			}
//			System.out.println("--------------------");
}
