package unitControlModule;

import bwapi.Unit;
import informationStorage.InformationStorage;
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

	/**
	 * Main function for creating a GoapAgent using a Unit and a general
	 * InformationStorage.
	 * 
	 * @param unit
	 *            the Unit that the GoapAgent is assigned to.
	 * @param informationStorage
	 *            the InformationStorage that stores all important information.
	 * @return a GoapAgent based on the provided information or null if the
	 *         UnitType of the provided Unit is not known.
	 * @throws UnknownUnitTypeException
	 *             Exception indicating that the provided Unit has a not
	 *             supported UnitType.
	 */
	public static GoapAgent createAgent(Unit unit, InformationStorage informationStorage)
			throws UnknownUnitTypeException {
		GoapAgent agent = null;

		// TODO: Add more Classes
		switch (unit.getType().toString()) {
		case "Terran_Marine":
			agent = new DefaultGoapAgent(PlayerUnitFactory.createMarine(unit, informationStorage));
			break;
		case "Terran_Firebat":
			agent = new DefaultGoapAgent(PlayerUnitFactory.createFirebat(unit, informationStorage));
			break;
		case "Terran_Vulture":
			agent = new DefaultGoapAgent(PlayerUnitFactory.createVulture(unit, informationStorage));
			break;
		case "Terran_Siege_Tank_Tank_Mode":
			agent = new DefaultGoapAgent(PlayerUnitFactory.createSiegeTank(unit, informationStorage));
			break;
		case "Terran_Siege_Tank_Siege_Mode":
			agent = new DefaultGoapAgent(PlayerUnitFactory.createSiegeTankSiegeMode(unit, informationStorage));
			break;
		case "Terran_SCV":
			agent = new DefaultGoapAgent(PlayerUnitFactory.createSCV(unit, informationStorage));
			break;
		default:
			break;
		}

		if (agent == null) {
			throw new UnknownUnitTypeException();
		} else {
			return agent;
		}
	}
}
