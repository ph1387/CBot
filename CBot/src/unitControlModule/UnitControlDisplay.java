package unitControlModule;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import bwapi.Color;
import bwapi.Game;
import core.Core;
import informationStorage.InformationStorage;
import javaGOAP.GoapAgent;
import unitControlModule.unitWrappers.PlayerBuilding;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitWorker;
import workerManagerConstructionJobDistribution.IConstrucionInformation;
import workerManagerConstructionJobDistribution.WorkerManagerConstructionJobDistribution;

/**
 * UnitControlModuleDisplay.java --- Class for displaying all important
 * information regarding Units, resources, queues etc.
 * 
 * @author P H - 14.04.2017
 *
 */
public class UnitControlDisplay {

	private static final int OFFSET_LEFT = Core.getInstance().getOffsetLeft();
	private static final int OFFSET_LEFT_TOTAL = OFFSET_LEFT * 20;
	private static final int LINEHEIGHT = Core.getInstance().getLineheight();
	private static final int BAR_WIDTH = Core.getInstance().getOffsetLeft() * 5;
	private static final Color WORKER_INFO_BAR_COLOR = new Color(255, 255, 255);
	private static final Color BUILDING_INFO_BAR_COLOR = new Color(255, 255, 255);
	private static final Game GAME = Core.getInstance().getGame();

	// Formatter for the PlayerUnit confidence output
	private static final DecimalFormat CONFIDENCE_FORMATTER = new DecimalFormat("0.00");

	// -------------------- Functions

	/**
	 * Function for displaying the confidence of each Unit on the map at their
	 * current Position.
	 * 
	 * @param agents
	 *            the GoapAgents which contain the PlayerUnit references that
	 *            are needed for displaying.
	 */
	public static void showConfidence(Collection<GoapAgent> agents) {
		for (GoapAgent goapAgent : agents) {
			GAME.drawTextMap(((PlayerUnit) goapAgent.getAssignedGoapUnit()).getUnit().getPosition(),
					CONFIDENCE_FORMATTER.format(((PlayerUnit) goapAgent.getAssignedGoapUnit()).getConfidence()));
		}
	}

	/**
	 * Main function for showing all important queued information regarding all
	 * {@link PlayerBuilding}s and {@link PlayerUnitWorker}s. This includes
	 * queued buildings, technologies etc. as well as reserved resources and
	 * construction timers / bars.
	 * 
	 * @param agents
	 *            the GoapAgents which contain the PlayerUnit instances that are
	 *            going to be used to show information.
	 * @param buildings
	 *            the PlayerUnitBuilding instances that are going to be used to
	 *            show information like training Units, added Addons, etc.
	 * @param informationStorage
	 *            object that holds all important worker and resource
	 *            information.
	 */
	public static void showQueueInformation(Collection<GoapAgent> agents, Collection<PlayerBuilding> buildings,
			InformationStorage informationStorage) {
		int currentPosY = LINEHEIGHT;

		// Calculate the new y position each time a function gets called. Each
		// function returns the new y position of the text so that the following
		// collection can be shown accordingly.
		try {
			currentPosY = showResourceInformation(OFFSET_LEFT_TOTAL, currentPosY, informationStorage);
			currentPosY = showWorkerInformation(OFFSET_LEFT_TOTAL, currentPosY, agents);
			currentPosY = showBuildingInformation(OFFSET_LEFT_TOTAL, currentPosY, buildings);
			currentPosY = showBuildingQueue(OFFSET_LEFT_TOTAL, currentPosY, agents);
			currentPosY = showTrainingQueue(OFFSET_LEFT_TOTAL, currentPosY, informationStorage);
			currentPosY = showAddonQueue(OFFSET_LEFT_TOTAL, currentPosY, informationStorage);
			currentPosY = showUpgradeQueue(OFFSET_LEFT_TOTAL, currentPosY, informationStorage);
			currentPosY = showResearchQueue(OFFSET_LEFT_TOTAL, currentPosY, informationStorage);
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
	 * @param informationStorage
	 *            object that holds all important worker and resource
	 *            information.
	 * @return the new y position the next line of text starts without
	 *         interfering with the currently display ones.
	 */
	public static int showResourceInformation(int posX, int posY, InformationStorage informationStorage) {
		String text = "Minerals: " + informationStorage.getResourceReserver().getReservedMinerals() + " - Gas: "
				+ informationStorage.getResourceReserver().getReservedGas();

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
	public static int showWorkerInformation(int posX, int posY, Collection<GoapAgent> agents) {
		List<PlayerUnitWorker> workerUnits = sortConstructionWorkers(getPerformingConstrcutionWorkers(agents));
		int counter = 1;

		// Show all construction information.
		GAME.drawTextScreen(posX, posY, "Constructions:");

		for (PlayerUnitWorker playerUnitWorker : workerUnits) {
			IConstrucionInformation constructionInformation = playerUnitWorker
					.getWorkerManagerConstructionJobDistribution().getConstructionInformation(playerUnitWorker);
			int calculatedPosY = posY + counter * LINEHEIGHT;

			showBarFilled(posX, calculatedPosY, BAR_WIDTH, LINEHEIGHT,
					constructionInformation.getBuilding().getRemainingBuildTime(),
					constructionInformation.getUnitType().buildTime(), WORKER_INFO_BAR_COLOR);
			GAME.drawTextScreen(posX + OFFSET_LEFT + BAR_WIDTH, calculatedPosY,
					constructionInformation.getUnitType().toString());
			counter++;
		}

		return leaveOneLineFree(posY + counter * LINEHEIGHT);
	}

	// TODO: UML ADD
	/**
	 * Function for extracting all {@link PlayerUnitWorker}s from a Collection
	 * of {@link GoapAgent}s that are assigned a construction job and as well as
	 * already started the construction of the building.
	 * 
	 * @param agents
	 *            the Collection of {@link GoapAgent}s from which all
	 *            {@link PlayerUnitWorker}s are extracted from.
	 * @return a List of all {@link PlayerUnitWorker}s that are assigned a
	 *         construction job and started the construction of the building.
	 */
	private static List<PlayerUnitWorker> getPerformingConstrcutionWorkers(Collection<GoapAgent> agents) {
		List<PlayerUnitWorker> workerUnits = new ArrayList<>();

		// Get all worker instances that are currently constructing a building.
		for (GoapAgent goapAgent : agents) {
			if (goapAgent.getAssignedGoapUnit() instanceof PlayerUnitWorker) {
				PlayerUnitWorker playerUnitWorker = (PlayerUnitWorker) goapAgent.getAssignedGoapUnit();
				WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution = playerUnitWorker
						.getWorkerManagerConstructionJobDistribution();

				if (workerManagerConstructionJobDistribution.isAssignedConstructing(playerUnitWorker)
						&& workerManagerConstructionJobDistribution.getConstructionInformation(playerUnitWorker)
								.constructionStarted()) {
					workerUnits.add(playerUnitWorker);
				}
			}
		}
		return workerUnits;
	}

	// TODO: UML ADD
	/**
	 * Function for sorting a List of {@link PlayerUnitWorker}s according to the
	 * time that their assigned construction job requires. The ones with the
	 * percentual least time left are moved to the beginning of the List.
	 * 
	 * @param workers
	 *            the List of {@link PlayerUnitWorker}s that is going to be
	 *            sorted.
	 * @return a sorted List of {@link PlayerUnitWorker}s with the workers that
	 *         are assigned the construction job requiring the least percentual
	 *         additional time in the beginning.
	 */
	private static List<PlayerUnitWorker> sortConstructionWorkers(List<PlayerUnitWorker> workers) {
		workers.sort(new Comparator<PlayerUnitWorker>() {

			@Override
			public int compare(PlayerUnitWorker puw1, PlayerUnitWorker puw2) {
				double compareValueOne = this.getCompareValue(puw1);
				double compareValueTwo = this.getCompareValue(puw2);

				return Double.compare(compareValueOne, compareValueTwo);
			}

			private double getCompareValue(PlayerUnitWorker playerUnitWorker) {
				WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution = playerUnitWorker
						.getWorkerManagerConstructionJobDistribution();
				IConstrucionInformation constructionInformation = workerManagerConstructionJobDistribution
						.getConstructionInformation(playerUnitWorker);

				return Double.valueOf(constructionInformation.getBuilding().getRemainingBuildTime())
						/ Double.valueOf(constructionInformation.getUnitType().buildTime());
			}
		});

		return workers;
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
	public static int showBuildingInformation(int posX, int posY, Collection<PlayerBuilding> buildings) {
		List<PlayerBuilding> sortedCollection = sortBuildings(buildings);
		int counter = 1;

		GAME.drawTextScreen(posX, posY, "Buildings:");

		for (PlayerBuilding playerBuilding : sortedCollection) {
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
				currentValue = playerBuilding.getUnit().getRemainingTrainTime();
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

	// TODO: UML ADD
	/**
	 * Function for sorting a Collection of {@link PlayerBuilding}s according to
	 * the time that their specific production / research etc. requires. The
	 * ones with the percentual least time left are moved to the beginning of
	 * the List.
	 * 
	 * @param buildings
	 *            the Collection of {@link PlayerBuilding}s that is going to be
	 *            sorted.
	 * @return a sorted List of {@link PlayerBuilding}s with the buildings
	 *         requiring the least percentual additional time in the beginning.
	 */
	private static List<PlayerBuilding> sortBuildings(Collection<PlayerBuilding> buildings) {
		List<PlayerBuilding> sortedCollection = new ArrayList<PlayerBuilding>(buildings);

		// Sort the collection for a better representation of the values. The
		// ones with lesser build time left get put before the ones with more
		// time left.
		sortedCollection.sort(new Comparator<PlayerBuilding>() {

			@Override
			public int compare(PlayerBuilding pb1, PlayerBuilding pb2) {
				double compareValueOne = this.getCompareValue(pb1);
				double compareValueTwo = this.getCompareValue(pb2);

				return Double.compare(compareValueOne, compareValueTwo);
			}

			private double getCompareValue(PlayerBuilding playerBuilding) {
				double compareValue = 0;

				if (playerBuilding.getTrainedUnit() != null) {
					compareValue = Double.valueOf(playerBuilding.getUnit().getRemainingTrainTime())
							/ Double.valueOf(playerBuilding.getTrainedUnit().buildTime());
				} else if (playerBuilding.getConstructedAddon() != null) {
					compareValue = Double.valueOf(playerBuilding.getUnit().getRemainingTrainTime())
							/ Double.valueOf(playerBuilding.getConstructedAddon().buildTime());
				} else if (playerBuilding.getBuiltUpgrade() != null) {
					compareValue = Double.valueOf(playerBuilding.getUnit().getRemainingUpgradeTime())
							/ Double.valueOf(playerBuilding.getBuiltUpgrade().upgradeTime());
				} else if (playerBuilding.getResearchedTech() != null) {
					compareValue = Double.valueOf(playerBuilding.getUnit().getRemainingResearchTime())
							/ Double.valueOf(playerBuilding.getResearchedTech().researchTime());
				}
				return compareValue;
			}
		});

		return sortedCollection;
	}

	/**
	 * Function for displaying the building Queue on the screen.
	 *
	 * @param posX
	 *            the x position of the information being displayed.
	 * @param posY
	 *            the y position of the information being displayed. * @param
	 *            agents the agents the PlayerUnitWorkers are extracted from
	 *            which are used for accessing the construction manager
	 *            instance.
	 * @return the new y position the next line of text starts without
	 *         interfering with the currently display ones.
	 */
	public static int showBuildingQueue(int posX, int posY, Collection<GoapAgent> agents) {
		PlayerUnitWorker referenceWorker = null;
		int newPosY = posY;

		// Get at least a single reference to a PlayerUnitWorker for accessing
		// the construction manager instance of it.
		for (GoapAgent goapAgent : agents) {
			if (goapAgent.getAssignedGoapUnit() instanceof PlayerUnitWorker) {
				referenceWorker = (PlayerUnitWorker) goapAgent.getAssignedGoapUnit();
				break;
			}
		}

		if (referenceWorker != null) {
			newPosY = showIterableCollection(posX, posY,
					referenceWorker.getWorkerManagerConstructionJobDistribution().getBuildingQueue(),
					"Building Queue:");
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
	 * @param informationStorage
	 *            object that holds all important worker and resource
	 *            information.
	 * @return the new y position the next line of text starts without
	 *         interfering with the currently display ones.
	 */
	public static int showTrainingQueue(int posX, int posY, InformationStorage informationStorage) {
		int newPosY = posY;

		if (!informationStorage.getTrainingQueue().isEmpty()) {
			newPosY = showIterableCollection(posX, posY, informationStorage.getTrainingQueue(), "Training Queue:");
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
	 * @param informationStorage
	 *            object that holds all important worker and resource
	 *            information.
	 * @return the new y position the next line of text starts without
	 *         interfering with the currently display ones.
	 */
	public static int showAddonQueue(int posX, int posY, InformationStorage informationStorage) {
		int newPosY = posY;

		if (!informationStorage.getAddonQueue().isEmpty()) {
			newPosY = showIterableCollection(posX, posY, informationStorage.getAddonQueue(), "Addon Queue:");
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
	 * @param informationStorage
	 *            object that holds all important worker and resource
	 *            information.
	 * @return the new y position the next line of text starts without
	 *         interfering with the currently display ones.
	 */
	public static int showUpgradeQueue(int posX, int posY, InformationStorage informationStorage) {
		int newPosY = posY;

		if (!informationStorage.getUpgradeQueue().isEmpty()) {
			newPosY = showIterableCollection(posX, posY, informationStorage.getUpgradeQueue(), "Upgrade Queue:");
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
	 * @param informationStorage
	 *            object that holds all important worker and resource
	 *            information.
	 * @return the new y position the next line of text starts without
	 *         interfering with the currently display ones.
	 */
	public static int showResearchQueue(int posX, int posY, InformationStorage informationStorage) {
		int newPosY = posY;

		if (!informationStorage.getResearchQueue().isEmpty()) {
			newPosY = showIterableCollection(posX, posY, informationStorage.getResearchQueue(), "Research Queue:");
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
		GAME.drawTextScreen(posX, posY, title);
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
