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

	// The distance below which the Unit's confidence drastically decreases.
	private int minDistance = 64;
	private double notInMinDistanceConfidenceMultiplier = 0.2;
	private double vultureOutRangesMultiplier = 1.5;
	private double vultureOutRangedMultiplier = 0.75;

	public PlayerUnitTerran_Vulture(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);

		// Vultures do NOT need to be grouped with combat Units.
		this.needsGrouping = false;
	}

	// -------------------- Functions

	@Override
	public void update() {
		super.update();

		// Ensure that Vultures NEVER move in a group!
		this.needsGrouping = false;
	}

	/**
	 * Removed the Action reset(s) due to them interfering with the kiting
	 * action of the Terran_Vulture. These cause the Unit to stop and therefore
	 * rendering the action useless.
	 * 
	 * @see unitControlModule.unitWrappers.PlayerUnit#updateConfidenceState()
	 */
	@Override
	protected void updateConfidenceState() {
		if (this.currentConfidenceState == ConfidenceState.UNDER_THRESHOLD
				&& this.confidence >= CONFIDENCE_THRESHHOLD) {
			this.currentConfidenceState = ConfidenceState.ABOVE_THRESHOLD;
		} else if (this.currentConfidenceState == ConfidenceState.ABOVE_THRESHOLD
				&& this.confidence < CONFIDENCE_THRESHHOLD) {
			this.currentConfidenceState = ConfidenceState.UNDER_THRESHOLD;
			this.resetActions();
		}
	}

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
