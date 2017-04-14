package unitControlModule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import bwapi.Color;
import bwapi.Game;
import core.Core;
import javaGOAP.GoapAgent;
import unitControlModule.unitWrappers.PlayerBuilding;
import unitControlModule.unitWrappers.PlayerUnitWorker;

// TODO: UML
/**
 * UnitControlModuleDisplay.java --- Class for displaying all important
 * information regarding Units, resources, queues etc.
 * 
 * @author P H - 14.04.2017
 *
 */
public class UnitControlDisplay {

	private static final int OFFSET_LEFT = Core.getInstance().getOffsetLeft();
	private static final int OFFSET_LEFT_TOTAL = OFFSET_LEFT * 15;
	private static final int LINEHEIGHT = Core.getInstance().getLineheight();
	private static final int BAR_WIDTH = Core.getInstance().getOffsetLeft() * 5;
	private static final Color WORKER_INFO_BAR_COLOR = new Color(255, 255, 255);
	private static final Color BUILDING_INFO_BAR_COLOR = new Color(255, 255, 255);
	private static final Game GAME = Core.getInstance().getGame();

	// -------------------- Functions

	// TODO: Possible Change: Add a bar to indicate the progress of any action

	/**
	 * Main function for showing all important information regarding all
	 * {@link PlayerBuilding}s and {@link PlayerUnitWorker}s.
	 * 
	 * @param agents
	 *            the GoapAgents which contain the PlayerUnit instances that are
	 *            going to be used to show information.
	 * @param buildings
	 *            the PlayerUnitBuilding instances that are going to be used to
	 *            show information like training Units, added Addons, etc.
	 */
	public static void showImportantInformation(HashSet<GoapAgent> agents, HashSet<PlayerBuilding> buildings) {
		int currentPosY = LINEHEIGHT;

		// Calculate the new y position each time a function gets called. Each
		// function returns the new y position of the text so that the following
		// collection can be shown accordingly.
		// TODO: REMOVE try catch block
		try {
			currentPosY = showResourceInformation(OFFSET_LEFT_TOTAL, currentPosY);
			currentPosY = showWorkerInformation(OFFSET_LEFT_TOTAL, currentPosY, agents);
			currentPosY = showBuildingInformation(OFFSET_LEFT_TOTAL, currentPosY, buildings);
			currentPosY = showBuildingQueue(OFFSET_LEFT_TOTAL, currentPosY);
			currentPosY = showTrainingQueue(OFFSET_LEFT_TOTAL, currentPosY);
			currentPosY = showAddonQueue(OFFSET_LEFT_TOTAL, currentPosY);
			currentPosY = showUpgradeQueue(OFFSET_LEFT_TOTAL, currentPosY);
			currentPosY = showResearchQueue(OFFSET_LEFT_TOTAL, currentPosY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
	 * Function for showing the reserved resources of the ResourceReserver on
	 * the screen.
	 * 
	 * @param posX
	 *            the x position of the information being displayed.
	 * @param posY
	 *            the y position of the information being displayed.
	 * @return the new y position the next line of text starts without
	 *         interfering with the currently display ones.
	 */
	public static int showResourceInformation(int posX, int posY) {
		String text = "Minerals: " + ResourceReserver.getReservedMinerals() + " - Gas: "
				+ ResourceReserver.getReservedGas();

		GAME.drawTextScreen(posX, posY, "Reserved Resources:");
		GAME.drawTextScreen(posX, posY + LINEHEIGHT, text);

		return leaveOneLineFree(posY + 2 * LINEHEIGHT);
	}

	/**
	 * Function for displaying all important PlayerUnitWorker information on the
	 * screen.
	 * 
	 * @param posX
	 *            the x position of the information being displayed.
	 * @param posY
	 *            the y position of the information being displayed.
	 * @param agents
	 *            the agents the PlayerUnitWorkers are extracted from.
	 * @return the new y position the next line of text starts without
	 *         interfering with the currently display ones.
	 */
	public static int showWorkerInformation(int posX, int posY, HashSet<GoapAgent> agents) {
		List<PlayerUnitWorker> workerUnits = new ArrayList<PlayerUnitWorker>();
		int counter = 1;

		// Get all worker instances
		for (GoapAgent goapAgent : agents) {
			if (goapAgent.getAssignedGoapUnit() instanceof PlayerUnitWorker) {
				workerUnits.add((PlayerUnitWorker) goapAgent.getAssignedGoapUnit());
			}
		}

		// Show all construction information
		GAME.drawTextScreen(posX, posY, "Constructions:");

		for (PlayerUnitWorker playerUnitWorker : workerUnits) {
			if (playerUnitWorker.getAssignedBuilding() != null) {
				int calculatedPosY = posY + counter * LINEHEIGHT;

				showBarFilled(posX, calculatedPosY, BAR_WIDTH, LINEHEIGHT,
						playerUnitWorker.getAssignedBuilding().getRemainingBuildTime(),
						playerUnitWorker.getAssignedBuildingType().buildTime(), WORKER_INFO_BAR_COLOR);
				GAME.drawTextScreen(posX + OFFSET_LEFT + BAR_WIDTH, calculatedPosY,
						playerUnitWorker.getAssignedBuildingType().toString());
				counter++;
			}
		}

		return leaveOneLineFree(posY + counter * LINEHEIGHT);
	}

	/**
	 * Function for displaying all important PlayerUnitBuilding information on
	 * the screen.
	 * 
	 * @param posX
	 *            the x position of the information being displayed.
	 * @param posY
	 *            the y position of the information being displayed.
	 * @param buildings
	 *            the buildings the information are extracted from.
	 * @return the new y position the next line of text starts without
	 *         interfering with the currently display ones.
	 */
	public static int showBuildingInformation(int posX, int posY, HashSet<PlayerBuilding> buildings) {
		int counter = 1;

		GAME.drawTextScreen(posX, posY, "Buildings:");

		for (PlayerBuilding playerBuilding : buildings) {
			String text = null;
			int currentValue = 0;
			int maxValue = 0;

			// Generate the specific information and values.
			if (playerBuilding.getTrainedUnit() != null) {
				text = playerBuilding.getTrainedUnit().toString();
				currentValue = playerBuilding.getUnit().getRemainingTrainTime();
				maxValue = playerBuilding.getTrainedUnit().buildTime();
			} else if (playerBuilding.getConstructedAddon() != null) {
				text = playerBuilding.getConstructedAddon().toString();
				currentValue = playerBuilding.getUnit().getRemainingBuildTime();
				maxValue = playerBuilding.getConstructedAddon().buildTime();
			} else if (playerBuilding.getBuiltUpgrade() != null) {
				text = playerBuilding.getBuiltUpgrade().toString();
				currentValue = playerBuilding.getUnit().getRemainingUpgradeTime();
				maxValue = playerBuilding.getBuiltUpgrade().upgradeTime();
			} else if (playerBuilding.getResearchedTech() != null) {
				text = playerBuilding.getResearchedTech().toString();
				currentValue = playerBuilding.getUnit().getRemainingResearchTime();
				maxValue = playerBuilding.getResearchedTech().researchTime();
			}

			if (text != null) {
				int calculatedPosY = posY + counter * LINEHEIGHT;

				showBarFilled(posX, calculatedPosY, BAR_WIDTH, LINEHEIGHT, currentValue, maxValue,
						BUILDING_INFO_BAR_COLOR);
				GAME.drawTextScreen(posX + OFFSET_LEFT + BAR_WIDTH, calculatedPosY, text);
				counter++;
			}
		}

		return leaveOneLineFree(posY + counter * LINEHEIGHT);
	}

	/**
	 * Function for displaying the building Queue on the screen.
	 * 
	 * @param posX
	 *            the x position of the information being displayed.
	 * @param posY
	 *            the y position of the information being displayed.
	 * @return the new y position the next line of text starts without
	 *         interfering with the currently display ones.
	 */
	public static int showBuildingQueue(int posX, int posY) {
		int newPosY = posY;

		if (!PlayerUnitWorker.buildingQueue.isEmpty()) {
			newPosY = showIterableCollection(posX, posY, PlayerUnitWorker.buildingQueue, "Building Queue:");
		}
		return leaveOneLineFree(newPosY);
	}

	/**
	 * Function for displaying the training Queue on the screen.
	 * 
	 * @param posX
	 *            the x position of the information being displayed.
	 * @param posY
	 *            the y position of the information being displayed.
	 * @return the new y position the next line of text starts without
	 *         interfering with the currently display ones.
	 */
	public static int showTrainingQueue(int posX, int posY) {
		int newPosY = posY;

		if (!PlayerBuilding.trainingQueue.isEmpty()) {
			newPosY = showIterableCollection(posX, posY, PlayerBuilding.trainingQueue, "Training Queue:");
		}
		return leaveOneLineFree(newPosY);
	}

	/**
	 * Function for displaying the addon Queue on the screen.
	 * 
	 * @param posX
	 *            the x position of the information being displayed.
	 * @param posY
	 *            the y position of the information being displayed.
	 * @return the new y position the next line of text starts without
	 *         interfering with the currently display ones.
	 */
	public static int showAddonQueue(int posX, int posY) {
		int newPosY = posY;

		if (!PlayerBuilding.addonQueue.isEmpty()) {
			newPosY = showIterableCollection(posX, posY, PlayerBuilding.addonQueue, "Addon Queue:");
		}
		return leaveOneLineFree(newPosY);
	}

	/**
	 * Function for displaying the upgrade Queue on the screen.
	 * 
	 * @param posX
	 *            the x position of the information being displayed.
	 * @param posY
	 *            the y position of the information being displayed.
	 * @return the new y position the next line of text starts without
	 *         interfering with the currently display ones.
	 */
	public static int showUpgradeQueue(int posX, int posY) {
		int newPosY = posY;

		if (!PlayerBuilding.upgradeQueue.isEmpty()) {
			newPosY = showIterableCollection(posX, posY, PlayerBuilding.upgradeQueue, "Upgrade Queue:");
		}
		return leaveOneLineFree(newPosY);
	}

	/**
	 * Function for displaying the research Queue on the screen.
	 * 
	 * @param posX
	 *            the x position of the information being displayed.
	 * @param posY
	 *            the y position of the information being displayed.
	 * @return the new y position the next line of text starts without
	 *         interfering with the currently display ones.
	 */
	public static int showResearchQueue(int posX, int posY) {
		int newPosY = posY;

		if (!PlayerBuilding.researchQueue.isEmpty()) {
			newPosY = showIterableCollection(posX, posY, PlayerBuilding.researchQueue, "Research Queue:");
		}
		return leaveOneLineFree(newPosY);
	}

	/**
	 * Function for displaying a collection with a title on the screen.
	 * 
	 * @param posX
	 *            the x position of the information being displayed.
	 * @param posY
	 *            the y position of the information being displayed.
	 * @param collection
	 *            the collection that is going to be displayed on the screen.
	 * @param title
	 *            the title that is going to be displayed above the collection.
	 * @return the new y position the next line of text starts without
	 *         interfering with the currently display ones.
	 */
	public static <T> int showIterableCollection(int posX, int posY, Iterable<T> collection, String title) {
		GAME.drawTextMap(posX, posY, title);
		return showIterableCollection(posX, posY + LINEHEIGHT, collection);
	}

	/**
	 * Function for displaying a collection on the screen.
	 * 
	 * @param posX
	 *            the x position of the information being displayed.
	 * @param posY
	 *            the y position of the information being displayed.
	 * @param collection
	 *            the collection that is going to be displayed on the screen.
	 * @return the new y position the next line of text starts without
	 *         interfering with the currently display ones.
	 */
	public static <T> int showIterableCollection(int posX, int posY, Iterable<T> collection) {
		int counter = 0;

		for (T unitType : collection) {
			GAME.drawTextScreen(posX, posY + counter * LINEHEIGHT, unitType.toString());
			counter++;
		}
		return posY + counter * LINEHEIGHT;
	}

	/**
	 * Function for displaying a bar on the screen which is filled to a certain
	 * extent.
	 * 
	 * @param posX
	 *            the x position of the bar being displayed.
	 * @param posY
	 *            the y position of the bar being displayed.
	 * @param width
	 *            the width of the bar.
	 * @param height
	 *            the height of the bar.
	 * @param currentValue
	 *            the current value that is represented in relation to the
	 *            maximum value with the bar.
	 * @param maxValue
	 *            the maximum that is going to be displayed in relation to the
	 *            minimum value with the bar.
	 * @param color
	 *            the Color that the bar will be displayed in.
	 */
	public static void showBarFilled(int posX, int posY, int width, int height, int currentValue, int maxValue,
			Color color) {
		double percentage = new Double(maxValue - currentValue) / new Double(maxValue);

		// left, top, right, bottom, color, filled
		GAME.drawBoxScreen(posX, posY, posX + width, posY + height, color);
		GAME.drawBoxScreen(posX, posY, (int) (posX + new Double(width) * percentage), posY + height, color, true);
	}
}
