package buildingOrderModule.stateFactories.actions.executableActions;

import java.util.HashSet;

import buildingOrderModule.scoringDirector.GameState;
import buildingOrderModule.scoringDirector.ScoringAction;
import buildingOrderModule.simulator.ActionType;
import javaGOAP.IGoapUnit;

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

	@Override
	public boolean performAction(IGoapUnit goapUnit) {
		if (this.checkProceduralPrecondition(goapUnit)) {
			this.performSpecificAction(goapUnit);
			this.iterationCount++;
		}
		return true;
	}

	/**
	 * The actual performance of the action.
	 * 
	 * @param goapUnit
	 *            the executing Unit.
	 * @return true if the action was successful, false if it failed.
	 */
	protected abstract void performSpecificAction(IGoapUnit goapUnit);

	@Override
	public boolean isDone(IGoapUnit goapUnit) {
		return this.iterationCount >= (int) this.target;
	}

	@Override
	public void reset() {
		this.iterationCount = 0;
	}

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

	@Override
	public int defineScore() {
		synchronized (this.lockScore) {
			return this.score;
		}
	}

	// ------------------------------ ScoringAction

	@Override
	public void setScore(int score) {
		synchronized (this.lockScore) {
			this.score = score;
		}
	}

	@Override
	public HashSet<GameState> defineUsedGameStates() {
		synchronized (this.lockGameStates) {
			return this.usedGameStates;
		}
	}

	// ------------------------------ Getter / Setter

}
