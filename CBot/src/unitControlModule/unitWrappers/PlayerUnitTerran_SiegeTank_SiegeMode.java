package unitControlModule.unitWrappers;

import bwapi.Unit;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.StateFactoryTerran_SiegeTank_SiegeMode;

/**
 * PlayerUnitTerran_SiegeTank_SiegeMode.java --- Terran_SiegeTank_SiegeMode
 * Class.
 * 
 * @author P H - 24.06.2017
 *
 */
public class PlayerUnitTerran_SiegeTank_SiegeMode extends PlayerUnitTerran_SiegeTank {

	public PlayerUnitTerran_SiegeTank_SiegeMode(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);

		// Is set here due to the waiting action for expected enemies requiring
		// a "true" value. The custom update function does not provide this in
		// time. If the value was falsely set, it is removed by the update
		// function.
		this.isExpectingEnemy = true;
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_SiegeTank_SiegeMode();
	}

}
