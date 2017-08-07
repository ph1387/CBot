package unitControlModule.stateFactories.actions.executableActions.grouping;

import java.util.ArrayList;
import java.util.List;

import javaGOAP.IGoapUnit;

/**
 * GroupAction.java --- A Class used for representing a group. Each instance
 * contains the type of action the group is performing as well as it's members
 * and their associated action references.
 * 
 * @author P H - 03.08.2017
 *
 */
public class GroupAction {

	// The type of action that the group is storing.
	private Class<?> type;
	private List<IGoapUnit> members = new ArrayList<>();
	private List<GroupableAction> actions = new ArrayList<>();
	private int maxGroupSize = 5;
	private int currentGroupSize = 0;

	public GroupAction(Class<?> type) {
		this.type = type;
	}

	// -------------------- Functions

	/**
	 * Function for actually performing the leader's stored action. The stored
	 * action reference of the leader's action is used for performing N-1
	 * actions as a group. <br>
	 * <b>Note:</b></br>
	 * The leader does <b>NOT</b> perform the action itself again. This
	 * functionality must be provided elsewhere since this function is only for
	 * mirroring it towards the other members of the group!</br>
	 * Any stored / calculated values can therefore be used by all members of
	 * the group. This enables the ability of them acting upon one single shared
	 * information pool. The leader as well as the currently performing Unit are
	 * given as references for the members to react on as well as actually
	 * performing their actions.
	 * 
	 * @return true if all other members aside the leader were able to perform
	 *         the mirrored action successfully.
	 */
	public boolean mirrorLeaderAction() {
		boolean success = true;

		// Perform the action of the leader for each registered member. The
		// leader does NOT perform the action himself. Only the other members of
		// the group do that!
		for (IGoapUnit goapUnit : members) {
			if (goapUnit != this.getLeader() && !this.actions.get(0).performGrouped(this.getLeader(), goapUnit)) {
				success = false;
			}
		}

		return success;
	}

	/**
	 * Function for checking if the group has not reached it's maximum available
	 * capacity.
	 * 
	 * @return true if space is availabel and a Unti could be added towards the
	 *         group or false is the maximum capacity of the group has been
	 *         reached.
	 */
	public boolean isSpaceAvailable() {
		return this.currentGroupSize < this.maxGroupSize;
	}

	/**
	 * Function for testing if the group is empty.
	 * 
	 * @return true if the group is empty. False if there are members inside the
	 *         group.
	 */
	public boolean isEmtpy() {
		return this.currentGroupSize == 0;
	}

	/**
	 * Function for adding a new member to the group. This is only possible if
	 * the group has not already reached it's member limit.
	 * 
	 * @param goapUnit
	 *            the Unit that is going to be added towards the group.
	 * @param action
	 *            the action reference of the action of the Unit. Needed for
	 *            execution if the Unit becomes leader of the group and
	 *            therefore mirrors it's action to other members.
	 * @return true if the Unit was successfully added towards the group. False
	 *         if the Unit was not added.
	 */
	public boolean addMember(IGoapUnit goapUnit, GroupableAction action) {
		boolean success = false;

		if (this.isSpaceAvailable()) {
			this.members.add(goapUnit);
			this.actions.add(action);

			this.currentGroupSize++;
		}

		return success;
	}

	/**
	 * Function for removing a member of the group from it.
	 * 
	 * @param goapUnit
	 *            the Unit that is going to be removed from the group.
	 * @return true if the Unit was successfully removed from the group. False
	 *         if either the Unit was not removed or if the Unit was not found
	 *         among the members of the group.
	 */
	public boolean removeMember(IGoapUnit goapUnit) {
		int indexToRemove = this.members.indexOf(goapUnit);
		boolean success = true;

		// Remove the found index of the members as well as the actions since
		// this is the one that was registered along side with it.
		if (indexToRemove != -1) {
			this.members.remove(indexToRemove);
			this.actions.remove(indexToRemove);

			this.currentGroupSize--;
		} else {
			success = false;
		}

		return success;
	}

	// ------------------------------ Getter / Setter

	public Class<?> getType() {
		return type;
	}

	public List<IGoapUnit> getMembers() {
		return members;
	}

	public IGoapUnit getLeader() {
		return this.members.get(0);
	}
}
