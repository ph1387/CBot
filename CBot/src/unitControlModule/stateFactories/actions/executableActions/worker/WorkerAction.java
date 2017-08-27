package unitControlModule.stateFactories.actions.executableActions.worker;

import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.stateFactories.actions.executableActions.BaseAction;

// TODO: UML ADD
/**
 * WorkerAction.java --- Superclass for shared information between different
 * worker actions (I.e.: Grouping).
 * 
 * @author P H - 22.08.2017
 *
 */
public abstract class WorkerAction extends BaseAction {

	/**
	 * @param target
	 *            type: Depends on the Subclass.
	 */
	public WorkerAction(Object target) {
		super(target);
		
		this.addPrecondition(new GoapState(0, "canMove", true));
	}

	// -------------------- Functions

	@Override
	protected boolean isInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected boolean requiresInRange(IGoapUnit goapUnit) {
		return false;
	}
	
	// -------------------- Group

	@Override
	public boolean canPerformGrouped() {
		// All basic worker actions like gathering and constructing buildings
		// are performed separately.
		return false;
	}

	@Override
	public boolean performGrouped(IGoapUnit groupLeader, IGoapUnit groupMember) {
		return false;
	}

	@Override
	public int defineMaxGroupSize() {
		return 0;
	}

	@Override
	public int defineMaxLeaderTileDistance() {
		return 0;
	}

}
