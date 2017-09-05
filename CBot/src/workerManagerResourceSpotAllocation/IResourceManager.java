package workerManagerResourceSpotAllocation;

import bwapi.Unit;

/**
 * IResourceManager.java --- Interface that each resource manager must implement
 * in order to be used in the {@link WorkerManagerResourceSpotAllocation}
 * instance.
 * 
 * @author P H - 03.09.2017
 *
 */
public interface IResourceManager {

	/**
	 * Function defining if space is available in the resource manager or if no
	 * more Units can be added towards it.
	 * 
	 * @return true if Units can be added towards the resource manager or false
	 *         if not space is available.
	 */
	public boolean isSpaceAvailable();

	/**
	 * Function for retrieving the number of assignable space that the manager
	 * currently has.
	 * 
	 * @return the number of Units that can be added towards the resource
	 *         manager.
	 */
	public int getAvailableSpace();

	/**
	 * Function for adding a {@link ResourceManagerEntry} to a resource manager.
	 * The Unit is then assigned a gathering spot internally.
	 * 
	 * @param entry
	 *            the Unit that is going to be added towards a resource manager.
	 * @return true if the Unit was assigned towards a resource manager, false
	 *         if not.
	 */
	public boolean addToManager(ResourceManagerEntry entry);

	/**
	 * Function for removing a {@link ResourceManagerEntry} from a resource
	 * manager.
	 * 
	 * @param entry
	 *            the Unit that is going to be removed from a resource manager.
	 * @return true if the Unit was successfully removed, false if it was not
	 *         removed or not found.
	 */
	public boolean removeFromManager(ResourceManagerEntry entry);

	/**
	 * Function for retrieving the internally assigned gathering source of the
	 * specified {@link ResourceManagerEntry}.
	 * 
	 * @param entry
	 *            the Unit that was previously added towards a resource manager
	 *            and therefore must be assigned towards a gathering spot.
	 * @return the gathering spot (Unit) that the {@link ResourceManagerEntry}
	 *         was assigned to internally ny the resource manager.
	 */
	public Unit getGatheringSource(ResourceManagerEntry entry);

}
