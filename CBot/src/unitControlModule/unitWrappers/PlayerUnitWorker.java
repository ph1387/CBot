package unitControlModule.unitWrappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.BiConsumer;

import bwapi.Unit;
import bwapi.UnitType;
import informationStorage.InformationPreserver;

// TODO: UML MASSIVE CHANGES
/**
 * PlayerUnitWorker.java --- Wrapper for a general worker Unit.
 * 
 * @author P H - 29.03.2017
 *
 */
public abstract class PlayerUnitWorker extends PlayerUnit {

	// TODO: UML REMOVED STATICS
	
	protected boolean assignedToSout = false;

	// TODO: UML REMOVED MORE STATICS
	
	// Building related stuff
	protected boolean constructingFlag = false;
	protected int personalReservedMinerals = 0;
	protected int personalReservedGas = 0;

	public enum ConstructionState {
		IDLE, AWAIT_CONFIRMATION, CONFIRMED
	}

	protected ConstructionState currentConstructionState = ConstructionState.IDLE;
	protected int constructionCounter = 0;
	protected UnitType assignedBuildingType;
	protected Unit assignedBuilding;
	
	// Resources
	protected Unit closestFreeMineralField;
	protected Unit closestFreeGasSource;
	protected boolean resourcesResettable = false;

	// TODO: UML
	public PlayerUnitWorker(Unit unit, InformationPreserver informationPreserver) {
		super(unit, informationPreserver);

		this.informationPreserver.getWorkerConfig().incrementTotalWorkerCount();
	}

	// -------------------- Functions

	/**
	 * Function needs to be overwritten due to the workers actions being very
	 * delicate processes. A simple reset is not possible since multiple actions
	 * require information that is going to be reseted normally. Therefore the
	 * order needs to be as follows:
	 * <ul>
	 * <li>Reset gets called -> information regarding various worker tasks are
	 * being reseted
	 * <li>Information must be restored
	 * <li>Information can be transferred to the actions at the end of the
	 * super.update function
	 * </ul>
	 * 
	 * @see javaGOAP.GoapUnit#resetActions()
	 */
	@Override
	public void resetActions() {
		if (!this.unit.isConstructing()) {
			super.resetActions();

			// Remove any contended spots
			this.updateMappedSourceContenders();

			// Assign a new closestFreeMineralField / closestFreeGasSource since
			// these are being transferred over to the GatherAction at the end
			// of
			// the main update function (especially after a reset!). Without
			// this
			// the Units would refrain from gathering for one cycle and possibly
			// end
			// up attacking the enemy.
			if (!this.isMappedToGatheringSource()) {
				this.markContenders();
			}
		}
	}

	@Override
	public void update() {
		this.customUpdate();

		super.update();
	}

	/**
	 * Should be called at least one time from the sub class if overwritten. It
	 * is updating all necessary information regarding various tasks the worker
	 * can execute as well as shared information between all workers.
	 */
	protected void customUpdate() {
		this.tryFreeingResources();
		this.updateMappedSourceContenders();
		this.updateConstructionState();

		// Scout at the beginning of the game if a certain worker count is
		// reached.
		if (!this.informationPreserver.getWorkerConfig().isWorkerOnceAssignedScouting() && this.informationPreserver.getWorkerConfig().getTotalWorkerCount() >= this.informationPreserver.getWorkerConfig().getWorkerScoutingTrigger()
				&& this.currentConstructionState == ConstructionState.IDLE && this.assignedBuildingType == null
				&& !this.unit.isGatheringGas()) {
			this.informationPreserver.getWorkerConfig().setWorkerOnceAssignedScouting(true);
			this.assignedToSout = true;
			this.resetActions();
		} else if (!this.assignedToSout) {
			this.updateCurrentActionInformation();
		}
	}

	/**
	 * Function for freeing any reserved resources. This depends on a flag, that
	 * is going to be set as soon as the building Unit, that this worker is
	 * constructing, and the resulting building flag is being set. This ensures,
	 * that any resources are only going to be freed a single and not multiple
	 * times.
	 */
	protected void tryFreeingResources() {
		if (this.resourcesResettable) {
			this.resourcesResettable = false;

			// Reset any reserved resources
			this.freeResources();
		}
	}

	/**
	 * Function for actually freeing the reserved resources of the Unit.
	 */
	protected void freeResources() {
		this.informationPreserver.getResourceReserver().freeMinerals(this.personalReservedMinerals);
		this.informationPreserver.getResourceReserver().freeGas(this.personalReservedGas);
		this.personalReservedMinerals = 0;
		this.personalReservedGas = 0;
	}

	/**
	 * Function for removing any previously contended gathering spots by the
	 * worker. This is needed since the spots must be reassigned if the Unit for
	 * example starts constructing a building.
	 */
	protected void updateMappedSourceContenders() {
		if (this.informationPreserver.getWorkerConfig().getMappedSourceContenders().containsKey(this.closestFreeMineralField)) {
			this.informationPreserver.getWorkerConfig().getMappedSourceContenders().get(this.closestFreeMineralField).remove(this.unit);
		}
		if (this.informationPreserver.getWorkerConfig().getMappedSourceContenders().containsKey(this.closestFreeGasSource)) {
			this.informationPreserver.getWorkerConfig().getMappedSourceContenders().get(this.closestFreeGasSource).remove(this.unit);
		}
	}

	/**
	 * Function for updating the construction state of a worker Unit. This is
	 * needed for every worker that is / was currently constructing a building
	 * and therefore was assigned a building type and a specific amount of
	 * resources. These information need to be removed from the worker to
	 * prevent a clogging of resources and buildings. Also this function acts as
	 * a safety feature since it queues all construction jobs the Unit is / was
	 * not able to fulfill in a certain amount of time in the general
	 * construction queue again. This way all queued buildings actually get
	 * constructed.
	 */
	protected void updateConstructionState() {
		// Wait for the confirmation until either a limit is reached or the
		// confirmation was given.
		if (this.currentConstructionState == ConstructionState.AWAIT_CONFIRMATION) {
			if (this.constructionCounter < this.informationPreserver.getWorkerConfig().getConstructionCounterMax()) {
				this.constructionCounter++;
			} else {
				this.constructionCounter = 0;
				this.currentConstructionState = ConstructionState.IDLE;

				this.resetAwaitedConstruction();
			}

			if (this.assignedBuildingType != null
					&& this.informationPreserver.getWorkerConfig().getMappedBuildActions().getOrDefault(this.unit, null) == this.assignedBuildingType) {
				this.constructionCounter = 0;
				this.currentConstructionState = ConstructionState.CONFIRMED;
			}
		}
		// No "else if" since it will be executed in one cycle this way.
		if (this.currentConstructionState == ConstructionState.CONFIRMED) {
			// Remove failed / finished construction jobs. No iteration counter
			// here, since this functionality would be overridden by the
			// ActionUpdaterWorker.
			// -> Safety feature, so that no Unit holds a order and does not
			// execute it because as soon as a building location is occupied,
			// the building gets added back into the building queue.
			if (this.assignedBuildingType != null && this.informationPreserver.getWorkerConfig().getMappedBuildActions().getOrDefault(this.unit, null) == null) {
				this.currentConstructionState = ConstructionState.IDLE;

				this.resetAwaitedConstruction();
			}
		}
	}

	/**
	 * Function for updating all information regarding possible work the worker
	 * can do. This is either the assigning of a building for construction or a
	 * gathering source for either minerals or gas.
	 */
	protected void updateCurrentActionInformation() {
		// Get a building from the building Queue and reset actions if possible.
		if (!this.unit.isGatheringGas() && !this.informationPreserver.getWorkerConfig().getBuildingQueue().isEmpty()
				&& this.informationPreserver.getResourceReserver().canAffordConstruction(this.informationPreserver.getWorkerConfig().getBuildingQueue().peek())
				&& this.currentConstructionState == ConstructionState.IDLE) {
			this.assignConstructionJob();
		}
		// Find a gathering source.
		else {
			if (!this.isMappedToGatheringSource()) {
				this.markContenders();
			}
		}
	}

	/**
	 * Function for assigning a construction job to a worker Unit and changing
	 * his current state to AWAIT_CONFIRMATION.
	 */
	protected void assignConstructionJob() {
		// Reset first or the assigned building type will be removed!
		this.resetActions();
		this.assignedBuildingType = this.informationPreserver.getWorkerConfig().getBuildingQueue().poll();

		// Reserve the resources for the construction.
		this.informationPreserver.getResourceReserver().reserveMinerals(this.assignedBuildingType.mineralPrice());
		this.informationPreserver.getResourceReserver().reserveGas(this.assignedBuildingType.gasPrice());
		this.personalReservedMinerals = this.assignedBuildingType.mineralPrice();
		this.personalReservedGas = this.assignedBuildingType.gasPrice();

		// Await the confirmation of the construction (by mapping the Unit
		// to a UnitType).
		this.currentConstructionState = ConstructionState.AWAIT_CONFIRMATION;
	}

	/**
	 * Function for testing if the Unit is mapped to any particular gathering
	 * source.
	 * 
	 * @return true or false depending if the Unit is mapped to a gathering
	 *         source.
	 */
	protected boolean isMappedToGatheringSource() {
		final Unit mappedUnit = this.unit;
		final HashSet<Unit> mappedSource = new HashSet<>();

		// Get all assigned gathering source(s) for this Unit.
		this.informationPreserver.getWorkerConfig().getMappedAccessibleGatheringSources().forEach(new BiConsumer<Unit, ArrayList<Unit>>() {
			@Override
			public void accept(Unit unit, ArrayList<Unit> set) {
				if (set.contains(mappedUnit)) {
					mappedSource.add(unit);
				}
			}
		});

		return !mappedSource.isEmpty();
	}

	/**
	 * Function for resetting everything assigned for a construction of a
	 * building. If the construction flag was not set, the UnitType is queued
	 * again since the building was not constructed / did not start being
	 * constructed.
	 */
	protected void resetAwaitedConstruction() {
		// Flag is not set = construction has not started
		if (!this.constructingFlag) {
			this.informationPreserver.getWorkerConfig().getBuildingQueue().add(this.assignedBuildingType);
			this.freeResources();

			// TODO: REMOVE extra Information
			System.out.println("Queued again: " + this.unit + " " + this.assignedBuildingType);
		} else {
			this.constructingFlag = false;
			this.assignedBuilding = null;
		}

		this.assignedBuildingType = null;
	}

	/**
	 * Mark a gathering source as contender so that no other worker can set it
	 * as their closest free gathering source. This can only be done if the Unit
	 * is currently not mapped to an actual accessible gathering source.
	 */
	protected void markContenders() {
		Unit mineralField = this.findClosestFreeMineralField();
		Unit gasSource = this.findClosestFreeGasSource();

		// Create new entries if necessary.
		if (!this.informationPreserver.getWorkerConfig().getMappedSourceContenders().containsKey(mineralField)) {
			this.informationPreserver.getWorkerConfig().getMappedSourceContenders().put(mineralField, new ArrayList<Unit>());
		}
		if (!this.informationPreserver.getWorkerConfig().getMappedSourceContenders().containsKey(gasSource)) {
			this.informationPreserver.getWorkerConfig().getMappedSourceContenders().put(gasSource, new ArrayList<Unit>());
		}
		if (!this.informationPreserver.getWorkerConfig().getMappedAccessibleGatheringSources().containsKey(mineralField)) {
			this.informationPreserver.getWorkerConfig().getMappedAccessibleGatheringSources().put(mineralField, new ArrayList<Unit>());
		}
		if (!this.informationPreserver.getWorkerConfig().getMappedAccessibleGatheringSources().containsKey(gasSource)) {
			this.informationPreserver.getWorkerConfig().getMappedAccessibleGatheringSources().put(gasSource, new ArrayList<Unit>());
		}

		// If a space for gathering a resource is free, set this Unit as a
		// contender for the spot. Contended sources are assigned to a Unit
		// while the action is executed so that, if the found source has no
		// free spot, a new one will be eventually found in one of the next
		// iterations.
		if (this.informationPreserver.getWorkerConfig().getMappedSourceContenders().get(mineralField).size()
				+ this.informationPreserver.getWorkerConfig().getMappedAccessibleGatheringSources().get(mineralField).size() < this.informationPreserver.getWorkerConfig().getMaxNumberMining()) {
			this.informationPreserver.getWorkerConfig().getMappedSourceContenders().get(mineralField).add(this.unit);
			this.closestFreeMineralField = mineralField;
		}
		if (this.informationPreserver.getWorkerConfig().getMappedSourceContenders().get(gasSource).size()
				+ this.informationPreserver.getWorkerConfig().getMappedAccessibleGatheringSources().get(gasSource).size() < this.informationPreserver.getWorkerConfig().getMaxNumberGatheringGas()) {
			this.informationPreserver.getWorkerConfig().getMappedSourceContenders().get(gasSource).add(this.unit);
			this.closestFreeGasSource = gasSource;
		}
	}

	/**
	 * Function for finding the closest free mineral field.
	 * 
	 * @return the closest free mineral field.
	 */
	protected Unit findClosestFreeMineralField() {
		Unit closestFreeMineralField = null;

		// Get all mineral fields
		for (Unit gatheringSource : this.getUnit().getUnitsInRadius(this.informationPreserver.getWorkerConfig().getPixelGatherSearchRadius())) {
			if (gatheringSource.getType().isMineralField()) {
				closestFreeMineralField = this.checkAgainstMappedAccessibleSources(gatheringSource,
						closestFreeMineralField, this.informationPreserver.getWorkerConfig().getMaxNumberMining());
			}
		}
		return closestFreeMineralField;
	}

	/**
	 * Function for finding the closest free gas source.
	 * 
	 * @return the closest free gas source.
	 */
	protected Unit findClosestFreeGasSource() {
		Unit closestRefinery = null;

		// Get all vaspene geysers
		for (Unit gatheringSource : this.getUnit().getUnitsInRadius(this.informationPreserver.getWorkerConfig().getPixelGatherSearchRadius())) {
			if (gatheringSource.getType().isRefinery()) {
				closestRefinery = this.checkAgainstMappedAccessibleSources(gatheringSource, closestRefinery,
						this.informationPreserver.getWorkerConfig().getMaxNumberGatheringGas());
			}
		}
		return closestRefinery;
	}

	/**
	 * Function for checking if a Unit can be mapped to a gathering source.
	 * Conditions are that the amount of Units already gathering there has to be
	 * less than a set threshold as well as the check against an already
	 * existing reference Unit, which is a gathering source of the same type
	 * with a set distance to the PlayerUnitWorker (Unit can be null => first
	 * other gathering source will be set). If no entry for the source is found,
	 * a new one is generated.
	 * 
	 * @param gatheringSource
	 *            the gathering source.
	 * @param referenceUnit
	 *            the currently chosen closest gathering source.
	 * @param workerThreshold
	 *            the threshold of workers, that can work at the specific
	 *            gathering source.
	 * @return the reference Unit if the distance to the new gathering source is
	 *         greater than the reference value or the threshold is reached. Or
	 *         the gathering source, if the threshold is not yet reached and the
	 *         distance is smaller than the distance towards the reference Unit.
	 */
	protected Unit checkAgainstMappedAccessibleSources(Unit gatheringSource, Unit referenceUnit, int workerThreshold) {
		// Create a new entry in the map if no other entry for the gathering
		// source is found.
		if (!this.informationPreserver.getWorkerConfig().getMappedAccessibleGatheringSources().containsKey(gatheringSource)) {
			this.informationPreserver.getWorkerConfig().getMappedAccessibleGatheringSources().put(gatheringSource, new ArrayList<Unit>());
		}

		ArrayList<Unit> mappedUnits = this.informationPreserver.getWorkerConfig().getMappedAccessibleGatheringSources().get(gatheringSource);

		// If the threshold is not reached, the Unit can gather there.
		if (mappedUnits.size() < workerThreshold && (referenceUnit == null
				|| this.unit.getDistance(gatheringSource) < this.unit.getDistance(referenceUnit))) {
			return gatheringSource;
		}
		return referenceUnit;
	}

	// ------------------------------ Getter / Setter

	public Unit getClosestFreeMineralField() {
		return closestFreeMineralField;
	}

	public Unit getClosestFreeGasSource() {
		return closestFreeGasSource;
	}

	public UnitType getAssignedBuildingType() {
		return assignedBuildingType;
	}

	public void setConstructingFlag(Unit building) {
		this.constructingFlag = true;
		this.assignedBuilding = building;
		this.resourcesResettable = true;
	}

	public int getPersonalReservedMinerals() {
		return personalReservedMinerals;
	}

	public int getPersonalReservedGas() {
		return personalReservedGas;
	}

	public ConstructionState getCurrentConstructionState() {
		return currentConstructionState;
	}

	public Unit getAssignedBuilding() {
		return assignedBuilding;
	}

	public boolean isAssignedToSout() {
		return assignedToSout;
	}
	
	// TODO: UML
	public InformationPreserver getInformationPreserver() {
		return informationPreserver;
	}

}
