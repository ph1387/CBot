package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import bwapi.UnitType;
import core.Core;

/**
 * GameState.java --- Class for representing a state in the game with an
 * associated score attached to it. Each state has a score attached to it that
 * resembles the importance of it. The higher the score the better.
 * 
 * @author P H - 16.07.2017
 *
 */
public abstract class GameState {

	// States that can be used by other actions for generating their individual
	// score:
	public static final GameState Expansion_Focused = new GameStateFocused_Expansion();
	public static final GameState Technology_Focused = new GameStateFocused_Technology();
	public static final GameState Upgrade_Focused = new GameStateFocused_Upgrade();

	public static final GameState Refinery_Units = new GameStateFocused_Refinery();
	public static final GameState Building_Units = new GameStateUnits_Building();
	public static final GameState Worker_Units = new GameStateUnits_Worker();
	public static final GameState Combat_Units = new GameStateUnits_Combat();

	public static final GameState Cheap_Units = new GameStateUnits_Cheap();
	public static final GameState Expensive_Units = new GameStateUnits_Expensive();
	public static final GameState Mineral_Units = new GameStateUnits_Mineral();
	public static final GameState Gas_Units = new GameStateUnits_Gas();

	public static final GameState Flying_Units = new GameStateUnits_Flying();
	public static final GameState Bio_Units = new GameStateUnits_Bio();
	public static final GameState Machine_Units = new GameStateUnits_Machines();
	public static final GameState Support_Units = new GameStateUnits_Support();
	public static final GameState Healer_Units = new GameStateUnits_Healer();

	// The GameStates that rely on free training facilities: (Positive meaning!
	// => Higher score + more dividers!)
	public static final GameState FreeTrainingFacility_Center = new GameStateUnits_TrainingFacilitiesFree(
			Core.getInstance().getPlayer().getRace().getCenter());
	public static final GameState FreeTrainingFacility_Terran_Barracks = new GameStateUnits_TrainingFacilitiesFree(
			UnitType.Terran_Barracks);
	public static final GameState FreeTrainingFacility_Terran_CommandCenter = new GameStateUnits_TrainingFacilitiesFree(
			UnitType.Terran_Command_Center);
	public static final GameState FreeTrainingFacility_Terran_Factory = new GameStateUnits_TrainingFacilitiesFree(
			UnitType.Terran_Factory);
	public static final GameState FreeTrainingFacility_Terran_Starport = new GameStateUnits_TrainingFacilitiesFree(
			UnitType.Terran_Starport);

	// The GameStates that rely on idling training facilities: (Negative
	// meaning! => More dividers are added!)
	public static final GameState IdleTrainingFacility_Center = new GameStateUnits_TrainingFacilitiesIdle(
			Core.getInstance().getPlayer().getRace().getCenter());
	public static final GameState IdleTrainingFacility_Terran_Barracks = new GameStateUnits_TrainingFacilitiesIdle(
			UnitType.Terran_Barracks);
	public static final GameState IdleTrainingFacility_Terran_CommandCenter = new GameStateUnits_TrainingFacilitiesIdle(
			UnitType.Terran_Command_Center);
	public static final GameState IdleTrainingFacility_Terran_Factory = new GameStateUnits_TrainingFacilitiesIdle(
			UnitType.Terran_Factory);
	public static final GameState IdleTrainingFacility_Terran_Starport = new GameStateUnits_TrainingFacilitiesIdle(
			UnitType.Terran_Starport);

	// The current score this state holds.
	private double currentScore = 0.;
	// The number of times the state is going to be divided.
	private int currentDivider = 1;
	// The frame time stamp of the last score / divider update.
	protected int lastUpdateTimeStampScore = 0;
	protected int updateFramesPassedScore = 0;
	// The frame time stamp of the last score / divider update.
	protected int lastUpdateTimeStampDivider = 0;
	protected int updateFramesPassedDivider = 0;

	public GameState() {

	}

	// -------------------- Functions

	/**
	 * Function for updating the current score of the GameState.
	 *
	 * @param scoringDirector
	 *            the ScoringDirector that is going to be used for all missing
	 *            values and information regarding the various influences the
	 *            Bot is experiencing.
	 * @param manager
	 *            the BuildActionManager that contains all important
	 *            information.
	 */
	public void updateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		int frameCount = Core.getInstance().getGame().getFrameCount();

		this.updateFramesPassedScore = frameCount - this.lastUpdateTimeStampScore;
		this.currentScore = this.generateScore(scoringDirector, manager);
		this.lastUpdateTimeStampScore = frameCount;
	}

	/**
	 * Function for updating the current divider of the GameState.
	 *
	 * @param scoringDirector
	 *            the ScoringDirector that is going to be used for all missing
	 *            values and information regarding the various influences the
	 *            Bot is experiencing.
	 * @param manager
	 *            the BuildActionManager that contains all important
	 *            information.
	 */
	public void updateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		int frameCount = Core.getInstance().getGame().getFrameCount();

		this.updateFramesPassedDivider = frameCount - this.lastUpdateTimeStampDivider;
		this.currentDivider = this.generateDivider(scoringDirector, manager);
		this.lastUpdateTimeStampDivider = frameCount;
	}

	/**
	 * Function for generating a new score for the GameState that represents the
	 * state of the game in the are that the GameState is responsible for. This
	 * score (For simplicity) should be between 0 and 1.
	 * 
	 * @param scoringDirector
	 *            the ScoringDirector that is going to be used for all missing
	 *            values and information regarding the various influences the
	 *            Bot is experiencing.
	 * @param manager
	 *            the BuildActionManager that contains all important
	 *            information.
	 * @return a score based on the provided information and the area the
	 *         GameState is performing in.
	 */
	protected abstract double generateScore(ScoringDirector scoringDirector, BuildActionManager manager);

	/**
	 * Function for generating a new divider for the GameState that represents
	 * the state of the game in the are that the GameState is responsible for.
	 * The higher the return value, the stronger the diving impact is.
	 * 
	 * @param scoringDirector
	 *            the ScoringDirector that is going to be used for all missing
	 *            values and information regarding the various influences the
	 *            Bot is experiencing.
	 * @param manager
	 *            the BuildActionManager that contains all important
	 *            information.
	 * @return a divider based on the provided information and the area the
	 *         GameState is performing in.
	 */
	protected abstract int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager);

	// ------------------------------ Getter / Setter

	public double getCurrentScore() {
		return currentScore;
	}

	public int getCurrentDivider() {
		return currentDivider;
	}

}
