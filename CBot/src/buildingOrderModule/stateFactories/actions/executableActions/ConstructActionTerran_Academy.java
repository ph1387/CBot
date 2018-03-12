package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

/**
 * ConstructActionTerran_Academy.java --- Action for constructing a
 * Terran_Academy Unit.
 * 
 * @author P H - 18.09.2017
 *
 */
public class ConstructActionTerran_Academy extends ConstructBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstructActionTerran_Academy(Object target) {
		super(target);

		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Bio_Units);
		this.addToGameStates(GameState.Technology_Focused);
		this.addToGameStates(GameState.Upgrade_Focused);
		
		this.addToGameStates(GameState.ResearchBioUnits);
		this.addToGameStates(GameState.UpgradeBioUnits);
		this.addToGameStates(GameState.SpecificBuilding_Terran_Academy);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Academy;
	}
	
	@Override
	public int defineMaxSimulationOccurrences() {
		return 1;
	}

}
