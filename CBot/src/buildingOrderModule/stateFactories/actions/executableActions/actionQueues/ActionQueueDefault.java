package buildingOrderModule.stateFactories.actions.executableActions.actionQueues;

import java.util.ArrayList;
import java.util.List;

import buildingOrderModule.stateFactories.actions.executableActions.ManagerBaseAction;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;

/**
 * ActionQueueDefault.java --- Superclass for action queues. Only provides the
 * most basic implementation of the required functions.
 * 
 * @author P H - 30.04.2017
 *
 */
public abstract class ActionQueueDefault extends ManagerBaseAction {

	protected List<ManagerBaseAction> actionQueue = new ArrayList<>();
	protected int index = 0;

	public ActionQueueDefault(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "buildOrderAllowed", false));
		this.addEffect(new GoapState(0, "startingBuildOrderNeeded", false));
		this.addPrecondition(new GoapState(0, "buildOrderAllowed", true));
		this.addPrecondition(new GoapState(0, "startingBuildOrderNeeded", true));
	}

	// -------------------- Functions

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		return this.index < this.actionQueue.size();
	}

	@Override
	public boolean performAction(IGoapUnit goapUnit) {
		if (this.checkProceduralPrecondition(goapUnit)) {
			this.performSpecificAction(goapUnit);
		}
		return true;
	}

	@Override
	protected void performSpecificAction(IGoapUnit goapUnit) {
		// Used for resetting finished actions and incrementing the index which
		// in return shows if the queue is finished.
		if (this.actionQueue.get(this.index).isDone(goapUnit)) {
			this.actionQueue.get(this.index).reset();
			this.index++;
		}

		// Check if any elements remain that must be cycled through.
		if (this.checkProceduralPrecondition(goapUnit)) {
			if (this.actionQueue.get(this.index).checkProceduralPrecondition(goapUnit)) {
				this.actionQueue.get(this.index).performAction(goapUnit);
			}
		}
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	public boolean isDone(IGoapUnit goapUnit) {
		return this.index >= this.actionQueue.size();
	}

	@Override
	public void reset() {
		super.reset();

		this.index = 0;
	}
	
	// ------------------------------ Getter / Setter
	
	// TODO: UML ADD
	public List<ManagerBaseAction> getActionQueue() {
		return actionQueue;
	}
}
