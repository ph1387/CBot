package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.TechType;

/**
 * ResearchActionTerran_SpiderMines.java --- Action for researching the
 * Spider_Mines for Terran_Vultures at the Terran_Machine_Shop.
 * 
 * @author P H - 23.09.2017
 *
 */
public class ResearchActionTerran_SpiderMines extends ResearchBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ResearchActionTerran_SpiderMines(Object target) {
		super(target);

		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Machine_Units);
		this.addToGameStates(GameState.Technology_Focused);
		
		this.addToGameStates(GameState.ResearchMachineUnits);
		this.addToGameStates(GameState.SpecificTech_Spider_Mines);
	}

	// -------------------- Functions

	@Override
	protected TechType defineType() {
		return TechType.Spider_Mines;
	}

}
