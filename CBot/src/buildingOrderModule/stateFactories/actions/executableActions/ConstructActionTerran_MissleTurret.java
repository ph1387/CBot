package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UnitType;

// TODO: UML ADD
/**
 * ConstructActionTerran_MissleTurret.java --- Action for constructing a
 * Terran_Missle_Turret Unit.
 * 
 * @author P H - 14.03.2018
 *
 */
public class ConstructActionTerran_MissleTurret extends ConstructBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ConstructActionTerran_MissleTurret(Object target) {
		super(target);

		this.addToGameStates(GameState.Cheap_Units);
		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Support_Units);
		this.addToGameStates(GameState.Combat_Units);

		this.addToGameStates(GameState.SpecificBuilding_Terran_Missle_Turret);
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
