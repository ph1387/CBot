package unitControlModule.stateFactories.actions.executableActions;

import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * AttackActionGeneralSuperclass.java --- Superclass for attacking actions for simplifying the corresponding functions.
 * @author P H - 04.07.2017
 *
 */
public abstract class AttackActionGeneralSuperclass extends BaseAction {

	/**
	 * @param target type: Unit / TilePosition
	 */
	public AttackActionGeneralSuperclass(Object target) {
		super(target);
		
		this.addEffect(new GoapState(0, "destroyUnit", true));
		this.addPrecondition(new GoapState(0, "enemyKnown", true));
		this.addPrecondition(new GoapState(0, "allowFighting", true));
	}

	// -------------------- Functions

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		boolean isConfidenceLow = ((PlayerUnit) goapUnit).isConfidenceBelowThreshold();
		
		return this.target == null || isConfidenceLow || this.isSpecificDone(goapUnit);
	}
	
	protected abstract boolean isSpecificDone(IGoapUnit goapUnit);
	
	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		boolean isConfident = ((PlayerUnit) goapUnit).isConfidenceAboveThreshold();
		
		return this.target != null && isConfident && this.checkProceduralSpecificPrecondition(goapUnit);
	}
	
	protected abstract boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit);

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 10;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0;
	}
	
	@Override
	protected void resetSpecific() {
		
	}

	@Override
	protected boolean isInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected boolean requiresInRange(IGoapUnit goapUnit) {
		return false;
	}
}
