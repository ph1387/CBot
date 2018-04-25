package unitControlModule.unitWrappers;

import java.util.HashSet;
import java.util.Queue;

import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import core.Core;
import core.TilePositionContenderFactory;
import informationStorage.InformationStorage;

/**
 * PlayerBuilding.java --- Wrapper for a Player building. Uses a FSM to
 * determine its actions.
 * 
 * @author P H - 06.04.2017
 *
 */
public class PlayerBuilding implements IPlayerUnitWrapper {

	/**
	 * QueueElementChecker.java --- Wrapper for Classes used in the
	 * {@link PlayerBuilding#extractPossibleMatch(Queue, QueueElementChecker)}
	 * function.
	 * 
	 * @author P H - 02.09.2017
	 *
	 */
	private interface QueueElementChecker<T> {

		public boolean checkElement();

		public boolean checkElement(T element);

		public boolean canAfford(T element);

	}

	/**
	 * Checker.java --- Class for transferring the Unit and InformationStorage
	 * reference.
	 * 
	 * @author P H - 02.09.2017
	 *
	 */
	private abstract class Checker<T> implements QueueElementChecker<T> {

		protected Unit unit;
		protected InformationStorage informationStorage;

		public Checker(Unit unit, InformationStorage informationStorage) {
			this.unit = unit;
			this.informationStorage = informationStorage;
		}
	}

	/**
	 * TechnologyChecker.java --- Class used for checking for a viable TechType
	 * in the
	 * {@link PlayerBuilding#extractPossibleMatch(Queue, QueueElementChecker)}
	 * function.
	 * 
	 * @author P H - 02.09.2017
	 *
	 */
	private class TechnologyChecker extends Checker<TechType> {

		public TechnologyChecker(Unit unit, InformationStorage informationStorage) {
			super(unit, informationStorage);
		}

		@Override
		public boolean checkElement() {
			return this.unit.canResearch();
		}

		@Override
		public boolean checkElement(TechType element) {
			return this.unit.canResearch(element);
		}

		@Override
		public boolean canAfford(TechType element) {
			return this.informationStorage.getResourceReserver().canAffordConstruction(element);
		}

	}

	/**
	 * UpgradeChecker.java --- Class used for checking for a viable UpgradeType
	 * in the
	 * {@link PlayerBuilding#extractPossibleMatch(Queue, QueueElementChecker)}
	 * function.
	 * 
	 * @author P H - 02.09.2017
	 *
	 */
	private class UpgradeChecker extends Checker<UpgradeType> {

		public UpgradeChecker(Unit unit, InformationStorage informationStorage) {
			super(unit, informationStorage);
		}

		@Override
		public boolean checkElement() {
			return this.unit.canUpgrade();
		}

		@Override
		public boolean checkElement(UpgradeType element) {
			return this.unit.canUpgrade(element);
		}

		@Override
		public boolean canAfford(UpgradeType element) {
			return this.informationStorage.getResourceReserver().canAffordConstruction(element);
		}

	}

	/**
	 * AddonChecker.java --- Class used for checking for a viable UnitType
	 * (Addon) in the
	 * {@link PlayerBuilding#extractPossibleMatch(Queue, QueueElementChecker)}
	 * function.
	 * 
	 * @author P H - 02.09.2017
	 *
	 */
	private class AddonChecker extends Checker<UnitType> {

		public AddonChecker(Unit unit, InformationStorage informationStorage) {
			super(unit, informationStorage);
		}

		@Override
		public boolean checkElement() {
			return this.unit.canBuildAddon();
		}

		@Override
		public boolean checkElement(UnitType element) {
			return this.unit.canBuildAddon(element);
		}

		@Override
		public boolean canAfford(UnitType element) {
			return this.informationStorage.getResourceReserver().canAffordConstruction(element);
		}

	}

	/**
	 * TrainChecker.java --- Class used for checking for a viable UnitType
	 * (Trainable Unit) in the
	 * {@link PlayerBuilding#extractPossibleMatch(Queue, QueueElementChecker)}
	 * function.
	 * 
	 * @author P H - 02.09.2017
	 *
	 */
	private class TrainChecker extends Checker<UnitType> {

		public TrainChecker(Unit unit, InformationStorage informationStorage) {
			super(unit, informationStorage);
		}

		@Override
		public boolean checkElement() {
			return this.unit.canTrain();
		}

		@Override
		public boolean checkElement(UnitType element) {
			return this.unit.canTrain(element);
		}

		@Override
		public boolean canAfford(UnitType element) {
			return this.informationStorage.getResourceReserver().canAffordConstruction(element);
		}

	}

	// The different checkers that are necessary for extracting different
	// elements from the Queues stored in the information storage:
	private QueueElementChecker<TechType> technologyChecker;
	private QueueElementChecker<UpgradeType> upgradeChecker;
	private QueueElementChecker<UnitType> addonChecker;
	private QueueElementChecker<UnitType> trainChecker;

	protected Unit unit;

	protected UnitType trainedUnit;
	protected UnitType constructedAddon;
	protected UpgradeType builtUpgrade;
	protected TechType researchedTech;

	protected boolean commandExecutable = true;

	private enum State {
		IDLE, TRAINING, CONSTRUCTING, UPGRADING, RESEARCHING
	}

	private State state = State.IDLE;

	private InformationStorage informationStorage;

	public PlayerBuilding(Unit unit, InformationStorage informationStorage) {
		this.unit = unit;
		this.informationStorage = informationStorage;

		// Initialize the different checkers:
		this.technologyChecker = new TechnologyChecker(this.unit, this.informationStorage);
		this.upgradeChecker = new UpgradeChecker(this.unit, this.informationStorage);
		this.addonChecker = new AddonChecker(this.unit, this.informationStorage);
		this.trainChecker = new TrainChecker(this.unit, this.informationStorage);
	}

	// -------------------- Functions

	/**
	 * Update-function for the building.
	 */
	public void update() {
		// Only perform these checks on buildings that actually can perform
		// these. This primarily excludes buildings that provide supplies.
		if (this.unit.getType() != Core.getInstance().getPlayer().getRace().getSupplyProvider()) {
			// Initiate the different kinds of actions the building can take.
			if (this.state == State.TRAINING && !this.unit.isTraining() && this.trainedUnit != null
					&& this.commandExecutable) {
				this.commandExecutable = !this.unit.train(this.trainedUnit);
			} else if (this.state == State.CONSTRUCTING && !this.unit.isConstructing() && this.constructedAddon != null
					&& this.commandExecutable) {
				this.commandExecutable = !this.unit.buildAddon(this.constructedAddon);
			} else if (this.state == State.UPGRADING && !this.unit.isUpgrading() && this.builtUpgrade != null
					&& this.commandExecutable) {
				this.commandExecutable = !this.unit.upgrade(this.builtUpgrade);
			} else if (this.state == State.RESEARCHING && !this.unit.isResearching() && this.researchedTech != null
					&& this.commandExecutable) {
				this.commandExecutable = !this.unit.research(this.researchedTech);
			}
			// Reset all information. The rather complicated condition is due to
			// the fact, that only checking for the "isIdle" parameter does not
			// always return the exact value:
			//
			// "Note It is possible for a unit to remain in the training queue
			// with no progress. In that case, this function will return false
			// because of supply or unit count limitations. (...)"
			//
			// Thats why the training Queue itself must be checked if the Unit
			// is able to train other Units.
			else if (((this.unit.canTrain() && this.unit.getTrainingQueue().isEmpty()) || (!this.unit.canTrain()))
					&& this.unit.isIdle() && this.state != State.IDLE) {
				this.state = State.IDLE;

				this.trainedUnit = null;
				this.constructedAddon = null;
				this.builtUpgrade = null;
				this.researchedTech = null;

				this.commandExecutable = true;
			}

			// Try assigning work to the building.
			if (this.state == State.IDLE) {
				this.switchState();
			}
		}

		this.addContendedTilePositions();
	}

	@Override
	public void destroy() {
		this.removeContendedTilePositions();
	}

	/**
	 * Function for switching the State of the building.
	 */
	private boolean switchState() {
		// Research a technology.
		if (this.state == State.IDLE) {
			// Remove any technologies that have already been researched. This
			// is needed to prevent deadlocks from happening.
			while (this.informationStorage.getResearchQueue().peek() != null && Core.getInstance().getPlayer()
					.hasResearched(this.informationStorage.getResearchQueue().peek())) {
				this.informationStorage.getResearchQueue().poll();
			}

			this.researchedTech = extractPossibleMatch(this.informationStorage.getResearchQueue(),
					this.technologyChecker);

			if (this.researchedTech != null) {
				this.state = State.RESEARCHING;
			}
		}

		// Build an upgrade.
		if (this.state == State.IDLE) {
			this.builtUpgrade = extractPossibleMatch(this.informationStorage.getUpgradeQueue(), this.upgradeChecker);

			if (this.builtUpgrade != null) {
				this.state = State.UPGRADING;
			}
		}

		// Construct an addon.
		if (this.state == State.IDLE) {
			this.constructedAddon = extractPossibleMatch(this.informationStorage.getAddonQueue(), this.addonChecker);

			if (this.constructedAddon != null) {
				this.state = State.CONSTRUCTING;
			}
		}

		// Train an Unit.
		if (this.state == State.IDLE) {
			this.trainedUnit = extractPossibleMatch(this.informationStorage.getTrainingQueue(), this.trainChecker);

			if (this.trainedUnit != null) {
				this.state = State.TRAINING;
			}
		}

		return this.state != State.IDLE;
	}

	/**
	 * Function for extracting an element from the provided Queue that matches
	 * the all QueueElementCheckers conditions.
	 * 
	 * @param checkedQueue
	 *            the Queue that is being looked at.
	 * @param elementCheker
	 *            the conditions that an element of the Queue must fulfill.
	 * @return an element that fulfills either all conditions of the
	 *         QueueElementChecker or null if none is found and the Queue was
	 *         cycled through once.
	 */
	private static <T> T extractPossibleMatch(Queue<T> checkedQueue, QueueElementChecker<T> elementCheker) {
		T matchingElement = null;

		if (elementCheker.checkElement()) {
			T element = checkedQueue.peek();

			if (element != null && elementCheker.checkElement(element) && elementCheker.canAfford(element)) {
				matchingElement = checkedQueue.poll();
			}

		}
		return matchingElement;
	}

	/**
	 * Function for adding the TilePositions the building is contending to the
	 * shared information storage.
	 */
	private void addContendedTilePositions() {
		HashSet<TilePosition> contendedTilePositions = TilePositionContenderFactory
				.generateNeededTilePositions(this.unit.getType(), this.unit.getTilePosition());

		this.informationStorage.getMapInfo().getTilePositionContenders().addAll(contendedTilePositions);
	}

	/**
	 * Function for removing the TilePositions the building is contending from
	 * the shared information storage.
	 */
	private void removeContendedTilePositions() {
		HashSet<TilePosition> contendedTilePositions = TilePositionContenderFactory
				.generateNeededTilePositions(this.unit.getType(), this.unit.getTilePosition());

		this.informationStorage.getMapInfo().getTilePositionContenders().removeAll(contendedTilePositions);
	}

	// ------------------------------ Getter / Setter

	public Unit getUnit() {
		return unit;
	}

	public UnitType getTrainedUnit() {
		return trainedUnit;
	}

	public UnitType getConstructedAddon() {
		return constructedAddon;
	}

	public UpgradeType getBuiltUpgrade() {
		return builtUpgrade;
	}

	public TechType getResearchedTech() {
		return researchedTech;
	}
}
