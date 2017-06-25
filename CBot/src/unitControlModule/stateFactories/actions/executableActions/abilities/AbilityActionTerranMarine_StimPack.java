package unitControlModule.stateFactories.actions.executableActions.abilities;

import bwapi.TechType;
import bwapi.Unit;
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
	// TODO: UML ADD
	private static final int EXTRA_GROUND_WEAPON_RANGE = 32;

	/**
	 * @param target
	 *            type: Null
	 */
	public AbilityActionTerranMarine_StimPack(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "isStimmed", true));
	}

	// -------------------- Functions

	@Override
	protected TechType defineType() {
		return TechType.Stim_Packs;
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;
		Unit closestEnemy = playerUnit.getClosestEnemyUnitInConfidenceRange();
		
		boolean isHealthMatched = ((PlayerUnit) goapUnit).getUnit().getHitPoints() > MIN_HEALTH;
		boolean isEnemyNear = !((PlayerUnit) goapUnit).getAllEnemyUnitsInWeaponRange().isEmpty();
		boolean isNotStimmed = !((PlayerUnit) goapUnit).getUnit().isStimmed();
		boolean isAllowedToStim = false;
		
		// Only enable the StimPack if the Unit is in range of the enemies
		// ground weapon.
		if (closestEnemy != null && playerUnit.isNearPosition(closestEnemy.getTargetPosition(),
				closestEnemy.getType().groundWeapon().maxRange() + EXTRA_GROUND_WEAPON_RANGE)) {
			isAllowedToStim = true;
		}

		return isAllowedToStim && isHealthMatched && isEnemyNear && isNotStimmed;
	}
}
