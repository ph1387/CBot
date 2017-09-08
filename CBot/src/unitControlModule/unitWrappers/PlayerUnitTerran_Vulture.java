package unitControlModule.unitWrappers;

import bwapi.Unit;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactoryTerran_Vulture;
import unitControlModule.stateFactories.StateFactory;

/**
 * PlayerUnit_Vulture.java --- Terran Vulture Class.
 * 
 * @author P H - 23.03.2017
 *
 */
public class PlayerUnitTerran_Vulture extends PlayerUnitTypeRanged {

	// TODO: UML ADD
	// The distance below which the Unit's confidence drastically decreases.
	private int minDistance = 64;
	// TODO: UML ADD
	private double notInMinDistanceConfidenceMultiplier = 0.2;
	// TODO: UML ADD
	private double vultureOutRangesMultiplier = 1.5;
	// TODO: UML ADD
	private double vultureOutRangedMultiplier = 0.75;

	public PlayerUnitTerran_Vulture(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);

		// TODO: Possible Change: Update confidenceDefaultRange based on the
		// closestEnemy's weapon range
		this.extraConfidencePixelRangeToClosestUnits = 112;
	}

	// -------------------- Functions

	// TODO: UML ADD
	@Override
	protected double generateConfidence() {
		double generatedConfidence = super.generateConfidence();

		if (this.closestEnemyUnitInConfidenceRange != null) {
			// The vulture outranges the enemy.
			if (this.unit.getType().groundWeapon().maxRange() > this.closestEnemyUnitInConfidenceRange.getType()
					.groundWeapon().maxRange()) {
				generatedConfidence *= this.vultureOutRangesMultiplier;
			}
			// The enemy outranges the vulture.
			else {
				generatedConfidence *= this.vultureOutRangedMultiplier;
			}

			// Decrease the distance drastically if the enemy is near the Unit.
			if (this.unit.getDistance(this.closestEnemyUnitInConfidenceRange) < this.minDistance) {
				generatedConfidence *= this.notInMinDistanceConfidenceMultiplier;
			}
		}
		return generatedConfidence;
	}

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_Vulture();
	}

}
