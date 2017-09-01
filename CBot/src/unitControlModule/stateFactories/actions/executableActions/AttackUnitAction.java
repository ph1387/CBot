package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Color;
import bwapi.Unit;
import bwapiMath.Vector;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * AttackUnitAction.java --- An attack action for attacking a single unit.
 * 
 * @author P H - 09.02.2017
 *
 */
public class AttackUnitAction extends AttackActionGeneralSuperclass {

	// TODO: UML CHANGE
	private int maxGroupSize = 10;
	private int maxLeaderTileDistance = 5;

	/**
	 * @param target
	 *            type: Unit
	 */
	public AttackUnitAction(Object target) {
		super(target);

		this.addPrecondition(new GoapState(0, "unitsInRange", true));
	}

	// -------------------- Functions

	@Override
	protected boolean isSpecificDone(IGoapUnit goapUnit) {
		boolean isEnemyDead = !((PlayerUnit) goapUnit).getAllEnemyUnitsInWeaponRange().contains(this.target);

		return isEnemyDead;
	}

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		if (this.actionChangeTrigger) {
			success = ((PlayerUnit) goapUnit).getUnit().attack(((Unit) this.target));
		}

		return success;
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().canAttack((Unit) this.target);
	}

	// -------------------- Group

	@Override
	public boolean canPerformGrouped() {
		return true;
	}

	@Override
	public boolean performGrouped(IGoapUnit groupLeader, IGoapUnit groupMember) {
		// TODO: DEBUG INFO
		(new Vector(((PlayerUnit) groupMember).getUnit().getPosition(), ((Unit) this.target).getPosition()))
				.display(new Color(255, 128, 0));

		return ((PlayerUnit) groupMember).getUnit().attack((Unit) this.target);
	}

	@Override
	public int defineMaxGroupSize() {
		return this.maxGroupSize;
	}

	@Override
	public int defineMaxLeaderTileDistance() {
		return this.maxLeaderTileDistance;
	}
	
}
