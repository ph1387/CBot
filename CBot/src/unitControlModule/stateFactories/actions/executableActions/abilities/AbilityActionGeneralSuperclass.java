package unitControlModule.stateFactories.actions.executableActions.abilities;

import bwapi.TechType;
import core.Core;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.stateFactories.actions.executableActions.BaseAction;

/**
 * AbilityActionGeneralSuperclass.java --- Superclass for all abilities of all
 * Races and Units.
 * 
 * @author P H - 23.06.2017
 *
 */
public abstract class AbilityActionGeneralSuperclass extends BaseAction {

	protected TechType ability;

	/**
	 * The target <b>can</b> specify any of the listed types. What type is
	 * exactly used is determined by the ability itself.
	 * 
	 * @param target
	 *            type: Unit, Position, etc.
	 */
	public AbilityActionGeneralSuperclass(Object target) {
		super(target);

		this.addPrecondition(new GoapState(0, "canUseAbilities", true));

		this.ability = this.defineType();
	}

	// -------------------- Functions

	/**
	 * Function which specifies the ability that is going to be used.
	 * 
	 * @return
	 */
	protected abstract TechType defineType();

	@Override
	protected void resetSpecific() {

	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		// Set to 1 by default. This only causes certain combinations of actions
		// to work along with abilities when the Units are allowed to use them.
		return 1.f;
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return this.target != null && Core.getInstance().getPlayer().hasResearched(this.ability)
				&& this.checkProceduralSpecificPrecondition(goapUnit);
	}

	/**
	 * Special check since the preconditions for each ability might differ.
	 * 
	 * @param goapUnit
	 *            the Unit that will perform the ability.
	 * @return true or false depending if the precondition(a) are met.
	 */
	protected abstract boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit);

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		// Exactly perform the ability one single time.
		return this.actionChangeTrigger;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		// Can be overridden if necessary.
		return 0;
	}

	@Override
	protected boolean isInRange(IGoapUnit goapUnit) {
		// Can be overridden if necessary.
		return false;
	}

	@Override
	protected boolean requiresInRange(IGoapUnit goapUnit) {
		// Can be overridden if necessary.
		return false;
	}

	// -------------------- Groups

	@Override
	public boolean canPerformGrouped() {
		return false;
	}

	@Override
	public boolean performGrouped(IGoapUnit groupLeader, IGoapUnit groupMember) {
		return false;
	}

	// TODO: UML ADD
	@Override
	public int defineMaxGroupSize() {
		return 0;
	}
	
	// ------------------------------ Getter / Setter

}
