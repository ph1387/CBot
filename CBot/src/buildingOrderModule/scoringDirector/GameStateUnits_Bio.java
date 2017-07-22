package buildingOrderModule.scoringDirector;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateUnits_Bio.java --- A GameState focused on bio Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Bio extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, GameStateCurrentInformation currenInformation) {
		
		// TODO: WIP REMOVE
		System.out.println("GameState BioUnits: " + scoringDirector.defineFixedScoreUnitsBio());
		
		return scoringDirector.defineFixedScoreUnitsBio();
	}

}
