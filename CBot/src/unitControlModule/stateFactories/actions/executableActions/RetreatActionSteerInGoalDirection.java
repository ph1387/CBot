package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Pair;
import bwapi.Position;
import bwapiMath.Polygon;
import bwapiMath.Vector;
import bwta.BWTA;
import bwta.Chokepoint;
import bwta.Region;
import javaGOAP.IGoapUnit;
import unitControlModule.stateFactories.actions.executableActions.steering.SteeringFactory;
import unitControlModule.stateFactories.actions.executableActions.steering.SteeringOperation;
import unitControlModule.stateFactories.actions.executableActions.steering.SteeringOperationChokePoints;
import unitControlModule.stateFactories.actions.executableActions.steering.SteeringOperationEnemiesInConfidenceRange;
import unitControlModule.stateFactories.actions.executableActions.steering.SteeringOperationStartingLocation;
import unitControlModule.stateFactories.actions.executableActions.steering.SteeringOperationStrongestPlayerArea;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * RetreatActionSteerInGoalDirection.java --- A Retreat Action with which a
 * PlayerUnit (!) moves away from an enemy. This Action's retreat Path has a
 * fixed length and is turned around the Unit until not collision with the map's
 * boundaries are detected. ChokePoints are ignored in this calculation. <br>
 * <b>Notice:</b> <br>
 * The length of the retreat path being taken has to be greater than the
 * DIST_TO_GATHERING_POINT from the superclass of this Action since this
 * determines when the Action is finished!
 * 
 * @author P H - 05.06.2017
 *
 */
public class RetreatActionSteerInGoalDirection extends RetreatActionGeneralSuperclass {

	private static final double TOTAL_RETREAT_DISTANCE = 96;
	private static final int TURN_RADIUS = 10;

	// Different influence sources for Vector calculations. Higher numbers
	// indicate a larger impact in the specific sector.
	private static final double INFLUENCE_INITIAL = 1.2;
	private static final double INFLUENCE_CHOKEPOINT = 5.7;
	private static final double INFLUENCE_ENEMIES = 0.9;
	private static final double INFLUENCE_BASE = 0.3;
	private static final double INFLUENCE_COMPANIONS = 1.2;

	private SteeringOperation steeringChokePoints, steeringEnemiesInConfidenceRange, steeringStartingLocation,
			steeringStrongestPlayerArea;

	/**
	 * @param target
	 *            type: Unit
	 */
	public RetreatActionSteerInGoalDirection(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		boolean precondtionsMet = false;

		// Instantiate the different SteeringOperations that are being used.
		if (this.steeringChokePoints == null) {
			this.instantiateSteeringOperations(goapUnit);
		}

		try {
			// Position missing -> Action not performed yet.
			if (this.retreatPosition == null) {
				Chokepoint nearestChoke = BWTA.getNearestChokepoint(((PlayerUnit) goapUnit).getUnit().getPosition());
				Pair<Region, Polygon> matchingRegionPolygonPair = findBoundariesPositionIsIn(
						((PlayerUnit) goapUnit).getUnit().getPosition());
				Polygon currentPolygon = matchingRegionPolygonPair.second;

				// Use a generalized Vector which combines all direction-Vectors
				// from all sources influencing the Unit. This generalized
				// Vector is the retreat Vector emerging from the Unit in
				// regards to the closest enemy Unit in it's confidence range.
				Vector generalizedTargetVector = this.vecUTP.clone();
				generalizedTargetVector.normalize();
				generalizedTargetVector.setDirX(generalizedTargetVector.getDirX() * INFLUENCE_INITIAL);
				generalizedTargetVector.setDirY(generalizedTargetVector.getDirY() * INFLUENCE_INITIAL);

				// Update the direction of the generalized Vector based on
				// various influences.
				((SteeringOperationChokePoints) this.steeringChokePoints)
						.setPolygonPairUnitIsIn(matchingRegionPolygonPair);
				this.steeringChokePoints.applySteeringForce(generalizedTargetVector, INFLUENCE_CHOKEPOINT);
				this.steeringEnemiesInConfidenceRange.applySteeringForce(generalizedTargetVector, INFLUENCE_ENEMIES);
				this.steeringStartingLocation.applySteeringForce(generalizedTargetVector, INFLUENCE_BASE);
				this.steeringStrongestPlayerArea.applySteeringForce(generalizedTargetVector, INFLUENCE_COMPANIONS);

				// Use the generalized Vector to find a valid retreat Position
				// using the previously generalized Vector as main steering
				// direction.
				Vector possibleRetreatVector = SteeringFactory.transformSteeringVector(goapUnit,
						generalizedTargetVector, currentPolygon, nearestChoke, TOTAL_RETREAT_DISTANCE, TURN_RADIUS);

				// Use the Vector's end-Position as retreat-Position.
				if (possibleRetreatVector != null) {
					this.generatedTempRetreatPosition = new Position(
							possibleRetreatVector.getX() + (int) (possibleRetreatVector.getDirX()),
							possibleRetreatVector.getY() + (int) (possibleRetreatVector.getDirY()));

					precondtionsMet = true;
				}
			}
			// Position known -> Action performed once.
			else {
				precondtionsMet = ((PlayerUnit) goapUnit).getUnit().hasPath(this.retreatPosition);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return precondtionsMet;
	}

	/**
	 * Function used for instantiating all SteertingOperations that are being
	 * used by the Action itself.
	 * 
	 * @param goapUnit
	 *            the Unit that is executing the Action.
	 */
	private void instantiateSteeringOperations(IGoapUnit goapUnit) {
		this.steeringChokePoints = new SteeringOperationChokePoints(goapUnit);
		this.steeringEnemiesInConfidenceRange = new SteeringOperationEnemiesInConfidenceRange(goapUnit);
		this.steeringStartingLocation = new SteeringOperationStartingLocation(goapUnit);
		this.steeringStrongestPlayerArea = new SteeringOperationStrongestPlayerArea(goapUnit);
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return (float) TOTAL_RETREAT_DISTANCE;
	}

}
