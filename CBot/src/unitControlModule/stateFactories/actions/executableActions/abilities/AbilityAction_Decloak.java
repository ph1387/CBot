package unitControlModule.stateFactories.actions.executableActions.abilities;

import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * AbilityAction_Decloak.java --- General decloaking ability for Units.
 * 
 * @author P H - 07.10.2017
 *
 */
public abstract class AbilityAction_Decloak extends AbilityActionTechTargetNone {

	/**
	 * @param target
	 *            type: Null
	 */
	public AbilityAction_Decloak(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "isCloaked", false));
		this.addPrecondition(new GoapState(0, "isCloaked", true));
	}

	// -------------------- Functions

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;

		return playerUnit.getUnit().isCloaked() && playerUnit.getUnit().canDecloak();
	}

	// TODO: UML ADD
	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().decloak();
	}

}
