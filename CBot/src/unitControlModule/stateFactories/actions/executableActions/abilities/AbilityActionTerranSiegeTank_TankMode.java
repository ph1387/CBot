package unitControlModule.stateFactories.actions.executableActions.abilities;

import bwapi.TechType;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * AbilityActionTerranSiegeTank_TankMode.java --- The TankMode ability of a
 * Terran_SiegeTank.
 * 
 * @author P H - 24.06.2017
 *
 */
public class AbilityActionTerranSiegeTank_TankMode extends AbilityActionTechTargetNone {

	/**
	 * @param target
	 *            type: Null
	 */
	public AbilityActionTerranSiegeTank_TankMode(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "canMove", true));
		this.addEffect(new GoapState(0, "isSieged", false));
		this.addPrecondition(new GoapState(0, "isSieged", true));
	}

	// -------------------- Functions

	@Override
	protected TechType defineType() {
		return null;
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return this.checkProceduralSpecificPrecondition(goapUnit);
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().isSieged();
	}

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		// Try to unsiege until the Unit is able to.
		if (((PlayerUnit) goapUnit).getUnit().canUnsiege()) {
			success = ((PlayerUnit) goapUnit).getUnit().unsiege();

			// Remove the agent to make space for the new TankMode agent.
			if (success) {
				((PlayerUnit) goapUnit).removeCorrespondingAgent();
			}
		}

		return success;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		return !((PlayerUnit) goapUnit).getUnit().isSieged();
	}

}
