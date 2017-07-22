package buildingOrderModule.scoringDirector;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateUnits_Support.java --- A GameState focused on support Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Support extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, GameStateCurrentInformation currenInformation) {
		
		// TODO: WIP REMOVE
		System.out.println("GameState SupportUnits: " + scoringDirector.defineFixedScoreUnitsSupport());
		
		return scoringDirector.defineFixedScoreUnitsSupport();
	}

}
