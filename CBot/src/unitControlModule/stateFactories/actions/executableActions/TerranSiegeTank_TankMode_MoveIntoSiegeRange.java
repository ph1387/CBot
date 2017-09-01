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
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_SiegeTank;

// TODO: UML ADD
/**
 * TerranSiegeTank_TankMode_MoveIntoSiegeRange.java --- Action for a
 * {@link PlayerUnitTerran_SiegeTank} to move into bombard / siege range of a
 * target. This action is added due to the massive damage and range increase of
 * the sieged attack of the Terran_Siege_Tanks.
 * 
 * @author P H - 01.09.2017
 *
 */
public class TerranSiegeTank_TankMode_MoveIntoSiegeRange extends BaseAction {

	private static final int TURN_RADIUS = 10;

	// The total distance the move Vector will cover.
	private double totalMoveDistance = 128;
	// The distance at which a new Position is going to be generated.
	private int minPositionDistance = 32;
	// Temporary storage of a generated Position.
	private Position generatedMovePosition = null;

	/**
	 * @param target
	 *            type: Unit
	 */
	public TerranSiegeTank_TankMode_MoveIntoSiegeRange(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "inSiegeRange", true));
		this.addPrecondition(new GoapState(0, "canMove", true));
		this.addPrecondition(new GoapState(0, "isSieged", false));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		Position position;

		// Move either towards or away from the target based on the distance
		// towards it. The goal is to move into the siege range:
		// Distance too far -> Move towards the enemy!
		if (((PlayerUnit) goapUnit).getUnit().getDistance((Unit) this.target) > PlayerUnitTerran_SiegeTank
				.getMaxSiegeRange()) {
			position = ((Unit) this.target).getPosition();
		}
		// Distance too short -> Move away from it!
		else {
			// Check if the Unit is near a previously generated Position.
			if (this.generatedMovePosition != null
					&& ((PlayerUnit) goapUnit).isNearPosition(this.generatedMovePosition, this.minPositionDistance)) {
				this.generatedMovePosition = null;
			}

			// Generate a new Position to move to if necessary.
			if (this.generatedMovePosition == null) {
				this.generatedMovePosition = this.generateNewMovePosition(goapUnit);
			}

			position = this.generatedMovePosition;
		}

		return position != null && ((PlayerUnit) goapUnit).getUnit().move(position);
	}

	// TODO: UML ADD
	/**
	 * Function for generating a new Position for the Terran_Siege_Tank to move
	 * to. This function utilizes the SteeringFactory since no Positions are
	 * allowed that are not in the map's boundaries.
	 * 
	 * @param goapUnit
	 *            the executing Unit.
	 * @return a new Position for the Unit to move to that is inside the map's
	 *         boundaries.
	 */
	private Position generateNewMovePosition(IGoapUnit goapUnit) {
		// Generate the Vectors necessary for the SteeringFactory to
		// work with.
		Position playerUnitPosition = ((PlayerUnit) goapUnit).getUnit().getPosition();
		Vector vecEU = new Vector(((Unit) this.target).getPosition(), playerUnitPosition);
		Vector vecUTP = new Vector(playerUnitPosition.getX(), playerUnitPosition.getY(), vecEU.getDirX(),
				vecEU.getDirY());

		// Generate the rest of the needed information.
		Pair<Region, Polygon> matchingRegionPolygonPair = findBoundariesPositionIsIn(
				((PlayerUnit) goapUnit).getUnit().getPosition());
		Chokepoint nearestChoke = BWTA.getNearestChokepoint(((PlayerUnit) goapUnit).getUnit().getPosition());

		// Generate a move Vector from the gathered information using
		// the Vector to the target Position as direction.
		Vector transformedVector = SteeringFactory.transformSteeringVector(goapUnit, vecUTP,
				matchingRegionPolygonPair.second, nearestChoke, this.totalMoveDistance, TURN_RADIUS);

		return new Position(transformedVector.getX() + (int) transformedVector.getDirX(),
				transformedVector.getY() + (int) transformedVector.getDirY());
	}

	@Override
	protected void resetSpecific() {

	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return this.target != null && !((PlayerUnit) goapUnit).getUnit().isSieged()
				&& ((PlayerUnit) goapUnit).getUnit().canMove();
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 1.f;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		return ((PlayerUnitTerran_SiegeTank) goapUnit).isInSiegeRange((Unit) this.target);
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

	@Override
	public int defineMaxGroupSize() {
		return 0;
	}

	@Override
	public int defineMaxLeaderTileDistance() {
		return 0;
	}

}
