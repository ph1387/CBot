package unitControlModule.stateFactories.actions.executableActions.abilities;

import bwapi.TechType;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * AbilityActionTerranMedic_Heal.java --- The Heal ability of a Terran_Medic.
 * 
 * @author P H - 01.07.2017
 *
 */
public class AbilityActionTerranMedic_Heal extends AbilityActionTechTargetUnit {

	/**
	 * @param target
	 *            type: Unit
	 */
	public AbilityActionTerranMedic_Heal(Object target) {
		super(target);
		
		this.addEffect(new GoapState(0, "healing", true));
		this.addPrecondition(new GoapState(0, "isNearSupportableUnit", true));
		this.addPrecondition(new GoapState(0, "isNearHealableUnit", true));
	}

	// -------------------- Functions

	@Override
	protected TechType defineType() {
		return TechType.Healing;
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().getEnergy() > 0;
	}
	
	// TODO: UML ADD
	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 1;
	}
}
