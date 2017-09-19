package buildingOrderModule.scoringDirector.ScoreGenerator;

import java.util.Collection;
import java.util.List;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import bwapi.UnitType;

/**
 * ScoreGeneratorProportionUnitTypes.java --- A {@link ScoreGenerator} based on
 * returning a score based on the proportion of different amounts of Units whose
 * UnitTypes are defined in a subclass.
 * 
 * @author P H - 16.09.2017
 *
 */
public abstract class ScoreGeneratorProportionUnitTypes extends ScoreGeneratorProportion {

	private static double DefaultScore = 0.;

	public ScoreGeneratorProportionUnitTypes(BuildActionManager manager) {
		super(manager, DefaultScore);
	}

	public ScoreGeneratorProportionUnitTypes(BuildActionManager manager, double defaultScore) {
		super(manager, defaultScore);
	}

	// -------------------- Functions

	@Override
	protected boolean canGenerateScore() {
		return true;
	}

	@Override
	protected int defineNumerator() {
		return this.generateUnitTypeSum(this.defineNumeratorUnitTypes());
	}

	/**
	 * Function for defining the UnitTypes that are used as numerators in the
	 * score generation. The number of current accessible Units of the defined
	 * types are summed up and utilized (Units that profit from the GameState).
	 * 
	 * @return a List defining the UnitTypes whose current ingame counts are
	 *         used as numerator in the score generation.
	 */
	protected abstract List<UnitType> defineNumeratorUnitTypes();

	@Override
	protected int defineDenominator() {
		return Math.max(this.generateUnitTypeSum(this.defineDenominatorUnitTypes()), 1)
				* Math.max(this.defineDenominatorMultiplier(), 1);
	}

	/**
	 * Function for defining the UnitTypes that are used as denominators in the
	 * score generation. The number of current accessible Units of the defined
	 * types are summed up, multiplied by a value and utilized (Units that
	 * provide profit).
	 * 
	 * @return a List defining the UnitTypes whose current ingame counts are
	 *         used as denominator in the score generation.
	 */
	protected abstract List<UnitType> defineDenominatorUnitTypes();

	/**
	 * Function for defining a multiplier for the generated denominator. This is
	 * necessary since some classes require the denominator to be small. </br>
	 * I.e. support {@link ScoreGenerator}s must ensure that not only support
	 * Units are trained. Moreover there should only be a support Unit for each
	 * 8.-9. Unit. Therefore the denominator must be increased arbitrarily to
	 * ensure this. I.e. eight marines represent a numerator of eight. One is
	 * enough for them to be properly supported. Without this function the
	 * generated score would be 8 since 8/1=8 resulting in a mass training of
	 * medics. (N:=8) With this function the score is 1 since 8/(N*1)=1
	 * therefore drastically decreasing it and the chance of a medic being
	 * trained. The next medic decreases this score even further: 8/(N*2)=0.5
	 * </br>
	 * </br>
	 * In Short:</br>
	 * <b>The higher the number, the lower the score.</b>
	 * 
	 * @return a multiplier that the denominator (Number of Units whose
	 *         UnitTypes are defined as denominators) is multiplied with.
	 */
	protected abstract int defineDenominatorMultiplier();

	/**
	 * Function for summing up the number of Units that have a UnitType that
	 * matches one in the given Collection of UnitTypes.
	 * 
	 * @param collection
	 *            the collection of UnitTypes whose Unit counts will be summed
	 *            u.p.
	 * @return the number of Units in the current game that match a UnitType in
	 *         the provided Collection of UnitTypes.
	 */
	private int generateUnitTypeSum(Collection<UnitType> collection) {
		int unitTypeSum = 0;

		for (UnitType unitType : collection) {
			unitTypeSum += this.manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
					.getOrDefault(unitType, 0);
		}

		return unitTypeSum;
	}

}
