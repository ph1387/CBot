package unitControlModule.stateFactories.actions;

import unitControlModule.stateFactories.actions.executableActions.AttackUnitActionTerran_SiegeTank_Bombard;
import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranSiegeTank_SiegeMode;

/**
 * Terran_SiegeTankAvailableActions.java --- HashSet containing all
 * Terran_SiegeTank Actions.
 * 
 * @author P H - 25.03.2017
 *
 */
public class AvailableActionsTerran_SiegeTank extends AvailableActionsDefault {

	public AvailableActionsTerran_SiegeTank() {
		this.add(new AttackUnitActionTerran_SiegeTank_Bombard(null));
		this.add(new AbilityActionTerranSiegeTank_SiegeMode(null));
	}
}
