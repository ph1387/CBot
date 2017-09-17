package buildingOrderModule.scoringDirector.ScoreGenerator;

import java.util.Arrays;
import java.util.List;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import bwapi.UnitType;

/**
 * ScoreGeneratorSupportTerran.java --- A {@link ScoreGenerator} focusing on the
 * Terran support score. This includes mainly the Medic, Science_Vessel and
 * similar UnitTypes. The supportable Units vary.
 * 
 * @author P H - 16.09.2017
 *
 */
public class ScoreGeneratorSupportTerran extends ScoreGeneratorProportionUnitTypes {

	private int denominatorMultiplier = 20;

	public ScoreGeneratorSupportTerran(BuildActionManager manager) {
		super(manager);
	}

	// -------------------- Functions

	@Override
	protected List<UnitType> defineNumeratorUnitTypes() {
		// Nearly every UnitType profits from the existence of a
		// Terran_Science_Vessel.
		return Arrays.asList(new UnitType[] { UnitType.Terran_Battlecruiser, UnitType.Terran_Dropship,
				UnitType.Terran_Firebat, UnitType.Terran_Ghost, UnitType.Terran_Goliath, UnitType.Terran_Marine,
				UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Siege_Tank_Siege_Mode, UnitType.Terran_Valkyrie,
				UnitType.Terran_Vulture, UnitType.Terran_Wraith });
	}

	@Override
	protected List<UnitType> defineDenominatorUnitTypes() {
		// Medics are ignored due to them being bio Unit specific. They use the
		// healer ScoreGenerator.
		return Arrays.asList(new UnitType[] { UnitType.Terran_Science_Vessel });
	}

	@Override
	protected int defineDenominatorMultiplier() {
		return this.denominatorMultiplier;
	}

}
