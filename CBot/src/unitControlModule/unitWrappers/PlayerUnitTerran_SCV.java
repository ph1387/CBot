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

	// TODO: UML ADD
	private static final int REPAIR_PIXEL_DISTANCE = 128;
	// TODO: UML ADD
	// Flag for workers that are repairing / are allowed to repair machine Units
	// on the battlefield.
	private boolean isCombatEngineer = false;

	// TODO: UML ADD
	// The machines that are repairable besides the buildings.
	private static List<UnitType> RepairableUnitTypes = Arrays
			.asList(new UnitType[] { UnitType.Terran_Siege_Tank_Siege_Mode, UnitType.Terran_Siege_Tank_Tank_Mode,
					UnitType.Terran_Goliath, UnitType.Terran_Vulture });

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

	// TODO: UML ADD
	@Override
	public void update() {
		super.update();

		// Switch from a "normal" worker to being a combat engineer or vice
		// versa.
		if (!this.isCombatEngineer && this.shouldBecomeCombatEngineer()) {
			this.isCombatEngineer = true;
			this.informationStorage.getWorkerConfig().incrementCombatEngineerCount();
		} else if (this.isCombatEngineer && this.shouldStopBeingCombatEngineer()) {
			this.isCombatEngineer = false;
			this.informationStorage.getWorkerConfig().decrementCombatEngineerCount();
		}
	}

	// TODO: WIP
	// TODO: UML ADD
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
		return false;
	}

	// TODO: WIP
	// TODO: UML ADD
	/**
	 * Function for determining if a worker should stop being a combat engineer.
	 * This can be necessary when either a lot of machine or worker Units die
	 * and therefore a priority shift is needed.
	 * 
	 * @return true if the worker should stop being a combat engineer, otherwise
	 *         false.
	 */
	private boolean shouldStopBeingCombatEngineer() {
		return false;
	}

	// TODO: UML ADD
	@Override
	public void destroy() {
		super.destroy();

		if (this.isCombatEngineer) {
			this.isCombatEngineer = false;
			this.informationStorage.getWorkerConfig().decrementCombatEngineerCount();
		}
	}

	// ------------------------------ Getter / Setter

	// TODO: UML ADD
	public static int getRepairPixelDistance() {
		return REPAIR_PIXEL_DISTANCE;
	}

	// TODO: UML ADD
	public static List<UnitType> getRepairableUnitTypes() {
		return RepairableUnitTypes;
	}

	// TODO: UML ADD
	public boolean isCombatEngineer() {
		return isCombatEngineer;
	}
}
