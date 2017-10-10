package informationStorage;

import bwapi.Unit;
import unitControlModule.stateFactories.actions.executableActions.FollowActionTerran_ScienceVessel;

// TODO: UML ADD
/**
 * ScienceVesselStorage.java --- Shared storage Class for the
 * {@link FollowActionTerran_ScienceVessel} action.
 * 
 * @author P H - 06.10.2017
 *
 */
public class ScienceVesselStorage {

	// Key: Science_Vessel.
	// Value: The Unit that it follows.
	private UnitMapper unitMapper = new UnitMapper();

	public ScienceVesselStorage() {

	}

	// -------------------- Functions

	/**
	 * Function for testing if a specific Unit is already being followed by a
	 * Science_Vessel.
	 * 
	 * @param unit
	 *            the Unit that is tested / targeted.
	 * @return true if a Science_Vessel is following that specific Unit, false
	 *         if not.
	 */
	public boolean isBeingFollowed(Unit unit) {
		return this.unitMapper.isBeingMapped(unit);
	}

	/**
	 * Function for testing if a Science_Vessel is following a Unit.
	 * 
	 * @param unit
	 *            the Science_Vessel that is tested.
	 * @return true if the Science_Vessel is following a Unit, false if not.
	 */
	public boolean isFollowing(Unit unit) {
		return this.unitMapper.isMapped(unit);
	}

	/**
	 * Function for assigning a Science_Vessel to follow another Unit.
	 * 
	 * @param vessel
	 *            the Science_Vessel that is going to follow the target Unit.
	 * @param target
	 *            the Unit that is going to be followed by the Science_Vessel.
	 * @return true if the assigning of the Science_Vessel to the target was a
	 *         success and no other Unit was mapped to it.
	 */
	public boolean followUnit(Unit vessel, Unit target) {
		return this.unitMapper.mapUnit(vessel, target);
	}

	/**
	 * Function for removing a mapping of a Science_Vessel.
	 * 
	 * @param vessel
	 *            the Science_Vessel whose mapping to a target is going to be
	 *            removed.
	 */
	public void unfollowUnit(Unit vessel) {
		this.unitMapper.unmapUnit(vessel);
	}

	// ------------------------------ Getter / Setter

	/**
	 * Function for retrieving the Unit that the provided Science_Vessel is
	 * currently following.
	 * 
	 * @param vessel
	 *            the Science_Vessel whose follow-target is requested.
	 * @return the Unit that the provided Science_Vessel is currently following
	 *         or null, if the provided Unit is currently not following any.
	 */
	public Unit getFollowedUnit(Unit vessel) {
		return this.unitMapper.getMappedUnit(vessel);
	}

	/**
	 * Function for retrieving the Science_Vessel that is following the provided
	 * Unit.
	 * 
	 * @param followedUnit
	 *            the Unit whose Science_Vessel follower is being requested.
	 * @return the Science_Vessel that is following the provided Unit or null,
	 *         if the Unit is not being followed by a Science_Vessel.
	 */
	public Unit getFollowingUnit(Unit followedUnit) {
		return this.unitMapper.getMappingUnit(followedUnit);
	}
}
