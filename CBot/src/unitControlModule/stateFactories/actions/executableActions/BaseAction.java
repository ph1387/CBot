package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashMap;

import bwapi.Game;
import bwapi.Position;
import core.Core;
import javaGOAP.GoapAction;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * BaseAction.java --- Superclass for all PlayerUnit actions.
 * 
 * @author P H - 09.02.2017
 *
 */
public abstract class BaseAction extends GoapAction {

	protected static HashMap<PlayerUnit, BaseAction> currentlyExecutingActions = new HashMap<>();

	protected boolean actionChangeTrigger = false;
	private IGoapUnit currentlyExecutingUnit;

	public BaseAction(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected boolean performAction(IGoapUnit goapUnit) {
		BaseAction storedAction = BaseAction.currentlyExecutingActions.get((PlayerUnit) goapUnit);

		// Check if the executing GoapAction has changed and if it did, enable a
		// trigger on which the subclass can react to.
		if (storedAction != null && storedAction.equals(this)) {
			this.actionChangeTrigger = false;
		} else {
			this.actionChangeTrigger = true;
		}

		// Store the executed action in the HashMap as well as the executing
		// Unit separately for having access to the Action and reset it when it
		// finishes.
		BaseAction.currentlyExecutingActions.put((PlayerUnit) goapUnit, this);
		this.currentlyExecutingUnit = goapUnit;

		return this.performSpecificAction(goapUnit);
	}

	protected abstract boolean performSpecificAction(IGoapUnit goapUnit);

	/**
	 * Function used for resetting the entry in the currentlyExecutingActions
	 * HashMap. This function has to be called when the GoapAction finishes so
	 * that the actionTrigger is going to be enabled in the next iteration.
	 */
	protected void resetStoredAction() {
		BaseAction.currentlyExecutingActions.put((PlayerUnit) this.currentlyExecutingUnit, null);
	}

	/**
	 * Function for testing if a Position is inside the map.
	 * 
	 * @param p
	 *            the Position that is going to be checked.
	 * @return true or false depending if the Position is inside the map or not.
	 */
	protected boolean isInsideMap(Position p) {
		Game game = Core.getInstance().getGame();

		return (p.getX() < (game.mapWidth() * Core.getInstance().getTileSize()) || p.getX() >= 0
				|| p.getY() < (game.mapHeight() * Core.getInstance().getTileSize()) || p.getY() >= 0);
	}

	// TODO: UML
	@Override
	protected void reset() {
		this.resetStoredAction();
		this.resetSpecific();
	}

	// TODO: UML
	/**
	 * Gets called when the Action is finished or removed from the FSM Stack
	 * after resetting the currentlyExecutingActions entry in the corresponding
	 * HashMap.
	 */
	protected abstract void resetSpecific();

	// ------------------------------ Getter / Setter

	public void setTarget(Object target) {
		this.target = target;
	}
}
