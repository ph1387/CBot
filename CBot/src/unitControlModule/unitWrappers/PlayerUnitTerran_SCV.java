package unitControlModule.unitWrappers;

import java.util.Arrays;
import java.util.List;

import bwapi.Unit;
import bwapi.UnitType;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.StateFactoryTerran_SCV;
import workerManagerConstructionJobDistribution.WorkerManagerConstructionJobDistribution;
import workerManagerResourceSpotAllocation.WorkerManagerResourceSpotAllocation;

/**
 * PlayerUnitTerran_SCV.java --- Terran SCV Class.
 * 
 * @author P H - 25.03.2017
 *
 */
public class PlayerUnitTerran_SCV extends PlayerUnitWorker {

	private static final int REPAIR_PIXEL_DISTANCE = 128;
	// Flag for workers that are repairing / are allowed to repair machine Units
	// on the battlefield.
	private boolean isCombatEngineer = false;

	private double combatEngineerTriggerPercentageEnroll = 0.2;
	private double combatEngineerTriggerPercentageCancel = 0.8;
	// The confidence multiplier for combat engineers following or repairing
	// Units.
	private double combatEngineerConfidenceMultiplier = 2.;

	// The machines that are repairable besides the buildings. Vultures are not
	// listed here since repairing them can be difficult due to them moving too
	// fast.
	private static List<UnitType> RepairableUnitTypes = Arrays.asList(new UnitType[] {
			UnitType.Terran_Siege_Tank_Siege_Mode, UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Goliath });

	public PlayerUnitTerran_SCV(Unit unit, InformationStorage informationStorage,
			WorkerManagerResourceSpotAllocation workerManagerResourceSpotAllocation,
			WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution) {
		super(unit, informationStorage, workerManagerResourceSpotAllocation, workerManagerConstructionJobDistribution);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_SCV();
	}

	@Override
	public void update() {
		boolean wasCombatEngineer = this.isCombatEngineer;

		// Switch from a "normal" worker to being a combat engineer or vice
		// versa.
		if (!this.isCombatEngineer && this.shouldBecomeCombatEngineer() && !this.unit.isGatheringGas()) {
			this.isCombatEngineer = true;
			this.informationStorage.getWorkerConfig().incrementCombatEngineerCount();
		}
		if (this.isCombatEngineer && this.shouldStopBeingCombatEngineer()) {
			this.isCombatEngineer = false;
			this.informationStorage.getWorkerConfig().decrementCombatEngineerCount();
		}

		// Needed for the Unit to take action when the state changes!
		if (this.isCombatEngineer && !wasCombatEngineer) {
			this.resetActions();
		}

		super.update();
	}

	/**
	 * Function for determining if a worker should become a combat engineer and
	 * therefore repair machine Units on the battlefield and actively follow
	 * them. This is based on various aspects of the game like the total number
	 * of worker Units, the number of centers or the number of machine Units
	 * that can be repaired.
	 * 
	 * @return true if the Unit should become a combat engineer, false if not.
	 */
	private boolean shouldBecomeCombatEngineer() {
		double machineUnitsCount = this.getNumberOfMachineUnits();
		double combatEngineers = this.informationStorage.getWorkerConfig().getTotalCombatEngineerCount();

		return combatEngineers / machineUnitsCount <= this.combatEngineerTriggerPercentageEnroll;
	}

	/**
	 * Function for determining if a worker should stop being a combat engineer.
	 * This can be necessary when either a lot of machine or worker Units die
	 * and therefore a priority shift is needed.
	 * 
	 * @return true if the worker should stop being a combat engineer, otherwise
	 *         false.
	 */
	private boolean shouldStopBeingCombatEngineer() {
		double machineUnitsCount = this.getNumberOfMachineUnits();
		double combatEngineers = this.informationStorage.getWorkerConfig().getTotalCombatEngineerCount();

		return combatEngineers / machineUnitsCount >= this.combatEngineerTriggerPercentageCancel;
	}

	/**
	 * Function for getting the total number of machine Units currently in game
	 * that a Terran_SCV is can repair.
	 * 
	 * @return the number of Player machine Units that can be repaired by a
	 *         Terran_SCV.
	 */
	private int getNumberOfMachineUnits() {
		int machineUnitsCount = 0;

		for (UnitType unitType : RepairableUnitTypes) {
			machineUnitsCount += this.informationStorage.getCurrentGameInformation().getCurrentUnitCounts()
					.getOrDefault(unitType, 0);
		}
		return machineUnitsCount;
	}

	@Override
	protected double generateModifiedConfidence() {
		boolean isRepairing = this.unit.isRepairing()
				|| this.informationStorage.getWorkerConfig().getUnitMapperRepair().isMapped(this.unit);
		boolean isFollowing = this.unit.isFollowing()
				|| this.informationStorage.getWorkerConfig().getUnitMapperFollow().isMapped(this.unit);
		double modifiedConfidence = super.generateModifiedConfidence();

		// Only apply changes to the confidence if the Unit is a combat engineer
		// and actually following / repairing a Unit. This is due to idling
		// combat engineers not being allowed to retreat easily when their
		// target is attacked.
		if (this.isCombatEngineer && (isRepairing || isFollowing)) {
			modifiedConfidence *= this.combatEngineerConfidenceMultiplier;
		}

		return modifiedConfidence;
	}

	@Override
	public void destroy() {
		super.destroy();

		if (this.isCombatEngineer) {
			this.isCombatEngineer = false;
			this.informationStorage.getWorkerConfig().decrementCombatEngineerCount();
		}
	}

	// ------------------------------ Getter / Setter

	public static int getRepairPixelDistance() {
		return REPAIR_PIXEL_DISTANCE;
	}

	public static List<UnitType> getRepairableUnitTypes() {
		return RepairableUnitTypes;
	}

	public boolean isCombatEngineer() {
		return isCombatEngineer;
	}
}
