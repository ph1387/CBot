package buildingOrderModule.scoringDirector.ScoreGenerator.specific;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.gameState.GameState;
import buildingOrderModule.scoringDirector.gameState.GameStateSpecific_Upgrade;
import bwapi.UpgradeType;

/**
 * ScoreGeneratorSpecificUpgrade.java --- A {@link ScoreGenerator} applying a
 * target specific rate to the score. This rate is based on the
 * {@link UpgradeType} that is associated with the {@link GameState}.
 * 
 * @author P H - 03.10.2017
 *
 */
public abstract class ScoreGeneratorSpecificUpgrade extends ScoreGeneratorSpecific {

	public ScoreGeneratorSpecificUpgrade(BuildActionManager manager) {
		super(manager);
	}

	// -------------------- Functions

	protected UpgradeType extractUpgradeType(GameState gameState) throws Exception {
		return ((GameStateSpecific_Upgrade) gameState).getSpecificUpgradeType();
	}

	// ------------------------------ Getter / Setter

}
