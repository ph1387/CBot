package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.TechType;

/**
 * ResearchActionTerran_SiegeMode.java --- Action for researching the Siege_Mode
 * for Terran_SiegeTanks at the Terran_Machine_Shop.
 * 
 * @author P H - 30.04.2017
 *
 */
public class ResearchActionTerran_SiegeMode extends ResearchBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ResearchActionTerran_SiegeMode(Object target) {
		super(target);
		
		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Machine_Units);
		this.addToGameStates(GameState.Technology_Focused);
		
		this.addToGameStates(GameState.ResearchMachineUnits);
		this.addToGameStates(GameState.SpecificTech_Tank_Siege_Mode);
	}

	// -------------------- Functions

	@Override
	protected TechType defineType() {
		return TechType.Tank_Siege_Mode;
	}
	
}
