package workerManagerResourceSpotAllocation;

import bwapi.Position;
import bwapi.Unit;

/**
 * GatheringSource.java --- An Interface specifying a single gathering source
 * or, addressing minerals, a group of possible gathering sources. The Class
 * implementing this Interface can be used by {@link IResourceManager}s for
 * adding different Units to them based on the distance towards the specific
 * {@link ResourceManagerEntry}.
 * 
 * @author P H - 03.09.2017
 *
 */
public interface GatheringSource {

	/**
	 * Function for adding a {@link ResourceManagerEntry} to a
	 * {@link GatheringSource}. This worker must be managed and therefore
	 * assigned a gathering source by the implementing Class.
	 * 
	 * @param worker
	 *            the worker that is going to be added towards the
	 *            {@link GatheringSource}.
	 * @return true if the worker was successfully added towards the
	 *         {@link GatheringSource}, false if either no space is available or
	 *         the worker could not be added towards the
	 *         {@link GatheringSource}.
	 */
	public boolean addWorker(ResourceManagerEntry worker);

	/**
	 * Function for removing a worker from the {@link GatheringSource} he was
	 * previously added to.
	 * 
	 * @param worker
	 *            the worker that is going to be removed from the
	 *            {@link GatheringSource}.
	 * @return true if the worker was successfully remove from the
	 *         {@link GatheringSource}, false if either the Unit was not added
	 *         towards it or the Unit was not successfully removed.
	 */
	public boolean removeWorker(ResourceManagerEntry worker);

	/**
	 * Function for testing if a {@link ResourceManagerEntry} was previously
	 * added towards the {@link GatheringSource}.
	 * 
	 * @param worker
	 *            the worker that the {@link GatheringSource} is being tested
	 *            for.
	 * @return true if the worker was added towards the {@link GatheringSource},
	 *         false if not.
	 */
	public boolean containsWorker(ResourceManagerEntry worker);

	/**
	 * Function for testing if {@link ResourceManagerEntry}s can be added
	 * towards a {@link GatheringSource}.
	 * 
	 * @return true if workers ({@link ResourceManagerEntry}s) can be added
	 *         towards the {@link GatheringSource}, false if not.
	 */
	public boolean isSpaceAvailable();

	/**
	 * Function for retrieving the number of workers that can be currently added
	 * towards a {@link GatheringSource} since a {@link GatheringSource} can
	 * only hold a limited number of workers simultaneously.
	 * 
	 * @return the number of workers that can be added towards the
	 *         {@link GatheringSource}.
	 */
	public int getAvailableSpace();

	/**
	 * Function for calculating the distance of the {@link GatheringSource} to a
	 * specific Position.
	 * 
	 * @param position
	 *            the Position the distance is calculated to.
	 * @return the distance of the {@link GatheringSource} towards the given
	 *         Position.
	 */
	public double getDistance(Position position);

	/**
	 * Function for actually retrieving the Unit / gathering spot that a
	 * previously added worker was assigned to internally.
	 * 
	 * @param worker
	 *            the {@link ResourceManagerEntry} representing the worker that
	 *            was previously added and whose gathering spot is now
	 *            requested.
	 * @return the Unit / gathering spot the {@link ResourceManagerEntry} was
	 *         assigned to or <b>null</b> if the Unit assigned Unit is no longer
	 *         available.
	 */
	public Unit getGatheringSource(ResourceManagerEntry worker);

}
