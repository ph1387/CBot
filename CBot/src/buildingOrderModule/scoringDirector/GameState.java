package buildingOrderModule.scoringDirector;

// TODO: UML ADD
/**
 * GameState.java --- Class for representing a state in the game with an
 * associated score attached to it. Each state has a multiplier attached to it
 * that resembles the importance / score of it. The higher the multiplier the
 * better.
 * 
 * @author P H - 16.07.2017
 *
 */
public abstract class GameState {

	// States that can be used by other actions for generating their individual
	// score:
	public static final GameState Resource_Focused = new GameStateResource_Focused();
	public static final GameState Military_Focused = new GameStateMilitary_Focused();
	public static final GameState Expansion_Focused = new GameStateExpansion_Focused();

	public static final GameState Technology_Focused = new GameStateTechnology_Focused();
	public static final GameState Upgrade_Focused = new GameStateUpgrade_Focused();

	public static final GameState Building_Units = new GameStateBuilding_Units();
	public static final GameState Worker_Units = new GameStateWorker_Units();
	public static final GameState Combat_Units = new GameStateCombat_Units();

	public static final GameState Cheap_Units = new GameStateCheap_Units();
	public static final GameState Expensive_Units = new GameStateExpensive_Units();
	public static final GameState Mineral_Units = new GameStateMineral_Units();
	public static final GameState Gas_Units = new GameStateGas_Units();
	
	public static final GameState Flying_Units = new GameStateFlying_Units();
	public static final GameState Bio_Units = new GameStateBio_Units();
	public static final GameState Support_Units = new GameStateSupport_Units();
	public static final GameState Healer_Units = new GameStateHealer_Units();

	// The current multiplier this state holds.
	private double currentMultiplier = 0.;

	public GameState() {

	}

	// -------------------- Functions

	/**
	 * Function for updating the current multiplier of the GameState.
	 */
	public void updateMultiplier() {
		this.currentMultiplier = this.generateMultiplier();
	}

	/**
	 * Function for generating a new multiplier for the GameState that
	 * represents the state of the game in the are that the GameState is
	 * responsible for.
	 * 
	 * @return a multiplier that resembles the state of the game.
	 */
	protected abstract double generateMultiplier();

	// ------------------------------ Getter / Setter

	public double getCurrentMultiplier() {
		return currentMultiplier;
	}

}
