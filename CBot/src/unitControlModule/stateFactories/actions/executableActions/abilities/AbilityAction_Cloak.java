package unitControlModule.stateFactories.actions.executableActions.abilities;

import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * AbilityAction_Cloak.java --- General cloaking ability for Units.
 * 
 * @author P H - 07.10.2017
 *
 */
public abstract class AbilityAction_Cloak extends AbilityActionTechTargetNone {

	/**
	 * @param target
	 *            type: Null
	 */
	public AbilityAction_Cloak(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "isCloaked", true));
		this.addEffect(new GoapState(0, "isDecloaked", false));
		this.addPrecondition(new GoapState(0, "isCloaked", false));
		this.addPrecondition(new GoapState(0, "isDecloaked", true));
		this.addPrecondition(new GoapState(0, "mayCloak", true));
	}

	// -------------------- Functions

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;

		return !playerUnit.getUnit().isCloaked() && playerUnit.getUnit().canCloak()
				&& playerUnit.getUnit().getEnergy() > this.defineType().energyCost();
	}

	// TODO: UML ADD
	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().cloak();
	}

}
