package unitControlModule.unitWrappers;

import bwapi.Unit;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactoryDefault;
import unitControlModule.stateFactories.StateFactory;

/**
 * PlayerUnit_Vulture.java --- Terran Vulture Class.
 * @author P H - 23.03.2017
 *
 */
public class PlayerUnitTerran_Vulture extends PlayerUnit {

	public PlayerUnitTerran_Vulture(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);
		
		// TODO: Possible Change: Update confidenceDefaultRange based on the closestEnemy's weapon range
		this.extraConfidencePixelRangeToClosestUnits = 112;
		this.confidenceDefault = 0.35;
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryDefault();
	}

}
