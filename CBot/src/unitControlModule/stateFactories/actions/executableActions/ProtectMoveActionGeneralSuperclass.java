package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Pair;
import bwapi.Position;
import bwapi.Unit;
import bwapiMath.Polygon;
import bwapiMath.Vector;
import bwta.BWTA;
import bwta.Chokepoint;
import bwta.Region;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.stateFactories.actions.executableActions.steering.SteeringFactory;
import unitControlModule.stateFactories.actions.executableActions.steering.SteeringOperation;
import unitControlModule.stateFactories.actions.executableActions.steering.SteeringOperationEnemiesInConfidenceRange;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * ProtectMoveActionGeneralSuperclass.java --- Superclass for all Actions
 * involving customized protected steering towards certain Units. These kind of
 * Actions are necessary when enemies are expected near the target or on the way
 * towards it. Therefore the normal "IsInRange" and "RequiresInRange" can not be
 * used since they would lead towards the Unit's certain death.
 * 
 * @author P H - 01.07.2017
 *
 */
public abstract class ProtectMoveActionGeneralSuperclass extends BaseAction implements SteeringVectorGenerator {

	private static final int TURN_RADIUS = 10;
	private static final double INFLUENCE_INITIAL = 5.0;
	private static final double INFLUENCE_ENEMIES = 1.0;

	// The total distance the Unit moves in one go.
	protected double totalMoveDistance = 128;
	// The distance at which the isDone function returns true.
	protected int minPixelDistanceToTarget = 32;
	private SteeringOperation steeringEnemiesInConfidenceRange;

	// The distance at which a new moveEndPosition will be generated.
	private double moveEndPositionMinDistance = 20;
	// The generated Position at the end of the Vector.
	private Position moveEndPosition = null;
	private Position moveEndPositionPrev = null;

	/**
	 * @param target
	 *            type: Unit
	 */
	public ProtectMoveActionGeneralSuperclass(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "isNearSupportableUnit", true));
		this.addPrecondition(new GoapState(0, "isNearSupportableUnit", false));
		this.addPrecondition(new GoapState(0, "canMove", true));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success;

		// Finish the one single move to a generated Position.
		if (this.moveEndPosition != null && ((PlayerUnit) this.currentlyExecutingUnit).getUnit()
				.getDistance(this.moveEndPosition) <= this.moveEndPositionMinDistance) {
			this.moveEndPosition = null;
		}

		// Generate a new Position to move to.
		if (this.moveEndPosition == null) {
			Pair<Region, Polygon> matchingRegionPolygonPair = findBoundariesPositionIsIn(
					((PlayerUnit) goapUnit).getUnit().getPosition());
			Chokepoint nearestChoke = BWTA.getNearestChokepoint(((PlayerUnit) goapUnit).getUnit().getPosition());
			Vector vecToTarget = this.generateGeneralizedRetreatVector(goapUnit, matchingRegionPolygonPair);

			// Use the generalized Vector to find a valid retreat Position
			// using the previously generalized Vector as main steering
			// direction.
			Vector transformedVector = SteeringFactory.transformSteeringVector(goapUnit, vecToTarget,
					matchingRegionPolygonPair.second, nearestChoke, this.totalMoveDistance, TURN_RADIUS);
			this.moveEndPosition = new Position(transformedVector.getX() + (int) transformedVector.getDirX(),
					transformedVector.getY() + (int) transformedVector.getDirY());
		}

		// Only execute the move command once.
		if (this.moveEndPosition != null && this.moveEndPosition != this.moveEndPositionPrev) {
			success = ((PlayerUnit) goapUnit).getUnit().move(this.moveEndPosition);

			this.moveEndPositionPrev = this.moveEndPosition;
		} else {
			success = ((PlayerUnit) goapUnit).getUnit().isMoving();
		}

		return success;
	}

	/**
	 * Use a generalized Vector which combines all direction-Vectors from all
	 * sources influencing the Unit. This generalized Vector is the retreat
	 * Vector emerging from the Unit in regards to the closest enemy Unit in
	 * it's confidence range.
	 * 
	 * @param goapUnit
	 *            the executing Unit.
	 * @param regionPolygonPairUnitIsIn
	 *            the Pair of Region and Polygon the Unit is currently inside
	 *            of.
	 * @return a Vector containing all direction Vectors influencing this Unit.
	 */
	public Vector generateGeneralizedRetreatVector(IGoapUnit goapUnit,
			Pair<Region, Polygon> regionPolygonPairUnitIsIn) {
		Unit unit = ((PlayerUnit) goapUnit).getUnit();
		Unit targetUnit = (Unit) this.target;
		Vector vecToTarget = new Vector(unit.getPosition().getX(), unit.getPosition().getY(),
				targetUnit.getPosition().getX() - unit.getPosition().getX(),
				targetUnit.getPosition().getY() - unit.getPosition().getY());

		// Configure the initial Vector.
		vecToTarget.normalize();
		vecToTarget.setDirX(vecToTarget.getDirX() * INFLUENCE_INITIAL);
		vecToTarget.setDirY(vecToTarget.getDirY() * INFLUENCE_INITIAL);

		// Update the direction of the generalized Vector based on
		// various influences.
		this.steeringEnemiesInConfidenceRange.applySteeringForce(vecToTarget, INFLUENCE_ENEMIES);

		return vecToTarget;
	}

	@Override
	protected void resetSpecific() {
		this.moveEndPosition = null;
		this.moveEndPositionPrev = null;
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		// Instantiate the SteeringOperation being used.
		if (this.steeringEnemiesInConfidenceRange == null) {
			this.steeringEnemiesInConfidenceRange = new SteeringOperationEnemiesInConfidenceRange(goapUnit);
		}

		return this.target != null
				&& ((PlayerUnit) goapUnit).getUnit().getDistance((Unit) this.target) > this.minPixelDistanceToTarget;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return (float) this.totalMoveDistance;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		return this.target == null
				|| ((PlayerUnit) goapUnit).getUnit().getDistance((Unit) this.target) <= this.minPixelDistanceToTarget;
	}

	@Override
	protected boolean isInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected boolean requiresInRange(IGoapUnit goapUnit) {
		return false;
	}

	// -------------------- Group

	@Override
	public boolean canPerformGrouped() {
		return false;
	}

	@Override
	public boolean performGrouped(IGoapUnit groupLeader, IGoapUnit groupMember) {
		return false;
	}
	
	// TODO: UML ADD
	@Override
	public int defineMaxGroupSize() {
		return 0;
	}
		
}
