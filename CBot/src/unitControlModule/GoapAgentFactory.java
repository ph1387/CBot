package unitControlModule;

import bwapi.Unit;
import bwapi.UnitType;
import javaGOAP.DefaultGoapAgent;
import javaGOAP.GoapAgent;
import unitControlModule.unitWrappers.PlayerUnitFactory;

/**
 * GoapAgentFactory.java --- Factory used to create GoapAgents based on the
 * provided UnitType.
 * 
 * @author P H - 26.02.2017
 *
 */
class GoapAgentFactory {

	// -------------------- Functions

	public static GoapAgent createAgent(Unit unit) throws Exception {
		GoapAgent agent = null;
		
		// TODO: Add more Classes
		// TODO: Possible Change: Implementation change!
		if(unit.getType() == UnitType.Terran_Marine) {
			agent = new DefaultGoapAgent(PlayerUnitFactory.createMarine(unit));
		}

		if(agent == null) {
			throw new Exception("Unknown / Undefined UnitType.");
		} else {
			return agent;
		}
	}
}
