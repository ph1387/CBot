package bwapiMath.graph;

import java.util.HashMap;

import bwta.BWTA;
import bwta.Chokepoint;
import bwta.Region;

//TODO: UML ADD
/**
 * RegionInstanceMapper.java --- Class implementing the IIstanceMapper-Interface
 * for mapping BWTA.Region instances to unique indices.
 * 
 * @author P H - 08.03.2018
 *
 */
public class RegionInstanceMapper implements IInstanceMapper<Region> {

	// -------------------- Functions

	@Override
	public HashMap<Region, Integer> map() {
		HashMap<Region, Integer> usedHashMap = new HashMap<>();
		int counter = 0;

		// Add an index / Integer to each Region. This is necessary since the
		// graph used is based on an adjacency List using Integers. ChokePoints
		// are used since the references obtained by them and BWTA.getRegions()
		// differ!
		for (Chokepoint chokePoint : BWTA.getChokepoints()) {
			Region regionOne = chokePoint.getRegions().first;
			Region regionTwo = chokePoint.getRegions().second;

			// Add the Region references if they are not already inside the
			// HashMap.
			if (!usedHashMap.containsKey(regionOne)) {
				usedHashMap.put(regionOne, counter);
				counter++;
			}
			if (!usedHashMap.containsKey(regionTwo)) {
				usedHashMap.put(regionTwo, counter);
				counter++;
			}
		}

		return usedHashMap;
	}

	// ------------------------------ Getter / Setter

}
