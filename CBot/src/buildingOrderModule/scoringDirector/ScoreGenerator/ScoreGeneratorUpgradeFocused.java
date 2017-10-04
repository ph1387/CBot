package buildingOrderModule.scoringDirector.ScoreGenerator;

import java.util.HashMap;
import java.util.function.BiConsumer;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.ScoreGeneratorGradualChangeMaxReset;
import buildingOrderModule.scoringDirector.gameState.GameState;
import bwapi.UpgradeType;

/**
 * ScoreGeneratorUpgradeFocused.java --- A {@link ScoreGenerator} focused on
 * upgrades. Increases it's score until the number of upgrades changes or no
 * more upgrades can be applied.
 * 
 * @author P H - 16.09.2017
 *
 */
public class ScoreGeneratorUpgradeFocused extends ScoreGeneratorGradualChangeMaxReset {

	private static double DefaultRate = 0.1;
	private static double DefaultFrameDiff = 200;
	private static double DefaultResetValue = 0.;

	// The number of upgrades performed by the Bot.
	private int upgradeCountPrev = 0;
	// Flag signaling if all upgrades are finished (No more upgrades can be
	// performed and therefore the score does not need to be changed).
	private boolean upgradesFinished = false;

	public ScoreGeneratorUpgradeFocused(BuildActionManager manager) {
		super(manager, DefaultRate, DefaultFrameDiff, DefaultResetValue);
	}

	// -------------------- Functions

	@Override
	protected boolean shouldReset(GameState gameState) {
		int upgradeCountCurrent = this
				.extractTotalNumberOfUpgrades(this.manager.getCurrentGameInformation().getCurrentUpgrades());
		int upgradeCountMax = this.extractTotalNumberOfUpgrades(this.manager.getDesiredUpgrades());
		boolean reset = false;

		// Reset the score after the previously stored number of upgrades
		// does not match the current number of upgrades. This means that a
		// new upgrade has been performed and therefore the Bot does not
		// immediately need to do another one.
		if (upgradeCountCurrent != this.upgradeCountPrev) {
			this.upgradeCountPrev = upgradeCountCurrent;
			reset = true;
		}

		// Check if all possible upgrades have been performed.
		if (upgradeCountCurrent == upgradeCountMax && !this.upgradesFinished) {
			this.upgradesFinished = true;
		}

		return reset;
	}

	/**
	 * Function for extracting the total number of Upgrades a HashMap contains.
	 * This function sums up the stored Integers and returns them.
	 * 
	 * @param hashMap
	 *            the HashMap whose Upgrades are being counted.
	 * @return the sum of all stored Integers of the provided HashMap.
	 */
	private int extractTotalNumberOfUpgrades(HashMap<UpgradeType, Integer> hashMap) {
		// Wrapper needed since the BiConsumer can only operate on final types.
		class CountWrapper {
			public int count = 0;
		}

		final CountWrapper counter = new CountWrapper();

		hashMap.forEach(new BiConsumer<UpgradeType, Integer>() {

			@Override
			public void accept(UpgradeType upgradeType, Integer integer) {
				counter.count += integer;
			}
		});

		return counter.count;
	}

	@Override
	protected boolean isThresholdReached(double score) {
		// The threshold is never reached. The rate is constantly applied to the
		// score and therefore the Bot is bound to upgrade things eventually
		// until all possible upgrades haven been performed by the Bot.
		return this.upgradesFinished;
	}

}
