package unitControlModule.unitWrappers;

import java.util.HashSet;

import bwapi.Position;
import bwapi.Unit;
import core.CBot;
import core.Core;
import informationStorage.InformationStorage;
import informationStorage.UnitMapper;
import workerManagerConstructionJobDistribution.ConstructionWorker;
import workerManagerConstructionJobDistribution.WorkerManagerConstructionJobDistribution;
import workerManagerResourceSpotAllocation.ResourceManagerEntry;
import workerManagerResourceSpotAllocation.WorkerManagerResourceSpotAllocation;

/**
 * PlayerUnitWorker.java --- Wrapper for a general worker Unit.
 * 
 * @author P H - 29.03.2017
 *
 */
public abstract class PlayerUnitWorker extends PlayerUnitTypeMelee implements ResourceManagerEntry, ConstructionWorker {

	// Enum needed since the worker would otherwise constantly reset his actions
	// ...
	private enum ConstructionState {
		IDLE, APPLIED
	};

	// Information regarding the construction of different buildings:
	private ConstructionState currentConstrcutionState = ConstructionState.IDLE;
	private int constructionStateApplianceTimeStamp = 0;
	private int constructionStateApplianceMaxFrameDiff = 200;

	private WorkerManagerResourceSpotAllocation workerManagerResourceSpotAllocation;
	private WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution;

	protected boolean assignedToSout = false;

	// TODO: UML ADD
	// The Unit that the worker is mapped attacking to.
	private Unit mappedAttackableTargetUnit = null;

	public PlayerUnitWorker(Unit unit, InformationStorage informationStorage,
			WorkerManagerResourceSpotAllocation workerManagerResourceSpotAllocation,
			WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution) {
		super(unit, informationStorage);

		this.informationStorage.getWorkerConfig().incrementTotalWorkerCount();
		this.workerManagerResourceSpotAllocation = workerManagerResourceSpotAllocation;
		this.workerManagerConstructionJobDistribution = workerManagerConstructionJobDistribution;
	}

	// -------------------- Functions

	@Override
	public void resetActions() {
		if (!this.unit.isConstructing()) {
			super.resetActions();
		}
	}

	@Override
	public void update() {
		super.update();

		// Only perform the custom update when there are no enemies in range.
		// This is due to the fact that in this case either a retreat or attack
		// action is necessary and the gathering and construction actions can be
		// ignored.
		if (this.currentRangeState == PlayerUnit.ConfidenceRangeStates.NO_UNIT_IN_RANGE) {
			this.customUpdate();
		}
	}

	/**
	 * Should be called at least one time from the sub class if overwritten. It
	 * is updating all necessary information regarding various tasks the worker
	 * can execute as well as shared information between all workers.
	 */
	protected void customUpdate() {
		// Reset the construction state of the worker if possible / when a
		// specific amount of time passed.
		if (!CBot.getInstance().getWorkerManagerConstructionJobDistribution().isAssignedConstructing(this)
				&& this.currentConstrcutionState == ConstructionState.APPLIED
				&& Core.getInstance().getGame().getFrameCount()
						- this.constructionStateApplianceTimeStamp >= this.constructionStateApplianceMaxFrameDiff) {
			this.currentConstrcutionState = ConstructionState.IDLE;
		}

		// Act differently if the worker is / must be assigned scouting.
		if (!this.informationStorage.getWorkerConfig().isWorkerOnceAssignedScouting()
				&& this.informationStorage.getWorkerConfig().getTotalWorkerCount() >= this.informationStorage
						.getWorkerConfig().getWorkerScoutingTrigger()
				&& !CBot.getInstance().getWorkerManagerConstructionJobDistribution().isAssignedConstructing(this)
				&& !this.unit.isGatheringGas() && !this.unit.isConstructing()
				&& this.currentConstrcutionState == ConstructionState.IDLE) {
			this.informationStorage.getWorkerConfig().setWorkerOnceAssignedScouting(true);
			this.assignedToSout = true;

			this.resetActions();
		} else if (!this.assignedToSout && !this.unit.isGatheringGas()) {
			if (!CBot.getInstance().getWorkerManagerConstructionJobDistribution().isAssignedConstructing(this)
					&& CBot.getInstance().getWorkerManagerConstructionJobDistribution().canConstruct()
					&& this.currentConstrcutionState == ConstructionState.IDLE) {
				this.currentConstrcutionState = ConstructionState.APPLIED;
				this.constructionStateApplianceTimeStamp = Core.getInstance().getGame().getFrameCount();

				this.resetActions();
			}
		}
	}

	// TODO: UML ADD
	@Override
	protected Unit generateAttackableEnemyUnitToReactTo() {
		Unit unitToReactTo = null;

		if (this.assignedToSout) {
			unitToReactTo = super.generateAttackableEnemyUnitToReactTo();
		} else {
			// Refresh any previously mapped enemy Units.
			if (this.mappedAttackableTargetUnit != null) {
				this.mappedAttackableTargetUnit = this
						.tryMappingExecutingUnitToTargetOne(this.mappedAttackableTargetUnit);
			}

			// Try finding a new Unit that the worker can attack / be mapped to.
			// If one is found, the enemy Unit is most certainly in the Player's
			// base.
			if (this.mappedAttackableTargetUnit == null && this.closestAttackableEnemyUnitInConfidenceRange != null) {
				for (Unit unit : this.getAllEnemyUnitsInConfidenceRange()) {
					Unit mappingTargetUnit = tryMappingExecutingUnitToTargetOne(unit);

					if (mappingTargetUnit != null) {
						this.mappedAttackableTargetUnit = mappingTargetUnit;
						this.resetActions();

						break;
					}
				}
			}

			unitToReactTo = this.mappedAttackableTargetUnit;
		}

		return unitToReactTo;
	}

	// TODO: UML ADD
	/**
	 * Function for mapping a worker Unit to another target Unit. This function
	 * utilizes a {@link UnitMapper} to ensure that only one single worker is
	 * mapped to a potential target at a time.
	 * 
	 * @param targetUnit
	 *            the Unit that the worker Unit is going to be mapped to if
	 *            possible.
	 * @return if either the Unit was successfully mapped to the target one or
	 *         already is the returned Unit is the targetUnit provided by the
	 *         caller. Otherwise null, since the mapping of the worker failed.
	 */
	private Unit tryMappingExecutingUnitToTargetOne(Unit targetUnit) {
		UnitMapper unitMapper = this.informationStorage.getWorkerConfig().getUnitMapperAttack();
		// PlayerUnit must not be the unitMappingTheTarget!
		Unit unitMappingTheTarget = unitMapper.getMappingUnit(targetUnit);
		Unit targetMappedByPlayerUnit = unitMapper.getMappedUnit(this.unit);
		Unit returnUnit = null;

		// Enemy ran away / is dead.
		if (targetUnit == null && unitMapper.isMapped(this.unit)) {
			unitMapper.unmapUnit(this.unit);
		}

		// Own Unit dead, enemy Unit lives.
		if (unitMapper.isBeingMapped(targetUnit) && unitMappingTheTarget != null && !unitMappingTheTarget.exists()) {
			unitMapper.unmapUnit(unitMapper.getMappingUnit(targetUnit));
		}
		// Own Unit lives, enemy Unit dead.
		if (targetMappedByPlayerUnit != null && !targetMappedByPlayerUnit.exists()) {
			unitMapper.unmapUnit(this.unit);
		}

		// Target is mapped to player Unit.
		if (unitMapper.isBeingMapped(targetUnit)) {
			if (unitMapper.getMappingUnit(targetUnit) == this.unit) {
				returnUnit = targetUnit;
			}
		}
		// PlayerUnit is (re-)mapped.
		else {
			if (unitMapper.isMapped(this.unit)) {
				unitMapper.unmapUnit(this.unit);
			}

			unitMapper.mapUnit(this.unit, targetUnit);
			returnUnit = targetUnit;
		}

		return returnUnit;
	}

	// TODO: UML ADD
	@Override
	public HashSet<Unit> getAllEnemyUnitsInWeaponRange() {
		HashSet<Unit> returnedHashSet;

		// A mapped enemy Unit should be targeted at all cost to ensure that the
		// worker keeps following / attacking it.
		if (this.mappedAttackableTargetUnit != null) {
			returnedHashSet = new HashSet<>();
			returnedHashSet.add(this.mappedAttackableTargetUnit);
		} else {
			returnedHashSet = super.getAllEnemyUnitsInWeaponRange();
		}
		return returnedHashSet;
	}

	@Override
	public void destroy() {
		super.destroy();

		this.informationStorage.getWorkerConfig().decrementTotalWorkerCount();
		// Free any reserved Unit from the worker UnitMapper.
		this.informationStorage.getWorkerConfig().getUnitMapperAttack().unmapUnit(this.unit);
	}

	// ------------------------------ ResourceManagerEntry

	@Override
	public Position getPosition() {
		return this.unit.getPosition();
	}

	// ------------------------------ Getter / Setter

	public WorkerManagerResourceSpotAllocation getWorkerManagerResourceSpotAllocation() {
		return workerManagerResourceSpotAllocation;
	}

	public WorkerManagerConstructionJobDistribution getWorkerManagerConstructionJobDistribution() {
		return workerManagerConstructionJobDistribution;
	}

	public boolean isAssignedToSout() {
		return assignedToSout;
	}

}
