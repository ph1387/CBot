package workerManagerConstructionJobDistribution;

import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

/**
 * IConstrucionInformation.java --- Interface for the worker to interact with an
 * assigned {@link ConstructionJob}.
 * 
 * @author P H - 05.09.2017
 *
 */
public interface IConstrucionInformation {

	/**
	 * Function for retrieving the building that is actually constructed by the
	 * worker.
	 * 
	 * @return the Unit reference of the building being created. Therefore this
	 *         function returns null until the construction of the building
	 *         started. After that the reference to the building is returned.
	 */
	public Unit getBuilding();

	/**
	 * Function for retrieving the UnitType of the building that the worker is
	 * going to be constructing.
	 * 
	 * @return the UnitType of the building that the worker is going to be
	 *         constructing.
	 */
	public UnitType getUnitType();

	/**
	 * Function for retrieving the TilePosition the building is going to be
	 * constructed at.
	 * 
	 * @return the TilePosition the worker must construct the building.
	 */
	public TilePosition getTilePosition();

	/**
	 * Function for retrieving information about the state of the
	 * {@link ConstructionJob}. Returns true if the construction actually
	 * started and the {@link ConstructionJob} found a matching Unit on the map.
	 * 
	 * @return true if the construction of the building started and a Unit with
	 *         matching properties appeared on the map, false if not.
	 */
	public boolean constructionStarted();

	/**
	 * Function for retrieving information about the state of the
	 * {@link ConstructionJob}. Returns true if the construction of the building
	 * is finished and the worker is not longer actively working on the building
	 * Unit.
	 * 
	 * @return true if the construction of the building is finished, false if
	 *         not.
	 */
	public boolean isFinished();

	/**
	 * Function for passing an update cycle to the {@link ConstructionJob} in
	 * which the information of it is / can be updated.
	 */
	public void update();

}
