package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

/**
 * ConstructActionTerran_Armory.java --- Construction action for a Terran_Armory
 * Unit.
 * 
 * @author P H - 22.09.2017
 *
 */
public class ConstructActionTerran_Armory extends ConstructBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstructActionTerran_Armory(Object target) {
		super(target);

		this.addToGameStates(GameState.Building_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Machine_Units);
		this.addToGameStates(GameState.Flying_Units);
		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Upgrade_Focused);
		
		this.addToGameStates(GameState.ResearchMachineUnits);
		this.addToGameStates(GameState.UpgradeMachineUnits);
		this.addToGameStates(GameState.ResearchFlyingUnits);
		this.addToGameStates(GameState.UpgradeFlyingUnits);
		
		this.addToGameStates(GameState.SpecificBuilding_Terran_Armory);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Armory;
	}

	@Override
	public int defineMaxSimulationOccurrences() {
		return 1;
	}

}
