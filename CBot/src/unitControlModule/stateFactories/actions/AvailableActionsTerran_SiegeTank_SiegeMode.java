package unitControlModule.stateFactories.actions;

import java.util.HashSet;

import javaGOAP.GoapAction;
import unitControlModule.stateFactories.actions.executableActions.AttackUnitActionTerran_SiegeTank_Bombard;
import unitControlModule.stateFactories.actions.executableActions.TerranSiegeTank_SiegeMode_Reposition;
import unitControlModule.stateFactories.actions.executableActions.TerranSiegeTank_SiegeMode_WaitForExpectedEnemy;
import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranSiegeTank_TankMode;

/**
 * AvailableActionsTerran_SiegeTank_SiegeMode.java --- HashSet containing all
 * Terran_SiegeTank_SiegeMode Actions.
 * 
 * @author P H - 24.06.2017
 *
 */
public class AvailableActionsTerran_SiegeTank_SiegeMode extends HashSet<GoapAction> {

	public AvailableActionsTerran_SiegeTank_SiegeMode() {
		this.add(new AttackUnitActionTerran_SiegeTank_Bombard(null));
		this.add(new AbilityActionTerranSiegeTank_TankMode(null));

		this.add(new TerranSiegeTank_SiegeMode_Reposition(null));

		this.add(new TerranSiegeTank_SiegeMode_WaitForExpectedEnemy(null));
	}
}
