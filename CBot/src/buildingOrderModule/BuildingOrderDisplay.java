package buildingOrderModule;

import java.text.DecimalFormat;
import java.util.HashSet;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.GameState;
import buildingOrderModule.scoringDirector.ScoringAction;
import buildingOrderModule.simulator.ActionType;
import bwapi.Game;
import core.Core;
import javaGOAP.GoapAction;

/**
 * BuildingOrderDisplay.java --- Display for all building order / build action
 * manager information as well as simulation and scoring results.
 * 
 * @author P H - 25.07.2017
 *
 */
public class BuildingOrderDisplay {

	private static final int OFFSET_LEFT = Core.getInstance().getOffsetLeft();
	private static final int OFFSET_LEFT_TOTAL = OFFSET_LEFT * 40;
	private static final int LINEHEIGHT = Core.getInstance().getLineheight();
	private static final Game GAME = Core.getInstance().getGame();

	// Formatter for the GameState value output.
	private static final DecimalFormat MULTIPLY_FORMATTER = new DecimalFormat("0.00");
	// Formatter for the score output.
	private static final DecimalFormat SCORE_FORMATTER = new DecimalFormat("00000");

	// -------------------- Functions

	/**
	 * Function for showing all information regarding the building manager,
	 * scoring and states.
	 * 
	 * @param manager
	 *            the manager that contains the references to the specified
	 *            instances that contain the information.
	 */
	public static void showInformation(BuildActionManager manager) {
		int currentPosY = LINEHEIGHT;

		currentPosY = showGameStates(manager, currentPosY);
		currentPosY = showGeneratedScores(manager, currentPosY);
	}

	// TODO: Possible Change: Combine with the UnitControlDisplay.
	/**
	 * Function for adding the default line height to the given y position and
	 * returning it.
	 * 
	 * @param posY
	 *            the Integer that the default line height is going to be added
	 *            to.
	 * @return the sum of the default line height and the given Integer.
	 */
	private static int leaveOneLineFree(int posY) {
		return posY += LINEHEIGHT;
	}

	/**
	 * Function for displaying the used GameStates of the BuildActionManager
	 * combined with their corresponding multiplier.
	 * 
	 * @param manager
	 *            the manager that contains the information regarding the
	 *            Actions and therefore the used GameStates.
	 * @param posY
	 *            the Y position at which the text will appear on screen.
	 * @return the new Y position leaving one line free after all information
	 *         got displayed.
	 */
	private static int showGameStates(BuildActionManager manager, int posY) {
		HashSet<ScoringAction> scoringActions = extractScoringActions(manager.getAvailableActions());
		HashSet<GameState> usedGameStates = extractUsedGameStates(scoringActions);
		int currentPosY = posY;

		GAME.drawTextScreen(OFFSET_LEFT_TOTAL, currentPosY, "MP's:");
		currentPosY += LINEHEIGHT;

		// Display the GameStates with their current value on the screen.
		for (GameState gameState : usedGameStates) {
			GAME.drawTextScreen(OFFSET_LEFT_TOTAL, currentPosY,
					MULTIPLY_FORMATTER.format(
							gameState.getCurrentScore() / Math.max((double) (gameState.getCurrentDivider()), 1.))
							+ " | " + gameState.getClass().getSimpleName());
			currentPosY += LINEHEIGHT;
		}

		return leaveOneLineFree(currentPosY);
	}

	/**
	 * Function for extracting all ScoringActions from a Set of given
	 * GoapActions. All actions that implement the specific Interface are
	 * returned.
	 * 
	 * @param actions
	 *            the actions that are going to be converted.
	 * @return a HashSet of ScoringActions that were converted using a Set of
	 *         GoapActions.
	 */
	private static HashSet<ScoringAction> extractScoringActions(HashSet<GoapAction> actions) {
		HashSet<ScoringAction> scoringActions = new HashSet<>();

		// Extract the instances that are convertible to ScoringActions.
		for (GoapAction goapAction : actions) {
			if (goapAction instanceof ScoringAction) {
				scoringActions.add((ScoringAction) goapAction);
			}
		}
		return scoringActions;
	}

	// TODO: Possible Change: Combine with ScoringDirector.
	/**
	 * Function for extracting the used GameStates from a HashSet of
	 * ScoringActions.
	 * 
	 * @param scoringActions
	 *            the actions whose GameStates are being extracted.
	 * @return a HashSet of GameStates that the provided ScoringActions use.
	 */
	private static HashSet<GameState> extractUsedGameStates(HashSet<ScoringAction> scoringActions) {
		HashSet<GameState> usedGameStates = new HashSet<>();

		for (ScoringAction scoringAction : scoringActions) {
			usedGameStates.addAll(scoringAction.defineUsedGameStates());
		}

		return usedGameStates;
	}

	/**
	 * Function for displaying the used Actions of the BuildActionManager as
	 * well as their corresponding scores.
	 * 
	 * @param manager
	 *            the manager that contains the information regarding the
	 *            Actions and therefore their corresponding scores.
	 * @param posY
	 *            the Y position at which the text will appear on screen.
	 * @return the new Y position leaving one line free after all information
	 *         got displayed.
	 */
	private static int showGeneratedScores(BuildActionManager manager, int posY) {
		int currentPosY = posY;

		GAME.drawTextScreen(OFFSET_LEFT_TOTAL, currentPosY, "Scores:");
		currentPosY += LINEHEIGHT;

		// Display the Actions and their score on screen (If they are an
		// instance of ActionType).
		for (GoapAction goapAction : manager.getAvailableActions()) {
			if (goapAction instanceof ActionType) {
				GAME.drawTextScreen(OFFSET_LEFT_TOTAL, currentPosY,
						SCORE_FORMATTER.format(((ActionType) goapAction).defineScore()) + " | "
								+ goapAction.getClass().getSimpleName());
				currentPosY += LINEHEIGHT;
			}
		}

		return leaveOneLineFree(currentPosY);
	}

}
