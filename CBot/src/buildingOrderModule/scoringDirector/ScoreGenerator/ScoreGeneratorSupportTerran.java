package buildingOrderModule.scoringDirector.ScoreGenerator;

import java.util.ArrayList;
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

	public ScoreGeneratorSupportTerran(BuildActionManager manager) {
		super(manager);
	}

	// -------------------- Functions

	// TODO: WIP ADD
	@Override
	protected List<UnitType> defineNumeratorUnitTypes() {
		return new ArrayList<>();
	}

	// TODO: WIP ADD
	@Override
	protected List<UnitType> defineDenominatorUnitTypes() {
		return new ArrayList<>();
	}

	// TODO: WIP ADD
	@Override
	protected int defineDenominatorMultiplier() {
		return 1;
	}

}
