package unitControlModule.stateFactories.actions.executableActions.abilities;

import bwapi.TechType;
import bwapi.Unit;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * AbilityActionTerranScienceVessel_DefensiveMatrix.java --- The
 * Defensiv_eMatrix ability of a Terran_Science_Vessel.
 * 
 * @author P H - 06.10.2017
 *
 */
public class AbilityActionTerranScienceVessel_DefensiveMatrix extends AbilityActionTechTargetUnit {

	/**
	 * @param target
	 *            type: Unit
	 */
	public AbilityActionTerranScienceVessel_DefensiveMatrix(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "shielding", true));
		this.addPrecondition(new GoapState(0, "isFollowingUnit", true));
		this.addPrecondition(new GoapState(0, "isNearSupportableUnit", true));
	}

	// -------------------- Functions

	@Override
	protected TechType defineType() {
		return TechType.Defensive_Matrix;
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		Unit target = (Unit) this.target;
		boolean isAlive = target.exists() && target.getHitPoints() > 0;
		boolean isNotDefensiveMatrixed = !target.isDefenseMatrixed();
		boolean hasEnoughEnergy = ((PlayerUnit) goapUnit).getUnit().getEnergy() >= this.defineType().energyCost();
		boolean isUnderAttack = ((Unit) this.target).isUnderAttack();

		return this.target != null && isAlive && isNotDefensiveMatrixed && hasEnoughEnergy && isUnderAttack;
	}

}
