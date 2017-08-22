package unitControlModule.stateFactories.actions.executableActions.grouping;

import javaGOAP.IGoapUnit;

/**
 * GroupableAction.java --- An Interface that each action which can be performed
 * as a group must implement.
 * 
 * @author P H - 03.08.2017
 *
 */
public interface GroupableAction {

	/**
	 * Function for checking if the action can be performed as a group.
	 * 
	 * @return true if the action can be performed as a group. False if not.
	 */
	public boolean canPerformGrouped();

	/**
	 * Function for actually performing the action as a group. This function is
	 * only called when mirroring the leader's action towards the other members
	 * of it's group and is not called for the leader himself.
	 * 
	 * @param groupLeader
	 *            reference to the leader of the group.
	 * @param groupMember
	 *            the current Unit that is mirroring the leader's action.
	 * @return true if the action was performed successfully, false if not.
	 */
	public boolean performGrouped(IGoapUnit groupLeader, IGoapUnit groupMember);

	// TODO: UML ADD
	/**
	 * Function for defining the maximum number of members of the group.
	 * 
	 * @return the maximum number of members the group is allowed to have.
	 */
	public int defineMaxGroupSize();

	// TODO: UML ADD
	/**
	 * Function for defining the max range for a Unit trying to join a leader's
	 * group.
	 * 
	 * @return the max distance in TilePositions that a leader is allowed to be
	 *         away from the Unit.
	 */
	public int defineMaxLeaderTileDistance();
}
