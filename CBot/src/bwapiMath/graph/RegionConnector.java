package bwapiMath.graph;

import java.util.HashMap;
import java.util.function.BiConsumer;

import bwapi.Pair;
import bwapi.Unit;
import bwta.Chokepoint;
import bwta.Region;
import core.CBot;

//TODO: UML ADD
/**
 * RegionConnector.java --- Class implementing the IConnector-Interface
 * connecting BWTA.Regions in the graph based on their accessibility to one
 * another.
 * 
 * @author P H - 08.03.2018
 *
 */
public class RegionConnector implements IConnector<Region> {

	// -------------------- Functions

	@Override
	public void addConnectionsToGraph(final DirectedGraphList graph, final HashMap<Region, Integer> mappedInstances) {
		// Add ChokePoints as edges to the created graph using the stored
		// indices in the HashMap.
		mappedInstances.forEach(new BiConsumer<Region, Integer>() {

			@Override
			public void accept(Region region, Integer index) {
				for (Chokepoint chokePoint : region.getChokepoints()) {
					boolean chokePointFree = true;

					for (Pair<Unit, Chokepoint> blockedInstance : CBot.getInstance().getInformationStorage()
							.getMapInfo().getMineralBlockedChokePoints()) {
						// Equals of the ChokePoints can NOT be used here since
						// the references are NOT the same! Therefore the
						// Position of the sides must be checked.
						if (blockedInstance.second.getSides().equals(chokePoint.getSides())) {
							chokePointFree = false;

							break;
						}
					}

					// Only add non-blocked ChokePoints to the connections.
					if (chokePointFree) {
						// Find the other region of the ChokePoint to determine
						// the
						// edge that must be added to the graph.
						Region otherRegion = chokePoint.getRegions().first;

						if (otherRegion == region) {
							otherRegion = chokePoint.getRegions().second;
						}

						graph.addEdge(index, mappedInstances.get(otherRegion));
					}
				}
			}
		});
	}

	// ------------------------------ Getter / Setter

}
