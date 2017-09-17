package buildingOrderModule.scoringDirector.ScoreGenerator;

import java.util.Arrays;
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

	private int denominatorMultiplier = 8;

	public ScoreGeneratorHealerTerran(BuildActionManager manager) {
		super(manager);
	}

	// -------------------- Functions

	@Override
	protected List<UnitType> defineNumeratorUnitTypes() {
		// TODO: Possible Change: Add Ghosts.
		// SCVs and Medics are ignored due to them not being combat Units.
		return Arrays.asList(new UnitType[] { UnitType.Terran_Marine, UnitType.Terran_Firebat });
	}

	@Override
	protected List<UnitType> defineDenominatorUnitTypes() {
		return Arrays.asList(new UnitType[] { UnitType.Terran_Medic });
	}

	@Override
	protected int defineDenominatorMultiplier() {
		return this.denominatorMultiplier;
	}

}
