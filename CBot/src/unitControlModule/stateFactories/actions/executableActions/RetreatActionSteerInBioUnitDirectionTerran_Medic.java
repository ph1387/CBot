package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Pair;
import bwapi.Unit;
import bwapiMath.Polygon;
import bwapiMath.Vector;
import bwta.Region;
import javaGOAP.IGoapUnit;
import unitControlModule.stateFactories.actions.executableActions.steering.SteeringOperation;
import unitControlModule.stateFactories.actions.executableActions.steering.SteeringOperationSpecificUnit;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * RetreatActionSteerInBioUnitDirectionTerran_Medic.java --- A Retreat Action
 * with which a PlayerUnit (!) moves towards a friendly Bio-Unit like Firebats
 * or Marines while also taking enemies into account.
 * 
 * @author P H - 30.06.2017
 *
 */
public class RetreatActionSteerInBioUnitDirectionTerran_Medic extends RetreatActionSteerInRetreatVectorDirection {

	private class CustomSearchCondition implements PlayerUnitSearchCondition {

		@Override
		public boolean isConditionMet(PlayerUnit playerUnit, Unit testingUnit) {
			boolean success = false;

			switch (testingUnit.getType().toString()) {
			case "Terran_SCV ":
				success = true;
				break;
			case "Terran_Marine":
				success = true;
				break;
			case "Terran_Firebat":
				success = true;
				break;
			case "Terran_Medic":
				success = true;
				break;
			case "Terran_Ghost":
				success = true;
				break;
			}

			return success;
		}
	}

	private static final double INFLUENCE_BIO_UNIT = 6.5;

	private PlayerUnitSearchCondition condition = new CustomSearchCondition();
	private SteeringOperation steeringBioUnits;

	/**
	 * @param target
	 *            type: Unit
	 */
	public RetreatActionSteerInBioUnitDirectionTerran_Medic(Object target) {
		super(target);
	}

	// -------------------- Functions

	// TODO: UML ADD
	@Override
	protected Vector generateGeneralizedRetreatVector(IGoapUnit goapUnit,
			Pair<Region, Polygon> regionPolygonPairUnitIsIn) {
		Vector previouslyCalculatedVector = super.generateGeneralizedRetreatVector(goapUnit, regionPolygonPairUnitIsIn);
		Unit closestPossibleBioUnit = getClosestUnit(
				getPlayerUnitsInIncreasingRange((PlayerUnit) goapUnit, this.condition),
				((PlayerUnit) goapUnit).getUnit());

		if (closestPossibleBioUnit != null) {
			if (this.steeringBioUnits == null) {
				this.steeringBioUnits = new SteeringOperationSpecificUnit(goapUnit);
			}

			((SteeringOperationSpecificUnit) this.steeringBioUnits).setTargetUnit(closestPossibleBioUnit);
			this.steeringBioUnits.applySteeringForce(previouslyCalculatedVector, INFLUENCE_BIO_UNIT);
		}

		return previouslyCalculatedVector;
	}
}
