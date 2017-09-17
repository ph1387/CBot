package buildingOrderModule.scoringDirector.ScoreGenerator;

import java.util.ArrayList;
import java.util.List;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import bwapi.UnitType;

/**
 * ScoreGeneratorHealerTerran.java --- A {@link ScoreGenerator} focusing on the
 * Terran healer score. This includes mainly the Medic and the UnitTypes that
 * can be healed by it.
 * 
 * @author P H - 16.09.2017
 *
 */
public class ScoreGeneratorHealerTerran extends ScoreGeneratorProportionUnitTypes {

	public ScoreGeneratorHealerTerran(BuildActionManager manager) {
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
