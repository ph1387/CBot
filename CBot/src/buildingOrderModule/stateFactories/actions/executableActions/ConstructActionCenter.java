package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;
import core.Core;

/**
 * ConstructActionCenter.java --- Action for constructing a race specific center
 * building.
 * 
 * @author P H - 30.04.2017
 *
 */
public class ConstructActionCenter extends ConstructBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstructActionCenter(Object target) {
		super(target);

		this.addToGameStates(GameState.Building_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Expansion_Focused);
		this.addToGameStates(GameState.Expensive_Units);
		
		this.addToGameStates(GameState.IdleTrainingFacility_Center);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return Core.getInstance().getPlayer().getRace().getCenter();
	}

	@Override
	public int defineMaxSimulationOccurrences() {
		return 1;
	}

}
