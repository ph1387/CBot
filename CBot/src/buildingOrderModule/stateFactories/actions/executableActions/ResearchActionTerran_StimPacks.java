package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.TechType;

/**
 * ResearchActionTerran_StimPacks.java --- Action for researching the Stim_Packs
 * for Terran_Marines at the Terran_Academy.
 * @author P H - 18.09.2017
 *
 */
public class ResearchActionTerran_StimPacks extends ResearchBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ResearchActionTerran_StimPacks(Object target) {
		super(target);
		
		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Bio_Units);
		this.addToGameStates(GameState.Technology_Focused);
		
		this.addToGameStates(GameState.ResearchBioUnits);
		this.addToGameStates(GameState.SpecificTech_Stim_Packs);
	}

	// -------------------- Functions

	@Override
	protected TechType defineType() {
		return TechType.Stim_Packs;
	}
	
}
