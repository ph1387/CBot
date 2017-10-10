package unitControlModule.stateFactories.actions.executableActions.worker;

import javaGOAP.GoapState;

// TODO: UML ADD
/**
 * RepairActionUnit.java --- Repair action focused on repairing a Unit. This
 * Class is separated from the {@link RepairActionBuilding} due to it containing
 * different preconditions that a Unit executing this action must meet.
 * 
 * @author P H - 09.10.2017
 *
 */
public class RepairActionUnit extends RepairAction {

	/**
	 * @param target
	 *            type: Unit
	 */
	public RepairActionUnit(Object target) {
		super(target);

		this.addPrecondition(new GoapState(0, "isFollowingUnit", true));
		this.addPrecondition(new GoapState(0, "isNearRepairableUnit", true));
	}

	// -------------------- Functions

}
