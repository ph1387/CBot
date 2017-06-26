package unitControlModule.stateFactories.actions.executableActions.abilities;

import bwapi.TechType;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

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

	/**
	 * @param target
	 *            type: Null
	 */
	public AbilityActionTerranMarine_StimPack(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "isStimmed", true));
		this.addPrecondition(new GoapState(0, "mayUseStimPack", true));
	}

	// -------------------- Functions

	@Override
	protected TechType defineType() {
		return TechType.Stim_Packs;
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;
		boolean isHealthMatched = playerUnit.getUnit().getHitPoints() > MIN_HEALTH;
		boolean isEnemyNear = !playerUnit.getAllEnemyUnitsInWeaponRange().isEmpty();
		boolean isNotStimmed = !playerUnit.getUnit().isStimmed();

		return isHealthMatched && isEnemyNear && isNotStimmed;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 1;
	}

}
