package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.GameState;
import bwapi.UnitType;
import core.Core;

//TODO: UML ADD
/**
 * ConstructActionRefinery.java --- Action for constructing a race specific
 * refinery. <br>
 * <b>NOTE:</b> <br>
 * The Simulator does not stop at a single instance of a building and can only
 * receive a single UnitType as condition. Therefore it is not advised to use
 * the Simulator for refineries.
 * 
 * @author P H - 30.04.2017
 *
 */
public class ConstructActionRefinery extends ConstructBaseAction {

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
