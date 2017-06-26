package unitControlModule.stateFactories.actions.executableActions;

import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;

/**
 * AttackUnitActionTerran_Marine_Stimmed.java --- Improved AttackUnitAction for
 * a Terran_Marine that is able to use a StimPack.
 * 
 * @author P H - 26.06.2017
 *
 */
public class AttackUnitActionTerran_Marine_Stimmed extends AttackUnitAction {

	/**
	 * @param target
	 *            type: Unit
	 */
	public AttackUnitActionTerran_Marine_Stimmed(Object target) {
		super(target);

		this.addPrecondition(new GoapState(0, "isStimmed", true));
	}

	// -------------------- Functions

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 1;
	}

}
