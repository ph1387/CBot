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
import unitControlModule.unitWrappers.PlayerUnitTerran_Vulture;

/**
 * TerranVulture_SpiderMines_RepositionEnemy.java --- Action for a
 * {@link PlayerUnitTerran_Vulture} to reposition itself according to the
 * enemies that are near it in order to be able to place a mine.
 * 
 * @author P H - 29.09.2017
 *
 */
public class TerranVulture_SpiderMines_RepositionEnemy extends BaseAction {

	private static final int TURN_RADIUS = 10;

	// The distance to the target Unit that is required to place a mine. If this
	// pixel distance is exceeded the "isDone" function returns true.
	private int minPixelDistanceToTarget = 192;
	// The distance to the generated Position at which a new one is being
	// generated.
	private int minPixelDistanceToGeneratedPosition = 32;
	private Position positionToMoveTo = null;

	/**
	 * @param target
	 *            type: Unit
	 */
	public TerranVulture_SpiderMines_RepositionEnemy(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "isAtSpiderMineLocation", true));
		this.addPrecondition(new GoapState(0, "canMove", true));
		this.addPrecondition(new GoapState(0, "enemyKnown", true));
		this.addPrecondition(new GoapState(0, "isAtSpiderMineLocation", false));
		this.addPrecondition(new GoapState(0, "canSpiderMineBePlaced", true));
		this.addPrecondition(new GoapState(0, "shouldSpiderMinesBePlaced", true));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		if (this.positionToMoveTo == null || ((PlayerUnit) goapUnit).isNearPosition(this.positionToMoveTo,
				this.minPixelDistanceToGeneratedPosition)) {
			this.positionToMoveTo = this.generateTempRetreatPosition(goapUnit);

			if (this.positionToMoveTo != null) {
				success &= ((PlayerUnit) goapUnit).getUnit().move(this.positionToMoveTo);
			}
		}
		return success;
	}

	/**
	 * Function used for generating a Position to which the Unit can retreat to.
	 * Is called when a new temporary retreat Position is needed.
	 * 
	 * @see RetreatActionGeneralSuperclass#generatedTempRetreatPosition(IGoapUnit
	 *      goapUnit)
	 * @param goapUnit
	 *            the Unit that will perform the Action.
	 * @return a Position to which the performing Unit can retreat to.
	 */
	protected Position generateTempRetreatPosition(IGoapUnit goapUnit) {
		Chokepoint nearestChoke = BWTA.getNearestChokepoint(((PlayerUnit) goapUnit).getUnit().getPosition());
		Pair<Region, Polygon> matchingRegionPolygonPair = findBoundariesPositionIsIn(
				((PlayerUnit) goapUnit).getUnit().getPosition());
		Polygon currentPolygon = matchingRegionPolygonPair.second;
		Vector generalizedTargetVector = this.generateRetreatVector(goapUnit);
		Position generatedPosition = null;

		// Use the generalized Vector to find a valid retreat Position
		// using the previously generalized Vector as main steering
		// direction.
		Vector possibleRetreatVector = SteeringFactory.transformSteeringVector(goapUnit, generalizedTargetVector,
				currentPolygon, nearestChoke, this.minPixelDistanceToTarget, TURN_RADIUS);

		// Use the Vector's end-Position as retreat-Position.
		if (possibleRetreatVector != null) {
			generatedPosition = new Position(possibleRetreatVector.getX() + (int) (possibleRetreatVector.getDirX()),
					possibleRetreatVector.getY() + (int) (possibleRetreatVector.getDirY()));
		}

		return generatedPosition;
	}

	/**
	 * Function for generating a Vector emerging at the executing Unit with the
	 * direction and of the target Unit towards itself set to a fixed length.
	 * This Vector can be used for generating the retreat Position that the Unit
	 * has to move to in order to perform the action.
	 * 
	 * @param goapUnit
	 *            the executing Unit.
	 * @return a Vector emerging at the executing Unit with the direction and of
	 *         the target Unit towards itself set to a fixed length.
	 */
	private Vector generateRetreatVector(IGoapUnit goapUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;

		Vector incomingVector = new Vector(((Unit) this.target).getPosition(), playerUnit.getUnit().getPosition());
		Position endPosition = new Position(
				playerUnit.getUnit().getPosition().getX() + (int) (incomingVector.getDirX()),
				playerUnit.getUnit().getPosition().getY() + (int) (incomingVector.getDirY()));

		// Generate a Vector starting from the goapUnit itself with the
		// direction Vector from the enemy Unit to the goapUnit applied.
		return new Vector(playerUnit.getUnit().getPosition(), endPosition);
	}

	@Override
	protected void resetSpecific() {
		this.positionToMoveTo = null;
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return this.target != null;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 10.f;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		return this.target == null
				|| ((PlayerUnit) goapUnit).getUnit().getDistance((Unit) this.target) >= this.minPixelDistanceToTarget;
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
