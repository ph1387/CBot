package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.simulator.TypeWrapper;
import bwapi.UnitType;

// TODO: UML ADD
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
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Academy;
	}

	// TODO: UML ADD
	@Override
	public TypeWrapper defineRequiredType() {
		return TypeWrapper.generateFrom(UnitType.Terran_Barracks);
	}
}
