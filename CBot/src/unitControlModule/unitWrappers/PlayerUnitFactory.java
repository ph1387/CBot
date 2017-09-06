package unitControlModule.unitWrappers;

import bwapi.Unit;
import core.CBot;
import informationStorage.InformationStorage;
import workerManagerConstructionJobDistribution.WorkerManagerConstructionJobDistribution;
import workerManagerResourceSpotAllocation.WorkerManagerResourceSpotAllocation;

/**
 * PlayerUnitFactory.java --- Factory used to create different kinds of
 * PlayerUnits.
 * 
 * @author P H - 26.02.2017
 *
 */
public class PlayerUnitFactory {

	private static WorkerManagerResourceSpotAllocation WorkerManagerResourceSpotAllocation = CBot.getInstance()
			.getWorkerManagerResourceSpotAllocation();
	private static WorkerManagerConstructionJobDistribution WorkerManagerConstructionJobDistribution = CBot
			.getInstance().getWorkerManagerConstructionJobDistribution();

	// -------------------- Functions

	public static PlayerUnit createSiegeTank(Unit unit, InformationStorage informationStorage) {
		return new PlayerUnitTerran_SiegeTank(unit, informationStorage);
	}

	public static PlayerUnit createSiegeTankSiegeMode(Unit unit, InformationStorage informationStorage) {
		return new PlayerUnitTerran_SiegeTank_SiegeMode(unit, informationStorage);
	}

	public static PlayerUnit createMarine(Unit unit, InformationStorage informationStorage) {
		return new PlayerUnitTerran_Marine(unit, informationStorage);
	}

	public static PlayerUnit createFirebat(Unit unit, InformationStorage informationStorage) {
		return new PlayerUnitTerran_Firebat(unit, informationStorage);
	}

	public static PlayerUnit createMedic(Unit unit, InformationStorage informationStorage) {
		return new PlayerUnitTerran_Medic(unit, informationStorage);
	}

	public static PlayerUnit createVulture(Unit unit, InformationStorage informationStorage) {
		return new PlayerUnitTerran_Vulture(unit, informationStorage);
	}

	public static PlayerUnit createSCV(Unit unit, InformationStorage informationStorage) {
		return new PlayerUnitTerran_SCV(unit, informationStorage, WorkerManagerResourceSpotAllocation,
				WorkerManagerConstructionJobDistribution);
	}
}
