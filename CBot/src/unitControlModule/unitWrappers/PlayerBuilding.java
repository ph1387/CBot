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
public class PlayerBuilding {

	// TODO: UML ADD
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

	// TODO: UML ADD
	/**
	 * Checker.java --- Class for transferring the Unit and InformationStorage
	 * reference.
	 * 
	 * @author P H - 02.09.2017
	 *
	 */
	private class Checker {

		protected Unit unit;
		protected InformationStorage informationStorage;

		public Checker(Unit unit, InformationStorage informationStorage) {
			this.unit = unit;
			this.informationStorage = informationStorage;
		}
	}

	// TODO: UML ADD
	/**
	 * TechnologyChecker.java --- Class used for checking for a viable TechType
	 * in the
	 * {@link PlayerBuilding#extractPossibleMatch(Queue, QueueElementChecker)}
	 * function.
	 * 
	 * @author P H - 02.09.2017
	 *
	 */
	private class TechnologyChecker extends Checker implements QueueElementChecker<TechType> {

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

	// TODO: UML ADD
	/**
	 * UpgradeChecker.java --- Class used for checking for a viable UpgradeType
	 * in the
	 * {@link PlayerBuilding#extractPossibleMatch(Queue, QueueElementChecker)}
	 * function.
	 * 
	 * @author P H - 02.09.2017
	 *
	 */
	private class UpgradeChecker extends Checker implements QueueElementChecker<UpgradeType> {

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

	// TODO: UML ADD
	/**
	 * AddonChecker.java --- Class used for checking for a viable UnitType
	 * (Addon) in the
	 * {@link PlayerBuilding#extractPossibleMatch(Queue, QueueElementChecker)}
	 * function.
	 * 
	 * @author P H - 02.09.2017
	 *
	 */
	private class AddonChecker extends Checker implements QueueElementChecker<UnitType> {

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

	// TODO: UML ADD
	/**
	 * TrainChecker.java --- Class used for checking for a viable UnitType
	 * (Trainable Unit) in the
	 * {@link PlayerBuilding#extractPossibleMatch(Queue, QueueElementChecker)}
	 * function.
	 * 
	 * @author P H - 02.09.2017
	 *
	 */
	private class TrainChecker extends Checker implements QueueElementChecker<UnitType> {

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
	// TODO: UML ADD
	private QueueElementChecker<TechType> technologyChecker;
	// TODO: UML ADD
	private QueueElementChecker<UpgradeType> upgradeChecker;
	// TODO: UML ADD
	private QueueElementChecker<UnitType> addonChecker;
	// TODO: UML ADD
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
			// Initiate the different kinds of actions the building can take
			if (this.state == State.TRAINING && !this.unit.isTraining() && this.trainedUnit != null
					&& this.commandExecutable) {
				this.unit.train(this.trainedUnit);
				this.commandExecutable = false;
			}
			// TODO: Test all following functionalities
			else if (this.state == State.CONSTRUCTING && !this.unit.isConstructing() && this.constructedAddon != null
					&& this.commandExecutable) {
				this.unit.buildAddon(this.constructedAddon);
				this.commandExecutable = false;
			} else if (this.state == State.UPGRADING && !this.unit.isUpgrading() && this.builtUpgrade != null
					&& this.commandExecutable) {
				this.unit.upgrade(this.builtUpgrade);
				this.commandExecutable = false;
			} else if (this.state == State.RESEARCHING && !this.unit.isResearching() && this.researchedTech != null
					&& this.commandExecutable) {
				this.unit.research(this.researchedTech);
				this.commandExecutable = false;
			}
			// Reset all information
			else if (this.unit.isIdle()) {
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

		// If the building can construct addons, contend the default location
		// for them in order to ensure that they can be build.
		this.addExtraAddonContendedTilePositions();
	}

	/**
	 * Function for switching the State of the building.
	 */
	private boolean switchState() {
		// Research a technology.
		if (this.state == State.IDLE) {
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

	// TODO: UML ADD
	/**
	 * Function for extracting an element from the provided Queue that matches
	 * the all QueueElementCheckers conditions. The function iterates through
	 * the whole Queue and only stops when either the Queue is cycled through
	 * once or an element that matches all conditions is found.
	 * 
	 * @param checkedQueue
	 *            the Queue that is being cycled through.
	 * @param elementCheker
	 *            the conditions that an element of the Queue must fulfill.
	 * @return an element that fulfills either all conditions of the
	 *         QueueElementChecker or null if none is found and the Queue was
	 *         cycled through once.
	 */
	private static <T> T extractPossibleMatch(Queue<T> checkedQueue, QueueElementChecker<T> elementCheker) {
		T matchingElement = null;

		if (elementCheker.checkElement()) {
			int queueSize = checkedQueue.size();

			// Iterate through all stored entries and try to find one that can
			// be worked on by this Unit.
			for (int i = 0; i < queueSize && matchingElement == null; i++) {
				T element = checkedQueue.poll();

				if (elementCheker.checkElement(element) && elementCheker.canAfford(element)) {
					matchingElement = element;
				} else {
					checkedQueue.add(element);
				}
			}
		}
		return matchingElement;
	}

	// TODO: UML ADD
	/**
	 * Function for adding the extra TilePositions to the contended ones that
	 * represent the default Position of the addon. This is needed since no
	 * other building is allowed to block any addons from being constructed
	 * which might happen if the space is not reserved.
	 */
	private void addExtraAddonContendedTilePositions() {
		if (this.unit.canBuildAddon()) {
			HashSet<TilePosition> extraAddonSpace = new HashSet<>();
			TilePositionContenderFactory.addAdditionalAddonSpace(this.unit.getType(), this.unit.getTilePosition(),
					extraAddonSpace);

			this.informationStorage.getMapInfo().getTilePositionContenders().addAll(extraAddonSpace);
		}
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
