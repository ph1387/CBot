package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.TechType;

/**
 * ResearchActionTerran_CloakingField.java --- Action for researching the
 * Cloaking_Field for Terran_Wraiths at the Terran_Control_Tower.
 * 
 * @author P H - 08.10.2017
 *
 */
public class ResearchActionTerran_CloakingField extends ResearchBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ResearchActionTerran_CloakingField(Object target) {
		super(target);

		this.addToGameStates(GameState.Expensive_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Flying_Units);
		this.addToGameStates(GameState.Technology_Focused);

		this.addToGameStates(GameState.ResearchFlyingUnits);
		this.addToGameStates(GameState.SpecificTech_Cloaking_Field);
	}

	// -------------------- Functions

	@Override
	protected TechType defineType() {
		return TechType.Cloaking_Field;
	}

}
