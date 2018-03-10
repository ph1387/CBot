package informationStorage;

import java.util.HashSet;

import bwta.Region;

// TODO: UML ADD
/**
 * DistantRegion.java --- A wrapper Class used for encapsulating a single
 * Region. The total distance needed to reach it as well as the accessible other
 * Regions are also stored in this Class.
 * 
 * @author P H - 10.03.2018
 *
 */
public class DistantRegion implements Comparable<DistantRegion> {

	private double distance = -1.;
	private Region region;
	private HashSet<Region> accessibleRegions;

	public DistantRegion(double distance, Region region, HashSet<Region> accessibleRegions) {
		this.distance = distance;
		this.region = region;
		this.accessibleRegions = accessibleRegions;
	}

	// -------------------- Functions

	@Override
	public int compareTo(DistantRegion region) {
		// DistantRegions with a shorter total distances are moved towards
		// the front.
		return Double.compare(this.distance, region.getDistance());
	}

	// ------------------------------ Getter / Setter

	public double getDistance() {
		return distance;
	}

	public Region getRegion() {
		return region;
	}

	public HashSet<Region> getAccessibleRegions() {
		// Prevent NullPointer Exceptions!
		if (this.accessibleRegions == null) {
			return new HashSet<>();
		}
		return this.accessibleRegions;
	}

}
