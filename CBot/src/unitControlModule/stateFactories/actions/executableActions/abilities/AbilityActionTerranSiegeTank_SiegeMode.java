package unitControlModule.stateFactories.actions.executableActions.abilities;

import bwapi.TechType;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * AbilityActionTerranSiegeTank_SiegeMode.java --- The SiegeMode ability of a
 * Terran_SiegeTank.
 * 
 * @author P H - 24.06.2017
 *
 */
public class AbilityActionTerranSiegeTank_SiegeMode extends AbilityActionTechTargetNone {

	/**
	 * @param target
	 *            type: Null
	 */
	public AbilityActionTerranSiegeTank_SiegeMode(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "canMove", false));
		this.addEffect(new GoapState(0, "isSieged", true));
		this.addPrecondition(new GoapState(0, "canMove", true));
		this.addPrecondition(new GoapState(0, "isSieged", false));

		// The tank is FORBIDDEN to transform into siege mode when a Unit is too
		// close to him! This effectively prevents the Unit from senselessly
		// transforming between these two states.
		this.addPrecondition(new GoapState(0, "belowSiegeRange", false));
	}

	// -------------------- Functions

	@Override
	protected TechType defineType() {
		return TechType.Tank_Siege_Mode;
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		return !((PlayerUnit) goapUnit).getUnit().isSieged();
	}

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		// Try to siege until the Unit is able to.
		if (((PlayerUnit) goapUnit).getUnit().canSiege()) {
			success = ((PlayerUnit) goapUnit).getUnit().siege();

			// Remove the agent to make space for the new SiegeMode agent.
			if (success) {
				((PlayerUnit) goapUnit).removeCorrespondingAgent();
			}
		}

		return success;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().isSieged();
	}

}
