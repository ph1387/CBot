package unitControlModule.stateFactories.updater;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import bwapi.Unit;
import bwapi.UnitType;
import unitControlModule.stateFactories.actions.AvailableActionsTerran_SCV;
import unitControlModule.stateFactories.actions.executableActions.FollowActionTerran_SCV;
import unitControlModule.stateFactories.actions.executableActions.worker.RepairActionBuilding;
import unitControlModule.stateFactories.actions.executableActions.worker.RepairActionUnit;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_SCV;

/**
 * ActionUpdaterTerran_SCV.java --- Updater for updating an
 * {@link AvailableActionsTerran_SCV} instance.
 * 
 * @author P H - 25.03.2017
 *
 */
public class ActionUpdaterTerran_SCV extends ActionUpdaterWorker {

	// TODO: UML ADD
	private FollowActionTerran_SCV followActionTerran_SCV;
	// TODO: UML ADD
	private RepairActionUnit repairActionUnit;
	// TODO: UML ADD
	private RepairActionBuilding repairActionBuilding;

	public ActionUpdaterTerran_SCV(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	// TODO: UML ADD
	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);

		this.repairActionBuilding.setTarget(this.getRepairActionTargetBuilding());

		// Only certain workers may repair other Units on the battlefield.
		if (((PlayerUnitTerran_SCV) playerUnit).isCombatEngineer()) {
			this.followActionTerran_SCV.setTarget(this.getClosestFollowableUnit());
			this.repairActionUnit.setTarget(this.getRepairActionTargetUnit());
		}
	}

	// TODO: UML ADD
	/**
	 * Function for finding the closest Unit whose UnitType matches one of the
	 * listed ones in the {@link PlayerUnitTerran_SCV}'s List of repairable
	 * UnitTypes. Only these types of Units are the ones that a worker may
	 * follow.
	 * 
	 * @return the closest Unit that the executing Unit can follow.
	 */
	private Unit getClosestFollowableUnit() {
		List<Unit> followableUnits = new ArrayList<>();
		Unit closestFollowableUnit = null;

		// Add all Units of the Player on the map that match the repairable
		// UnitType to
		// the List.
		for (UnitType unitType : PlayerUnitTerran_SCV.getRepairableUnitTypes()) {
			followableUnits.addAll(this.playerUnit.getInformationStorage().getCurrentGameInformation().getCurrentUnits()
					.getOrDefault(unitType, new HashSet<Unit>()));
		}

		this.sortByDistance(followableUnits, this.playerUnit.getUnit());

		// Find a Unit that is not already being followed by another worker,
		// starting
		// with the closest one.
		// OR
		// Renew the reference to the Unit set in the previous iteration.
		for (Unit unit : followableUnits) {
			if (!this.playerUnit.getInformationStorage().getWorkerConfig().getUnitMapperFollow().isBeingMapped(unit)
					|| this.playerUnit.getInformationStorage().getWorkerConfig().getUnitMapperFollow()
							.getMappingUnit(unit) == this.playerUnit.getUnit()) {
				closestFollowableUnit = unit;

				break;
			}
		}

		return closestFollowableUnit;
	}

	// TODO: UML ADD
	/**
	 * Function for sorting a List containing Units references based on their
	 * distance towards another provided one in an ascending order based on
	 * their distance towards it.
	 * 
	 * @param list
	 *            the List of Units that is going to be sorted.
	 * @param executingUnit
	 *            the reference Unit to which the Units in the sorting List are
	 *            calculating their distance to.
	 * @return the sorted List of Units (Ascending) based on the distance of the
	 *         Units to the provided one.
	 */
	private List<Unit> sortByDistance(List<Unit> list, final Unit executingUnit) {
		// Sort based on the distance to the executing Unit. The closest one is
		// at the
		// beginning of the List.
		list.sort(new Comparator<Unit>() {

			@Override
			public int compare(Unit u1, Unit u2) {
				return Integer.compare(executingUnit.getDistance(u1), executingUnit.getDistance(u2));
			}
		});

		return list;
	}

	// TODO: UML ADD
	/**
	 * Function for finding a target to repair (Unit) that is not already mapped
	 * to another worker Unit in the executing Unit's confidence range. <br>
	 * <b>Note:</b> <br>
	 * The Unit has to either be unmapped or mapped to the executing Unit
	 * itself!
	 * 
	 * @return a Unit that the worker Unit can repair.
	 */
	private Unit getRepairActionTargetUnit() {
		List<Unit> damagedUnits = this.sortByDistance(this.getDamagedUnitsInConfidenceRange(),
				this.playerUnit.getUnit());
		Unit repairActionTarget = null;

		// Try finding a non mapped Unit to use as repair target.
		for (Unit unit : damagedUnits) {
			boolean wasPreviousTarget = this.playerUnit.getInformationStorage().getWorkerConfig().getUnitMapperRepair()
					.getMappingUnit(unit) == this.playerUnit.getUnit();
			boolean isNewTargetAndUnmapped = PlayerUnitTerran_SCV.getRepairableUnitTypes().contains(unit.getType())
					&& !this.playerUnit.getInformationStorage().getWorkerConfig().getUnitMapperRepair()
							.isBeingMapped(unit);

			if (wasPreviousTarget || isNewTargetAndUnmapped) {
				repairActionTarget = unit;

				break;
			}
		}
		return repairActionTarget;
	}

	// TODO: UML ADD
	/**
	 * Function for finding a target to repair (Building) that is not already
	 * mapped to another worker Unit in the executing Unit's confidence range.
	 * <br>
	 * <b>Note:</b> <br>
	 * The Unit has to either be unmapped or mapped to the executing Unit
	 * itself!
	 * 
	 * @return a building that the worker Unit can repair.
	 */
	private Unit getRepairActionTargetBuilding() {
		List<Unit> damagedUnits = this.sortByDistance(this.getDamagedUnitsInConfidenceRange(),
				this.playerUnit.getUnit());
		Unit repairActionTarget = null;

		// Try finding a non mapped building to use as repair target.
		for (Unit unit : damagedUnits) {
			boolean wasPreviousTarget = this.playerUnit.getInformationStorage().getWorkerConfig().getUnitMapperRepair()
					.getMappingUnit(unit) == this.playerUnit.getUnit();
			boolean isNewTargetAndUnmapped = unit.getType().isBuilding() && !this.playerUnit.getInformationStorage()
					.getWorkerConfig().getUnitMapperRepair().isBeingMapped(unit);

			if (wasPreviousTarget || isNewTargetAndUnmapped) {
				repairActionTarget = unit;

				break;
			}
		}
		return repairActionTarget;
	}

	// TODO: UML ADD
	/**
	 * Function for extracting all damaged Player Units from the Collection of
	 * Units in the executing Unit's confidence range.
	 * 
	 * @return a List of damaged Player Units in the executing Unit's confidence
	 *         range.
	 */
	private List<Unit> getDamagedUnitsInConfidenceRange() {
		List<Unit> damagedUnits = new ArrayList<>();

		for (Unit unit : this.playerUnit.getAllPlayerUnitsInConfidenceRange()) {
			if (unit.getHitPoints() < unit.getType().maxHitPoints()) {
				damagedUnits.add(unit);
			}
		}
		return damagedUnits;
	}

	// TODO: UML ADD
	@Override
	protected void init() {
		super.init();

		this.followActionTerran_SCV = ((FollowActionTerran_SCV) this
				.getActionFromInstance(FollowActionTerran_SCV.class));
		this.repairActionUnit = ((RepairActionUnit) this.getActionFromInstance(RepairActionUnit.class));
		this.repairActionBuilding = ((RepairActionBuilding) this.getActionFromInstance(RepairActionBuilding.class));
	}

}
