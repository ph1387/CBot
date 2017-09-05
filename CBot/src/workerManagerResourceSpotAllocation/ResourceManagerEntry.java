package workerManagerResourceSpotAllocation;

import bwapi.Position;

/**
 * ResourceManagerEntry.java --- The Interface that each worker that is going to
 * be added to the {@link IResourceManager}s.
 * 
 * @author P H - 03.09.2017
 *
 */
public interface ResourceManagerEntry {

	/**
	 * Function defining the current Position of the Unit. Needed for assigning
	 * the Unit to the closest free gathering spot on the map.
	 * 
	 * @return the current Position of the Unit.
	 */
	public Position getPosition();

}
