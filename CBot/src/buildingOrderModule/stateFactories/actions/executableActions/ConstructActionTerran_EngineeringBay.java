package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

// TODO: UML ADD
/**
 * ConstructActionTerran_EngineeringBay.java --- Construction action for a
 * Terran_Engineering_Bay Unit.
 * 
 * @author P H - 22.09.2017
 *
 */
public class ConstructActionTerran_EngineeringBay extends ConstructBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstructActionTerran_EngineeringBay(Object target) {
		super(target);

		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Bio_Units);
		this.addToGameStates(GameState.Upgrade_Focused);
		
		this.addToGameStates(GameState.ResearchBioUnits);
		this.addToGameStates(GameState.UpgradeBioUnits);
		this.addToGameStates(GameState.SpecificImprovementFacility_Terran_Engineering_Bay);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Engineering_Bay;
	}

	// TODO: UML ADD
	@Override
	public int defineMaxSimulationOccurrences() {
		return 1;
	}

}
