package buildingOrderModule.stateFactories.actions.executableActions;

import java.util.HashSet;

import buildingOrderModule.scoringDirector.GameState;
import buildingOrderModule.scoringDirector.ScoringAction;
import buildingOrderModule.simulator.ActionType;
import javaGOAP.IGoapUnit;

// TODO: UML ADD INTERFACES
/**
 * ManagerBaseAction.java --- Superclass for BuildActionManager actions.
 * 
 * @author P H - 28.04.2017
 *
 */
public abstract class ManagerBaseAction extends BaseAction implements ActionType, ScoringAction {

	protected int iterationCount = 0;

	// Score used by the Simulator to perform the simulations.
	// -> ONLY access with the lock below!
	private int score = 0;
	// Locking Object for ensuring the Thread compatibility.
	private Object lockScore = new Object();
	// Used GameStates for representing the effects the Action has.
	private HashSet<GameState> usedGameStates = new HashSet<>();
	// Locking Object for ensuring the Thread compatibility.
	private Object lockGameStates = new Object();

	/**
	 * @param target
	 *            type: Integer, the amount of times the Unit, Upgrade etc. must
	 *            be build.
	 */
	public ManagerBaseAction(Object target) {
		super(target);
	}

	// -------------------- Functions

	/**
	 * Function for checking a more specific precondition of any subclass.
	 * 
	 * @param goapUnit
	 *            the executing GoapUnit.
	 * @return true or false depending if the specific preconditions are met.
	 */
	protected abstract boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit);

	@Override
	public boolean performAction(IGoapUnit goapUnit) {
		if (this.checkProceduralPrecondition(goapUnit)) {
			this.performSpecificAction(goapUnit);
			this.iterationCount++;
		}
		return true;
	}

	protected abstract void performSpecificAction(IGoapUnit goapUnit);

	@Override
	public boolean isDone(IGoapUnit goapUnit) {
		return this.iterationCount >= (int) this.target;
	}

	@Override
	public void reset() {
		this.iterationCount = 0;
	}

	// TODO: UML ADD
	/**
	 * Function for adding a GameState to the Collection of used GameStates
	 * representing the Action.
	 * 
	 * @param gameState
	 *            the GameState that is going to be added.
	 */
	protected void addToGameStates(GameState gameState) {
		synchronized (this.lockGameStates) {
			this.usedGameStates.add(gameState);
		}
	}

	// TODO: UML ADD
	/**
	 * Function for removing a GameState from the Collection of used GameStates
	 * representing the Action.
	 * 
	 * @param gameState
	 *            the GameState that is going to be removed.
	 */
	protected void removeFromGameStates(GameState gameState) {
		synchronized (this.lockGameStates) {
			this.usedGameStates.remove(gameState);
		}
	}

	// ------------------------------ ActionType

	// TODO: UML ADD
	@Override
	public int defineScore() {
		synchronized (this.lockScore) {
			return this.score;
		}
	}

	// ------------------------------ ScoringAction

	// TODO: UML ADD
	@Override
	public void setScore(int score) {
		synchronized (this.lockScore) {
			this.score = score;

			// TODO: WIP REMOVE
			System.out.println("Generated Score: " + score + " | " + this.getClass().getSimpleName());
		}
	}

	// TODO: UML ADD
	@Override
	public HashSet<GameState> defineUsedGameStates() {
		synchronized (this.lockGameStates) {
			return this.usedGameStates;
		}
	}

	// ------------------------------ Getter / Setter

}
