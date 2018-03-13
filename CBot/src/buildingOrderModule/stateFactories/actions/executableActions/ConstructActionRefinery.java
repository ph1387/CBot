package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;
import core.Core;

/**
 * ConstructActionRefinery.java --- Action for constructing a race specific
 * refinery.
 * 
 * @author P H - 30.04.2017
 *
 */
public class ConstructActionRefinery extends ConstructBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstructActionRefinery(Object target) {
		super(target);

		this.addToGameStates(GameState.Building_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Refinery_Units);
		this.addToGameStates(GameState.Cheap_Units);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return Core.getInstance().getPlayer().getRace().getRefinery();
	}

	@Override
	public int defineMaxSimulationOccurrences() {
		return 1;
	}

}
