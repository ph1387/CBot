package buildingOrderModule.stateFactories.actions.executableActions;

import bwapi.TechType;

/**
 * ResearchActionTerran_SiegeMode.java --- Action for researching the Siege_Mode
 * for Terran_SiegeTanks.
 * 
 * @author P H - 30.04.2017
 *
 */
public class ResearchActionTerran_SiegeMode extends ResearchBaseAction {

	/**
	 * @param target
	 *            type: Integer
	 */
	public ResearchActionTerran_SiegeMode(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected TechType defineType() {
		return TechType.Tank_Siege_Mode;
	}
	
}
