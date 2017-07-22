package buildingOrderModule.scoringDirector;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateUnits_Combat.java --- A GameState focused on training combat Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Combat extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, GameStateCurrentInformation currenInformation) {
		double score = scoringDirector.defineDesiredCombatUnitsPercent()
				- (currenInformation.getCurrentCombatUnitsPercent()
						- scoringDirector.defineDesiredCombatUnitsPercent());

		// TODO: WIP REMOVE
		System.out.println("GameState CombatUnits: " + score);

		return score;
	}

}
