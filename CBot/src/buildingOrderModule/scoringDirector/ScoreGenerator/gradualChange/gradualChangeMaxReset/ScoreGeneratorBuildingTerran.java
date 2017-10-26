package buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeMaxReset;

import java.util.Arrays;
import java.util.List;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import bwapi.UnitType;

/**
 * ScoreGeneratorBuildingTerran.java --- A {@link ScoreGenerator} focused on
 * buildings. Increases it's score until the number of buildings that are
 * defined as building types changes.
 * 
 * @author P H - 19.09.2017
 *
 */
public class ScoreGeneratorBuildingTerran extends ScoreGeneratorBuilding {

	private static double DefaultRate = 0.1;
	private static double DefaultFrameDiff = 200;
	private static double DefaultResetValue = 0.;

	// The UnitTypes of the buildings that are counted. If the number of them
	// changes, the score is reset.
	// Note:
	// Addons are not counted! -> Make sure that the addons DO NOT add the
	// building GameState to themselves!
	// Supply_Depots are not added since they are constructed separately!
	private static List<UnitType> BuildingTypes = Arrays.asList(new UnitType[] { UnitType.Terran_Academy,
			UnitType.Terran_Armory, UnitType.Terran_Barracks, UnitType.Terran_Bunker, UnitType.Terran_Command_Center,
			UnitType.Terran_Engineering_Bay, UnitType.Terran_Factory, UnitType.Terran_Missile_Turret,
			UnitType.Terran_Refinery, UnitType.Terran_Science_Facility, UnitType.Terran_Starport });

	public ScoreGeneratorBuildingTerran(BuildActionManager manager) {
		super(manager, DefaultRate, DefaultFrameDiff, DefaultResetValue, BuildingTypes);
	}

	// -------------------- Functions

}
