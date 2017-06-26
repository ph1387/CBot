package unitControlModule.stateFactories.actions.executableActions;

import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;

/**
 * RetreatActionSteerInGoalDirectionTerran_Marine_Stimmed.java --- Improved RetreatAction for a Terran_Marine that is able to use a StimPack.
 * @author P H - 26.06.2017
 *
 */
public class RetreatActionSteerInGoalDirectionTerran_Marine_Stimmed extends RetreatActionSteerInGoalDirection {

	/**
	 * @param target
	 *            type: Unit
	 */
	public RetreatActionSteerInGoalDirectionTerran_Marine_Stimmed(Object target) {
		super(target);
		
		this.addPrecondition(new GoapState(0, "isStimmed", true));
	}
	
	// -------------------- Functions

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 1;
	}
	
}
