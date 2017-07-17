package buildingOrderModule.stateFactories.actions.executableActions.actionQueues;

import java.util.Collection;

import buildingOrderModule.stateFactories.actions.executableActions.ManagerBaseAction;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;

// TODO: UML ADD
/**
 * ActionQueueSimulationResults.java --- ActionQueue designed for enabling the
 * execution of simulation results within the designated BuildActionManager.
 * </br>
 * <b>Notice:</b> </br>
 * The Actions added towards the Queue must be managed and updated by a
 * designated Updater.
 * 
 * @author P H - 14.07.2017
 *
 */
public class ActionQueueSimulationResults extends ActionQueueDefault {

	private boolean changesOccurred = false;

	/**
	 * @param target
	 *            type: Irrelevant, because the whole Queue will be cycled
	 *            through.
	 */
	public ActionQueueSimulationResults(Object target) {
		super(target);

		this.addPrecondition(new GoapState(0, "simulationAllowed", true));
	}

	// -------------------- Functions

	@Override
	protected void performSpecificAction(IGoapUnit goapUnit) {
		// Used for resetting finished actions and incrementing the index which
		// in return shows if the queue is finished.
		if (this.actionQueue.get(this.index).isDone(goapUnit)) {
			this.actionQueue.get(this.index).reset();
			this.index++;

			// Since the index was incremented, a change occurred.
			this.changesOccurred = true;
		}

		// Check if any elements remain that must be cycled through.
		if (this.checkProceduralPrecondition(goapUnit)) {
			if (this.actionQueue.get(this.index).checkProceduralPrecondition(goapUnit)) {
				this.actionQueue.get(this.index).performAction(goapUnit);
			}
		}
	}

	@Override
	public void reset() {
		// Cycle through the stored Actions in the simulation and remove the
		// finished ones until the index is 0 again.
		while (this.index > 0) {
			this.actionQueue.remove(0);

			this.index--;
		}
	}

	/**
	 * Function for adding another Collection to the already existing action
	 * Queue. This function calls the own implementation of the {@link #reset()}
	 * function and therefore removes any finished Actions from the Queue and
	 * resets the index to 0.
	 * 
	 * @param actionQueue
	 *            the Collection of ManagerBaseActions that is going to be added
	 *            towards the action Queue.
	 */
	public void addToActionQueue(Collection<ManagerBaseAction> actionQueue) {
		this.reset();

		this.actionQueue.addAll(actionQueue);
	}

	/**
	 * Function for checking if either the index changed (+).
	 * 
	 * @return true or false depending if the index changed.
	 */
	public boolean didChangesOccurr() {
		return this.changesOccurred;
	}

	/**
	 * Function for resetting the flag that represents the index changes.
	 */
	public void resetChangesFlag() {
		this.changesOccurred = false;
	}

	// ------------------------------ Getter / Setter

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
