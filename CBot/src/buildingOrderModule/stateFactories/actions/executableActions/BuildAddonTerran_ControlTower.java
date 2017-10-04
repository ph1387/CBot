package buildingOrderModule.stateFactories.actions.executableActions;

import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.simulator.TypeWrapper;
import bwapi.UnitType;

// TODO: UML ADD
/**
 * BuildAddonTerran_ControlTower.java --- Action for building a
 * Terran_Control_Tower at a Starport.
 * 
 * @author P H - 23.09.2017
 *
 */
public class BuildAddonTerran_ControlTower extends BuildAddonBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public BuildAddonTerran_ControlTower(Object target) {
		super(target);

		this.addToGameStates(GameState.Mineral_Units);
		this.addToGameStates(GameState.Gas_Units);
		this.addToGameStates(GameState.Cheap_Units);
		this.addToGameStates(GameState.Flying_Units);
		this.addToGameStates(GameState.Upgrade_Focused);
		this.addToGameStates(GameState.Technology_Focused);
		
		this.addToGameStates(GameState.UpgradeFlyingUnits);
		this.addToGameStates(GameState.ResearchFlyingUnits);
	}

	// -------------------- Functions

	@Override
	public TypeWrapper defineRequiredType() {
		return TypeWrapper.generateFrom(UnitType.Terran_Starport);
	}

	@Override
	protected UnitType defineType() {
		return UnitType.Terran_Control_Tower;
	}

	// TODO: UML ADD
	@Override
	public int defineMaxSimulationOccurrences() {
		return 1;
	}

}
