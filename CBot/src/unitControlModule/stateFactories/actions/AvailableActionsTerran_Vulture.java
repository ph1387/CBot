package unitControlModule.stateFactories.actions;

import unitControlModule.stateFactories.actions.executableActions.TerranVulture_SpiderMines_RepositionEnemy;
import unitControlModule.stateFactories.actions.executableActions.abilities.AbilityActionTerranVuture_SpiderMines;

/**
 * AvailableActionsTerran_Vulture.java --- HashSet containing all Terran_Vulture
 * Actions.
 * 
 * @author P H - 04.07.2017
 *
 */
public class AvailableActionsTerran_Vulture extends AvailableActionsDefault {

	public AvailableActionsTerran_Vulture() {
		this.add(new AbilityActionTerranVuture_SpiderMines(null));
		this.add(new TerranVulture_SpiderMines_RepositionEnemy(null));
	}
}
