package buildingOrderModule.scoringDirector;

// TODO: UML ADD
/**
 * GameState.java --- Class for representing a state in the game with an
 * associated score attached to it.
 * 
 * @author P H - 16.07.2017
 *
 */
public class GameState {

	// States that can be used by other actions for generating their individual score.
	public static final GameState Resource_Focused = new GameState();
	public static final GameState Military_Focused = new GameState();
	public static final GameState Expansion_Focused = new GameState();
	
	public static final GameState Technology_Focused = new GameState();
	public static final GameState Upgrade_Focused = new GameState();
	
	public static final GameState Building_Units = new GameState();
	public static final GameState Worker_Units = new GameState();
	public static final GameState Combat_Units = new GameState();
	
	public static final GameState Cheap_Units = new GameState();
	public static final GameState Expensive_Units = new GameState();
	public static final GameState Mineral_Units = new GameState();
	public static final GameState Gas_Units = new GameState();
	public static final GameState Flying_Units = new GameState();
	
	public static final GameState Bio_Units = new GameState();
	
	// The current multiplier this state holds.
	private double currentMultiplier = 0.; 
	
	private GameState() {
		
	}
	
	// -------------------- Functions

	// ------------------------------ Getter / Setter

	public double getCurrentMultiplier() {
		return currentMultiplier;
	}

	public void setCurrentMultiplier(double multiplier) {
		this.currentMultiplier = multiplier;
	}
}
