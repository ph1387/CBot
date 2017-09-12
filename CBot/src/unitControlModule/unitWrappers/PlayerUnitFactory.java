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

	// TODO: UML ADD
	private InformationStorage informationStorage;

	// TODO: UML NON STATIC RENAME
	private WorkerManagerResourceSpotAllocation workerManagerResourceSpotAllocation = CBot.getInstance()
			.getWorkerManagerResourceSpotAllocation();
	// TODO: UML NON STATIC
	private WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution = CBot.getInstance()
			.getWorkerManagerConstructionJobDistribution();

	// TODO: UML ADD
	public PlayerUnitFactory(InformationStorage informationStorage) {
		this.informationStorage = informationStorage;
	}

	// -------------------- Functions

	// TODO: UML NON STATIC PARAMS
	public PlayerUnit createSiegeTank(Unit unit) {
		return new PlayerUnitTerran_SiegeTank(unit, this.informationStorage);
	}

	// TODO: UML NON STATIC PARAMS
	public PlayerUnit createSiegeTankSiegeMode(Unit unit) {
		return new PlayerUnitTerran_SiegeTank_SiegeMode(unit, this.informationStorage);
	}

	// TODO: UML NON STATIC PARAMS
	public PlayerUnit createMarine(Unit unit) {
		return new PlayerUnitTerran_Marine(unit, this.informationStorage);
	}

	// TODO: UML NON STATIC PARAMS
	public PlayerUnit createFirebat(Unit unit) {
		return new PlayerUnitTerran_Firebat(unit, this.informationStorage);
	}

	// TODO: UML NON STATIC PARAMS
	public PlayerUnit createMedic(Unit unit) {
		return new PlayerUnitTerran_Medic(unit, this.informationStorage);
	}

	// TODO: UML NON STATIC PARAMS
	public PlayerUnit createVulture(Unit unit) {
		return new PlayerUnitTerran_Vulture(unit, this.informationStorage);
	}

	// TODO: UML NON STATIC PARAMS
	public PlayerUnit createSCV(Unit unit) {
		return new PlayerUnitTerran_SCV(unit, this.informationStorage, this.workerManagerResourceSpotAllocation,
				this.workerManagerConstructionJobDistribution);
	}
}
