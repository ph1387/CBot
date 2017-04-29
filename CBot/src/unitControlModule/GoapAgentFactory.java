package unitControlModule;

import bwapi.Unit;
import informationStorage.InformationPreserver;
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
public class GoapAgentFactory {

	// -------------------- Functions

	// TODO: UML EXCEPTION ETC
	// TODO: UML
	// TODO: JAVADOC
	public static GoapAgent createAgent(Unit unit, InformationPreserver informationPreserver) throws UnknownUnitTypeException {
		GoapAgent agent = null;
		
		// TODO: Add more Classes
		switch (unit.getType().toString()) {
		case "Terran_Marine":
			agent = new DefaultGoapAgent(PlayerUnitFactory.createMarine(unit, informationPreserver));
			break;
		case "Terran_Vulture":
			agent = new DefaultGoapAgent(PlayerUnitFactory.createVulture(unit, informationPreserver));
			break;
		case "Terran_Siege_Tank_Tank_Mode":
			agent = new DefaultGoapAgent(PlayerUnitFactory.createSiegeTank(unit, informationPreserver));
			break;
		case "Terran_SCV":
			agent = new DefaultGoapAgent(PlayerUnitFactory.createSCV(unit, informationPreserver));
			break;
		default:
			break;
		}

		if(agent == null) {
			throw new UnknownUnitTypeException();
		} else {
			return agent;
		}
	}
}
