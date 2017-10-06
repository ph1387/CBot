package informationStorage;

import java.util.HashMap;

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
	private HashMap<Unit, Unit> vesselsFollowingUnits = new HashMap<>();

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
		return this.vesselsFollowingUnits.containsValue(unit);
	}

	/**
	 * Function for testing if a Science_Vessel is following a Unit.
	 * 
	 * @param unit
	 *            the Science_Vessel that is tested.
	 * @return true if the Science_Vessel is following a Unit, false if not.
	 */
	public boolean isFollowing(Unit unit) {
		return this.vesselsFollowingUnits.containsKey(unit);
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
		return !this.vesselsFollowingUnits.containsKey(vessel)
				&& this.vesselsFollowingUnits.put(vessel, target) == null;
	}

	/**
	 * Function for removing a mapping of a Science_Vessel.
	 * 
	 * @param vessel
	 *            the Science_Vessel whose mapping to a target is going to be
	 *            removed.
	 */
	public void unfollowUnit(Unit vessel) {
		this.vesselsFollowingUnits.remove(vessel);
	}

	// ------------------------------ Getter / Setter

}
