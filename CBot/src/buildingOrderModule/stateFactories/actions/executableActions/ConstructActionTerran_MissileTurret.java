package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

// TODO: UML ADD
/**
 * ConstructActionTerran_MissileTurret.java --- Action for constructing a
 * Terran_Missile_Turret Unit.
 * 
 * @author P H - 14.03.2018
 *
 */
public class ConstructActionTerran_MissileTurret extends ConstructBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstructActionTerran_MissileTurret(Object target) {
		super(target);

		this.addToGameStates(GameState.Cheap_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Support_Units);
		this.addToGameStates(GameState.Combat_Units);

		this.addToGameStates(GameState.SpecificBuilding_Terran_Missile_Turret);
	}

	// -------------------- Functions

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Missile_Turret;
	}

	@Override
	public int defineMaxSimulationOccurrences() {
		return 1;
	}

}
