package bwapiMath.graph;

import java.util.HashMap;
import java.util.List;

import bwta.Region;

/**
 * RegionInstanceMapper.java --- Class implementing the IIstanceMapper-Interface
 * for mapping BWTA.Region instances to unique indices.
 * 
 * @author P H - 08.03.2018
 *
 */
public class RegionInstanceMapper implements IInstanceMapper<Region> {

	private List<Region> regions;
	
	public RegionInstanceMapper(List<Region> regions) {
		this.regions = regions;
	}
	
	// -------------------- Functions

	@Override
	public HashMap<Region, Integer> map() {
		HashMap<Region, Integer> usedHashMap = new HashMap<>();

		// Add an index / Integer to each Region. This is necessary since the
		// graph used is based on an adjacency List using Integers.
		for(int i = 0; i < this.regions.size(); i++) {
			usedHashMap.put(this.regions.get(i), i);
		}

		return usedHashMap;
	}

	// ------------------------------ Getter / Setter

}
