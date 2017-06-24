package unitControlModule.stateFactories.actions.executableActions.abilities;

import bwapi.TechType;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * AbilityActionTerranMarine_StimPack.java --- The StimPack ability of a
 * Terran_Marine.
 * 
 * @author P H - 23.06.2017
 *
 */
public class AbilityActionTerranMarine_StimPack extends AbilityActionTechTargetNone {

	// Since the ability costs health make sure the Unit has enough hit points
	// left to either escape or fight!
	private static final int MIN_HEALTH = 30;

	public AbilityActionTerranMarine_StimPack(Object target) {
		super(target);

		this.addPrecondition(new GoapState(0, "canUseStimPack", true));
	}

	// -------------------- Functions

	@Override
	protected TechType defineType() {
		return TechType.Stim_Packs;
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		boolean isHealthMatched = ((PlayerUnit) goapUnit).getUnit().getHitPoints() > MIN_HEALTH;
		boolean isEnemyNear = !((PlayerUnit) goapUnit).getAllEnemyUnitsInWeaponRange().isEmpty();
		boolean isNotStimmed = !((PlayerUnit) goapUnit).getUnit().isStimmed();

		return isHealthMatched && isEnemyNear && isNotStimmed;
	}
}
