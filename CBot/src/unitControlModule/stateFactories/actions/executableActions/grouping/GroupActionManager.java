package unitControlModule.stateFactories.actions.executableActions.grouping;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.TilePosition;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * GroupActionManager.java --- The manager that handles all ongoing groups of
 * actions. New members can be added and are then either put into a new group as
 * a leader or added to an existing one.
 * 
 * @author P H - 03.08.2017
 *
 */
public class GroupActionManager {

	private HashMap<Class<?>, HashSet<GroupAction>> storedGroups = new HashMap<>();
	private HashMap<IGoapUnit, GroupAction> mappedUnits = new HashMap<>();

	// -------------------- Functions

	/**
	 * Function for testing if the provided Unit is a leader in it's current
	 * group.
	 * 
	 * @param goapUnit
	 *            the Unit that is going to be tested.
	 * @return true if the Unit is the leader of it's current group or false if
	 *         it is either not mapped to one or is no leader.
	 */
	public boolean isLeader(IGoapUnit goapUnit) {
		return this.mappedUnits.get(goapUnit).getLeader() == goapUnit;
	}

	/**
	 * Function for testing if a Unit is already mapped to a group.
	 * 
	 * @param goapUnit
	 *            the Unit that is going ot be tested.
	 * @return true if the Unit is part of a group, false if it is not.
	 */
	public boolean isGrouped(IGoapUnit goapUnit) {
		return this.mappedUnits.containsKey(goapUnit);
	}

	/**
	 * Function for performing the grouped action of the leader. The other
	 * members of the group mimic the action provided by the leader. This is
	 * only executed if the given Unit is the leader of it's group.
	 * 
	 * @param goapUnit
	 *            the Unit (Must be leader) whose action is executed in a
	 *            grouped way.
	 * @return true if the action was successfully executed in a group and false
	 *         if the provided Unit was either no leader or mirroring the
	 *         leader's action failed.
	 */
	public boolean performGroupLeaderAction(IGoapUnit goapUnit) {
		boolean success = false;

		// Only perform the actions if the calling Unit is the leader.
		if (this.isGrouped(goapUnit) && this.mappedUnits.get(goapUnit).getLeader() == goapUnit) {
			success = this.mappedUnits.get(goapUnit).mirrorLeaderAction();
		}

		return success;
	}

	/**
	 * Function for adding a new Unit to a group. This may either be a new one
	 * with the provided Unit as leader, if no valid other group in the Unit's
	 * vicinity is found, or an existing one to which the Unit will be added as
	 * a new member.
	 * 
	 * @param type
	 *            the type / Class of action to whose group the Unit is going to
	 *            be added to.
	 * @param goapUnit
	 *            the Unit that is going to be added towards a group.
	 * @param action
	 *            the reference to the action of the Unit itself. This is needed
	 *            since the Unit might become the leader of the group and
	 *            therefore must provide a instance of an action for mirroring.
	 * @return true if the Unit was added towards a group, otherwise false.
	 */
	public boolean addToGroupAction(Class<? extends GroupableAction> type, IGoapUnit goapUnit, GroupableAction action) {
		boolean success = false;

		// The Unit must not be already grouped and the action must be
		// executable as a group.
		if (!this.isGrouped(goapUnit) && action.canPerformGrouped()) {
			// The group the Unit will be added to.
			GroupAction chosenGroup = null;

			// Add a HashSet for the GroupActions to the HashMap if not already
			// present.
			if (!this.storedGroups.containsKey(type)) {
				this.storedGroups.put(type, new HashSet<GroupAction>());
			}

			// Find the potential groups that the Unit can be added to.
			HashSet<GroupAction> potentialGroups = this.extractViableGroups(type, goapUnit,
					action.defineMaxLeaderTileDistance());

			// If no remain create a new group just for the Unit itself.
			if (potentialGroups.isEmpty()) {
				chosenGroup = new GroupAction(type, action);
				this.storedGroups.get(type).add(chosenGroup);
			}
			// Find the "best" / closest one if there are more than one group
			// left.
			else {
				chosenGroup = this.extractBestGroup(potentialGroups, goapUnit);
			}

			// Add the Unit to the group and mapped entries.
			success = chosenGroup.addMember(goapUnit, action);
			this.mappedUnits.put(goapUnit, chosenGroup);
		}

		return success;
	}

	/**
	 * Function for extracting groups that the Unit is able to join. The groups
	 * that are going to be looked at are the ones stored in this instance. From
	 * these a certain subset is chosen (Distance, space etc.).
	 * 
	 * @param type
	 *            the type / Class of action that the Unit is going to join.
	 * @param goapUnit
	 *            the Unit that is going to join a group.
	 * @param maxLeaderTileDistance
	 *            the maximum distance in TilePositions that a leader is allowed
	 *            to be away from the Unit.
	 * @return a HashSet of all groups that the Unit is able to join.
	 */
	private HashSet<GroupAction> extractViableGroups(Class<?> type, IGoapUnit goapUnit, int maxLeaderTileDistance) {
		HashSet<GroupAction> potentialGroups = new HashSet<>();

		// Get all groups with the same action and extract the ones that are not
		// already full and in acceptable range.
		for (GroupAction groupAction : this.storedGroups.get(type)) {
			if (groupAction.isSpaceAvailable()
					&& this.isLeaderInTileRange(goapUnit, groupAction, maxLeaderTileDistance)) {
				potentialGroups.add(groupAction);
			}
		}

		return potentialGroups;
	}

	/**
	 * Function for checking if a group's leader is in an acceptable, predefined
	 * tile range around a provided Unit.
	 * 
	 * @param goapUnit
	 *            the Unit that is going to be checked.
	 * @param groupAction
	 *            the group whose leader must be in a certain tile range around
	 *            the provided Unit.
	 * @param maxLeaderTileDistance
	 *            the maximum distance in TilePositions that a leader is allowed
	 *            to be away from the Unit.
	 * @return true if the group's leader is in an acceptable tile range, false
	 *         if it is not.
	 */
	private boolean isLeaderInTileRange(IGoapUnit goapUnit, GroupAction groupAction, int maxLeaderTileDistance) {
		TilePosition unitTilePosition = ((PlayerUnit) goapUnit).getUnit().getTilePosition();
		TilePosition leaderTilePosition = ((PlayerUnit) groupAction.getLeader()).getUnit().getTilePosition();

		// X and Y coordinates are in the acceptable range.
		return (Math.abs(unitTilePosition.getX() - leaderTilePosition.getX()) <= maxLeaderTileDistance)
				&& (Math.abs(unitTilePosition.getY() - leaderTilePosition.getY()) <= maxLeaderTileDistance);
	}

	/**
	 * Function for extracting the best group from a HashSet of potential
	 * groups. The "best" one is determined by the leader's range towards a
	 * provided Unit.
	 * 
	 * @param potentialGroups
	 *            the HashSet of groups whose leader distances are going to be
	 *            measured.
	 * @param goapUnit
	 *            the Unit to which the group leader distances are going to be
	 *            measured to.
	 * @return the GroupAction that contains the leader with the smallest
	 *         distance towards the provided Unit.
	 */
	private GroupAction extractBestGroup(HashSet<GroupAction> potentialGroups, IGoapUnit goapUnit) {
		GroupAction currentlyBestGroup = null;

		// TODO: Possible Change: Remove the PlayerUnit dependency.
		// Extract the group whose leader is the closest.
		for (GroupAction groupAction : potentialGroups) {
			if (currentlyBestGroup == null || ((PlayerUnit) currentlyBestGroup.getLeader()).getUnit()
					.getDistance(((PlayerUnit) goapUnit).getUnit()) > ((PlayerUnit) groupAction.getLeader()).getUnit()
							.getDistance(((PlayerUnit) goapUnit).getUnit())) {
				currentlyBestGroup = groupAction;
			}
		}

		return currentlyBestGroup;
	}

	/**
	 * Function for removing a Unit from it's current group. This also removes
	 * the reference to the GroupAction itself if the Unit that is going to be
	 * removed was the last one remaining inside it.
	 * 
	 * @param goapUnit
	 *            the Unit that is going to be removed from it's current group.
	 * @return true if the Unit was successfully removed, false if either the
	 *         Unit was in no group or the removing failed.
	 */
	public boolean removeFromGroupAction(IGoapUnit goapUnit) {
		boolean success = false;
		// Get the group the Unit is currently in.
		GroupAction groupAction = this.mappedUnits.get(goapUnit);

		// Remove the Unit from the group and mapped entries.
		if (groupAction != null) {
			success = groupAction.removeMember(goapUnit);

			// Remove the reference to the group since no members remain.
			if (groupAction.isEmtpy()) {
				this.storedGroups.get(groupAction.getType()).remove(groupAction);
			}
		}

		// Remove the stored entry for the Unit.
		this.mappedUnits.remove(goapUnit);

		return success;
	}

	// ------------------------------ Getter / Setter

}
