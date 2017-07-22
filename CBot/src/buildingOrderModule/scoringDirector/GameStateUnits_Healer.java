package buildingOrderModule.scoringDirector;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateUnits_Healer.java --- A GameState focused on healing Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Healer extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, GameStateCurrentInformation currenInformation) {
		
		// TODO: WIP REMOVE
		System.out.println("GameState HealerUnits: " + scoringDirector.defineFixedScoreUnitsHealer());
		
		return scoringDirector.defineFixedScoreUnitsHealer();
	}

}
