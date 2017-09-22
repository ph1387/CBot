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

	private InformationStorage informationStorage;

	private WorkerManagerResourceSpotAllocation workerManagerResourceSpotAllocation = CBot.getInstance()
			.getWorkerManagerResourceSpotAllocation();
	private WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution = CBot.getInstance()
			.getWorkerManagerConstructionJobDistribution();

	public PlayerUnitFactory(InformationStorage informationStorage) {
		this.informationStorage = informationStorage;
	}

	// -------------------- Functions

	public PlayerUnit createSiegeTank(Unit unit) {
		return new PlayerUnitTerran_SiegeTank(unit, this.informationStorage);
	}

	public PlayerUnit createSiegeTankSiegeMode(Unit unit) {
		return new PlayerUnitTerran_SiegeTank_SiegeMode(unit, this.informationStorage);
	}

	public PlayerUnit createMarine(Unit unit) {
		return new PlayerUnitTerran_Marine(unit, this.informationStorage);
	}

	public PlayerUnit createFirebat(Unit unit) {
		return new PlayerUnitTerran_Firebat(unit, this.informationStorage);
	}

	public PlayerUnit createMedic(Unit unit) {
		return new PlayerUnitTerran_Medic(unit, this.informationStorage);
	}

	public PlayerUnit createVulture(Unit unit) {
		return new PlayerUnitTerran_Vulture(unit, this.informationStorage);
	}

	public PlayerUnit createSCV(Unit unit) {
		return new PlayerUnitTerran_SCV(unit, this.informationStorage, this.workerManagerResourceSpotAllocation,
				this.workerManagerConstructionJobDistribution);
	}

	public PlayerUnit createWraith(Unit unit) {
		return new PlayerUnitTerran_Wraith(unit, this.informationStorage);
	}

	// TODO: UML ADD
	public PlayerUnit createGoliath(Unit unit) {
		return new PlayerUnitTerran_Goliath(unit, this.informationStorage);
	}
}
